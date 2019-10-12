package com.wiki.framework.mybatis.query.v2;


import com.wiki.framework.common.util.LambdaUtils;
import com.wiki.framework.common.util.PropertyUtil;
import com.wiki.framework.common.util.lambda.SFunction;
import com.wiki.framework.common.util.lambda.SerializedLambda;
import com.wiki.framework.mybatis.query.Joint;
import com.wiki.framework.mybatis.query.PropertySelector;
import com.wiki.framework.mybatis.query.Sort;
import com.wiki.framework.mybatis.query.SortProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Thomason
 * @version 1.0
 * @since 2017/6/15 17:26
 */
public final class Criteria {
	/**
	 * 对象名称
	 */
	private String entityName;
	/**
	 * 筛选条件
	 */
	private List<Condition> conditions = new ArrayList<>();
	/**
	 * 排序属性列
	 */
	private List<SortProperty> sortProperties;
	/**
	 * 属性选择器
	 */
	private PropertySelector selector;

	public static Criteria create() {
		return new Criteria();
	}

	public Criteria addCriterion(Condition... condition) {
		if (conditions == null) {
			conditions = new ArrayList<>();
		}
		if (condition != null) {
			for (Condition c : condition) {
				if (c.getOperator().equals(Operator.beginWith)
						|| c.getOperator().equals(Operator.notBeginWith)
						|| c.getOperator().equals(Operator.endWith)
						|| c.getOperator().equals(Operator.notEndWith)
						|| c.getOperator().equals(Operator.contains)
						|| c.getOperator().equals(Operator.notContains)
				) {
					String value = c.getValue();
					if (value != null) {
						c.setValue(escape(value));
					}
				}
				conditions.add(c);
			}
		}
		return this;
	}

	public Criteria clear() {
		this.conditions = new ArrayList<>();
		this.sortProperties = null;
		return this;
	}

	public Criteria addSortProperty(SortProperty... sortProperty) {
		if (sortProperty == null || sortProperty.length == 0) {
			return this;
		}
		if (this.sortProperties == null) {
			this.sortProperties = new ArrayList<>();
		}
		sortProperties.addAll(Arrays.asList(sortProperty));
		return this;
	}

	public <T> Criteria and(SFunction<T, ?> lambda, Operator operator, String value) {
		SerializedLambda resolved = LambdaUtils.resolve(lambda);
		String prop = PropertyUtil.methodToProperty(resolved.getImplMethodName());
		Condition condition = new Condition(prop, operator, value);
		this.addCriterion(condition);
		return this;
	}

	public Criteria and(String prop, Operator operator, String value) {
		return and(new String[]{prop}, operator, value);
	}

	public Criteria and(String[] props, Operator operator, String value) {
		if (props != null && props.length > 0) {
			for (String prop : props) {
				Condition condition = new Condition(prop, operator, value);
				this.addCriterion(condition);
			}
		}
		return this;
	}

	public <T> Criteria or(SFunction<T, ?> lambda, Operator operator, String value) {
		SerializedLambda resolved = LambdaUtils.resolve(lambda);
		String prop = PropertyUtil.methodToProperty(resolved.getImplMethodName());
		return or(new String[]{prop}, operator, value);
	}

	public Criteria or(String prop, Operator operator, String value) {
		return or(new String[]{prop}, operator, value);
	}

	public Criteria or(String[] props, Operator operator, String value) {
		if (props != null && props.length > 0) {
			Condition condition = new Condition();
			List<Condition> subConditions = new ArrayList<>();
			for (String prop : props) {
				Condition subCondition = new Condition(prop, operator, value);
				subCondition.setJoint(Joint.OR);
				subConditions.add(subCondition);
			}
			condition.setSubConditions(subConditions);
			this.addCriterion(condition);
		}
		return this;
	}

	public <T> Criteria asc(SFunction<T,?> ... lambdas) {
		if (lambdas != null && lambdas.length > 0) {
			for (SFunction<?,?> prop : lambdas) {
				this.addSortProperty(new SortProperty(PropertyUtil.methodToProperty(LambdaUtils.resolve(prop).getImplMethodName()), Sort.ASC));
			}
		}
		return this;
	}

	public Criteria asc(String... props) {
		if (props != null && props.length > 0) {
			for (String prop : props) {
				this.addSortProperty(new SortProperty(prop, Sort.ASC));
			}
		}
		return this;
	}

	public <T> Criteria des(SFunction<T,?> ... lambdas) {
		if (lambdas != null && lambdas.length > 0) {
			for (SFunction<?,?> prop : lambdas) {
				this.addSortProperty(new SortProperty(PropertyUtil.methodToProperty(LambdaUtils.resolve(prop).getImplMethodName()), Sort.DESC));
			}
		}
		return this;
	}

	public Criteria desc(String... props) {
		if (props != null && props.length > 0) {
			for (String prop : props) {
				this.addSortProperty(new SortProperty(prop, Sort.DESC));
			}
		}
		return this;
	}

	/**
	 * 对特殊字符进行转义
	 *
	 * @param input
	 * @return
	 */
	private String escape(String input) {
		if (input == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		char[] chars = input.toCharArray();
		for (char c : chars) {
			if (c == 37) {
				builder.append("\\%");
			} else if (c == 95) {
				builder.append("\\_");
			} else if (c == 91) {
				builder.append("\\[");
			} else if (c == 92) {
				builder.append("\\\\");
			} else if (c == 93) {
				builder.append("\\]");
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public List<SortProperty> getSortProperties() {
		return sortProperties;
	}

	public PropertySelector getSelector() {
		return selector;
	}

	public void setSelector(PropertySelector selector) {
		this.selector = selector;
	}
}
