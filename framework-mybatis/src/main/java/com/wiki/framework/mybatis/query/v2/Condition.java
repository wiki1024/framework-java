package com.wiki.framework.mybatis.query.v2;



import com.wiki.framework.common.util.LambdaUtils;
import com.wiki.framework.common.util.PropertyUtil;
import com.wiki.framework.common.util.lambda.SFunction;
import com.wiki.framework.mybatis.query.Joint;

import javax.persistence.criteria.JoinType;
import java.io.Serializable;
import java.util.List;

/**
 * 表单上的查询条件封装类
 * 封装表单的查询条件
 *
 * @author Thomason
 * Date: 11-4-22
 * Time: 下午2:16
 * @version 1.0
 */
public class Condition implements Serializable {
	/**
	 * 条件连接字符串 and or
	 */
	private Joint joint = Joint.AND;
	/**
	 * 查询主实体名称
	 */
	private String entityName;
	/**
	 * 实体对应的属性名称
	 */
	private String propertyName;
	/**
	 * 多个属性之间的连接方式
	 */
	private JoinType joinType = JoinType.INNER;
	/**
	 * 查询关系符号
	 */
	private Operator operator = Operator.equal;
	/**
	 * 条件的值
	 */
	private String value;
	/**
	 * 子条件
	 */
	private List<Condition> subConditions;

	public Condition() {
	}

	public Condition(String propertyName, String value) {
		this.propertyName = propertyName;
		this.value = value;
	}

	public Condition(String propertyName, Operator operator, String value) {
		this.propertyName = propertyName;
		this.operator = operator;
		this.value = value;
	}

	public <T> Condition(SFunction<T, ?> lambda, Operator operator, String value) {
		this(PropertyUtil.methodToProperty(LambdaUtils.resolve(lambda).getImplMethodName()), operator, value);
	}


	public boolean hasSubCondition() {
		return this.subConditions != null && this.subConditions.size() > 0;
	}

	public Joint getJoint() {
		return joint;
	}

	public void setJoint(Joint joint) {
		this.joint = joint;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Condition> getSubConditions() {
		return subConditions;
	}

	public void setSubConditions(List<Condition> subConditions) {
		this.subConditions = subConditions;
	}

	public JoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(JoinType joinType) {
		this.joinType = joinType;
	}
}
