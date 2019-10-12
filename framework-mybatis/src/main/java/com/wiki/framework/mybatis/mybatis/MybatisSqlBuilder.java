package com.wiki.framework.mybatis.mybatis;


import com.wiki.framework.common.util.DataType;
import com.wiki.framework.common.util.ReflectionUtils;
import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.mybatis.ORMapping;
import com.wiki.framework.mybatis.database.Column;
import com.wiki.framework.mybatis.database.Join;
import com.wiki.framework.mybatis.database.Table;
import com.wiki.framework.mybatis.po.CommonPO;
import com.wiki.framework.mybatis.query.Joint;
import com.wiki.framework.mybatis.query.PropertySelector;
import com.wiki.framework.mybatis.query.SortProperty;
import com.wiki.framework.mybatis.query.v2.Condition;
import com.wiki.framework.mybatis.query.v2.Criteria;
import com.wiki.framework.mybatis.query.v2.Operator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.JoinType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author thomason
 * todo remove delete and get
 * todo throw exception on empty criteria
 * * @version 1.0
 * @since 2017/8/2 下午3:50
 */
public class MybatisSqlBuilder {
	public static final String BLANK = " ";
	public final static String PATH_SEPARATOR = ".";
	public final String ROOT_TABLE_ALIAS_KEY = "_self";
	private Logger logger = LoggerFactory.getLogger(MybatisSqlBuilder.class);


	private void buildCondition(Map<String, Object> parameterMap, Map<String, String> tableAliasMap, AtomicInteger counter, SqlBuilder sql, Condition condition, Table table, Criteria criteria) {
		if (condition.hasSubCondition()) {
			andOr(condition.getJoint(), sql);
			sql.WHERE("( 1 = 1 ");
			for (Condition subCondition : condition.getSubConditions()) {
				buildCondition(parameterMap, tableAliasMap, counter, sql, subCondition, table, criteria);
			}
			sql.AND().WHERE(" 1 = 1 )");
		} else {
			buildSingleCondition(parameterMap, tableAliasMap, counter, sql, condition, table, criteria);
		}
	}

	private void buildSingleCondition(Map<String, Object> parameterMap, Map<String, String> tableAliasMap, AtomicInteger counter, SqlBuilder sql, Condition condition, Table table, Criteria criteria) {
		if (!match(condition)) {
			return;
		}

		String propertyName = condition.getPropertyName();
		Joint joint = condition.getJoint();
		String value = condition.getValue();
		Operator operator = condition.getOperator();
		String mapKey = StringUtils.replace(propertyName, ".", "_") + "Value";
		mapKey = genUniqueMapKey(mapKey, parameterMap);
		String alias = tableAliasMap.get(ROOT_TABLE_ALIAS_KEY);
		Column column = resolvePropertyCascade(tableAliasMap, table, alias, counter, sql, condition.getJoinType(), null, propertyName, criteria);
		if (column == null) {
			return;
		}
		String columnName = column.getSqlName();
		String jdbcType = column.getSqlTypeName();
		int dataType = DataType.getDataType(column.getJavaType());
		if (propertyName.contains(PATH_SEPARATOR)) {
			alias = tableAliasMap.get(propertyName.substring(0, propertyName.lastIndexOf(PATH_SEPARATOR)));
		}
		andOr(joint, sql);
		switch (operator) {
			case beginWith:
			case notBeginWith:
				sql.WHERE(alias + "." + columnName + BLANK + condition.getOperator().value() + BLANK + "#{map." + mapKey + ",jdbcType=" + jdbcType + "}");
				parameterMap.put(mapKey, value + "%");
				break;
			case endWith:
			case notEndWith:
				sql.WHERE(alias + "." + columnName + BLANK + condition.getOperator().value() + BLANK + "#{map." + mapKey + ",jdbcType=" + jdbcType + "}");
				parameterMap.put(mapKey, "%" + value);
				break;
			case contains:
			case notContains:
				sql.WHERE(alias + "." + columnName + BLANK + condition.getOperator().value() + BLANK + "#{map." + mapKey + ",jdbcType=" + jdbcType + "}");
				parameterMap.put(mapKey, "%" + value + "%");
				break;
			case between:
			case notBetween:
				String[] betweenValues = StringUtils.split(value, ",");
				String betweenKey1 = mapKey + "Value1";
				String betweenKey2 = mapKey + "Value2";
				sql.WHERE(alias + "." + columnName + BLANK + condition.getOperator().value() + BLANK + "#{map." + betweenKey1 + ",jdbcType=" + jdbcType + "}" + BLANK
						+ "AND" + BLANK + "#{map." + betweenKey2 + ",jdbcType=" + jdbcType + "}");
				parameterMap.put(betweenKey1, DataType.toType(betweenValues[0], DataType.DT_String, dataType));
				if (betweenValues.length > 1) {
					parameterMap.put(betweenKey2, DataType.toType(betweenValues[1], DataType.DT_String, dataType));
				} else {
					parameterMap.put(betweenKey2, null);
				}
				break;
			case blank:
			case notBlank:
			case isNull:
			case isNotNull:
				sql.WHERE(alias + "." + columnName + BLANK + condition.getOperator().value());
				break;
			case equal:
			case notEqual:
			case lessEqual:
			case lessThan:
			case greaterEqual:
			case greaterThan:
				sql.WHERE(alias + "." + columnName + BLANK + condition.getOperator().value() + BLANK + "#{map." + mapKey + ",jdbcType=" + jdbcType + "}");
				parameterMap.put(mapKey, DataType.toType(value, DataType.DT_String, dataType));
				break;
			case in:
			case notIn:
				String[] inValues = StringUtils.split(value, ",");
				StringBuilder builder = new StringBuilder();
				builder.append(alias).append(".").append(columnName).append(BLANK).append(condition.getOperator().value());
				builder.append(BLANK);
				builder.append("(");
				if (jdbcType.equals(JdbcType.VARCHAR.toString())) {
					for (String inValue : inValues) {
						builder.append("'");
						builder.append(StringUtil.escapeSql(inValue));
						builder.append("'");
						builder.append(",");
					}
					builder.deleteCharAt(builder.length() - 1);
				} else {
					for (String inValue : inValues) {
						builder.append(inValue);
						builder.append(",");
					}
					builder.deleteCharAt(builder.length() - 1);
				}
				builder.append(")");
				sql.WHERE(builder.toString());
				break;
			case custom:
				sql.WHERE(alias + "." + columnName + BLANK + value);
				break;
			default:
				break;
		}
	}

	private Column resolvePropertyCascade(Map<String, String> tableAliasMap, Table table, String parentTableAlias
			, AtomicInteger counter, SqlBuilder sql, JoinType joinType, String path, String propertyName, Criteria criteria) {
		//判断是否是扩展表
		if (table.isExtTable() && propertyName.startsWith("extMap.")) {
			return null;
		}
		PropertySelector selector = criteria.getSelector();
		if (propertyName.contains(PATH_SEPARATOR)) {
			int indexOf = propertyName.indexOf(PATH_SEPARATOR);
			String prefix = propertyName.substring(0, indexOf);
			String next = propertyName.substring(indexOf + 1);
			String key = path == null ? prefix : (path + "." + prefix);
			List<Join> joins = table.getJoins();
			if (CollectionUtils.isEmpty(joins)) {
				throw new RuntimeException("error propertyName -> " + key);
			}
			Join join = joins.stream().filter(j -> j.getFieldName().equals(prefix)).findAny().orElse(null);
			if (join == null) {
				throw new RuntimeException("error propertyName -> " + key);
			}

			Class targetEntity = join.getTargetEntity();
			Table targetTable = ORMapping.get(targetEntity);
			assert targetTable != null;
			String targetTableName = getTableNameByCriteria(targetTable, criteria);
			String alias = tableAliasMap.computeIfAbsent(key, (k) -> {
				StringBuilder builder = new StringBuilder();
				String targetAlias = createTableAlias(targetTableName, counter);
				builder.append(targetTableName).append(BLANK).append("AS").append(BLANK).append(targetAlias);
				builder.append(BLANK).append("ON").append(BLANK);
				join.getJoinColumns().forEach(joinColumn -> {
					builder.append(BLANK).append(parentTableAlias).append(".").append(joinColumn.getName()).append(BLANK);
					builder.append("=").append(BLANK).append(targetAlias).append(".").append(joinColumn.getReferencedColumnName());
					builder.append(BLANK).append("AND");
				});
//				builder.delete(builder.length() - 3, builder.length());
				//fixme 此处写死要加上isDeleted = 0 的条件，大部分情况下是正确的，但是极少数特殊情况不支持，可能会出错
				builder.append(BLANK).append(targetAlias).append(".is_deleted = 0").append(BLANK);
				switch (joinType) {
					case INNER:
						sql.INNER_JOIN(builder.toString());
						break;
					case LEFT:
						sql.LEFT_OUTER_JOIN(builder.toString());
						break;
					case RIGHT:
						sql.RIGHT_OUTER_JOIN(builder.toString());
						break;
					default:
						break;
				}
				//根据列选择器动态选择列 增加查询结果
				targetTable.getColumns().stream().filter(column -> {
					if (selector == null ||
							(CollectionUtils.isEmpty(selector.getIncludes()) && CollectionUtils.isEmpty(selector.getExcludes()))) {
						return true;
					}
					List<String> includes = selector.getIncludes();
					if (CollectionUtils.isNotEmpty(includes)) {
						return includes.contains(k + "." + column.getJavaName());
					} else if (CollectionUtils.isNotEmpty(selector.getExcludes())) {
						return !selector.getExcludes().contains(k + "." + column.getJavaName());
					}
					return false;
				}).forEach(column -> {
					sql.SELECT(targetAlias + "." + column.getSqlName() + BLANK + "AS" + BLANK + "\"" + k + "." + column.getJavaName() + "\"");
				});
				return targetAlias;
			});
			return resolvePropertyCascade(tableAliasMap, targetTable, alias, counter, sql, joinType, key, next, criteria);
		} else {
			//如果主表没有include  exclude就返回
//			if (!pathPrexs.contains(path) && StringUtil.isEmpty(path)){
//				return table.getColumns().stream().filter(k -> !k.getJavaName().contains(PATH_SEPARATOR)).findFirst().orElse(null);
//			}
			return table.getColumns().stream().filter(k -> k.getJavaName().equals(propertyName)).findFirst().orElse(null);
		}
	}

	private void andOr(Joint joint, SqlBuilder sql) {
		switch (joint) {
			case AND:
				sql.AND();
				break;
			case OR:
				sql.OR();
				break;
			default:
				break;
		}
	}

	private String createTableAlias(String tableName, AtomicInteger counter) {
		return "t" + counter.getAndIncrement();
	}

	private String genUniqueMapKey(String mapKey, Map<String, Object> map) {
		if (!map.containsKey(mapKey)) {
			return mapKey;
		}
		String newKey = mapKey + "_R";
		while (map.containsKey(newKey)) {
			newKey += "_R";
		}
		return newKey;
	}

	public void buildOrder(Map<String, String> tableAliasMap, AtomicInteger counter, SqlBuilder sql, Table table, Criteria criteria) {
		List<SortProperty> sortProperties = criteria.getSortProperties();
		if (CollectionUtils.isNotEmpty(sortProperties)) {
			for (SortProperty sortProperty : sortProperties) {
				String propertyName = sortProperty.getPropertyName();
				if (StringUtils.isBlank(propertyName)) {
					continue;
				}
				String rootAlias = tableAliasMap.get(ROOT_TABLE_ALIAS_KEY);
				//此处的joinType是瞎猜的，不作数
				Column column = resolvePropertyCascade(tableAliasMap, table, rootAlias, counter, sql, JoinType.LEFT, null, propertyName, criteria);
				if (column == null) {
					continue;
				}
				String propertyAlias = rootAlias;
				if (propertyName.contains(PATH_SEPARATOR)) {
					propertyAlias = tableAliasMap.get(propertyName.substring(0, propertyName.lastIndexOf(".")));
				}
				sql.ORDER_BY(propertyAlias + "." + column.getSqlName() + BLANK + sortProperty.getSort().name());
			}
		}
	}

	public boolean match(Condition condition) {
		String value = condition.getValue();
		Operator operator = condition.getOperator();
		if (operator == Operator.isNull
				|| operator == Operator.isNotNull
				|| operator == Operator.blank
				|| operator == Operator.notBlank) {
			return true;
		}
		if (StringUtils.isNotBlank(value)) {
			return true;
		}
		return false;
	}


	public String insert(Object po) {
		if (!(po instanceof CommonPO)) {
			return null;
		}
		CommonPO commonPO = (CommonPO) po;
		Class<?> poClass = po.getClass();
		Table table = ORMapping.get(poClass);
		if (table == null) {
			throw new RuntimeException("not a jpa standard class:" + poClass.getName());
		}
		StringBuilder builder = new StringBuilder();
		builder.append("insert into");
		builder.append(BLANK);
		builder.append(getTableNameByPO(table, commonPO));
		builder.append(BLANK).append("(");
		table.getColumns().stream().filter(Column::isInsertable).forEach(column -> {
			builder.append(BLANK).append(column.getSqlName()).append(",");
		});
		builder.deleteCharAt(builder.length() - 1);
		builder.append(")").append(BLANK).append("values(");
		table.getColumns().stream().filter(Column::isInsertable).forEach(column -> {
			builder.append(BLANK);
			builder.append("#{").append(column.getJavaName())
					.append(",jdbcType=")
					.append(column.getSqlTypeName()).append("}");
			builder.append(",");
		});
		builder.deleteCharAt(builder.length() - 1);
		builder.append(")");
		if (logger.isDebugEnabled()) {
			logger.debug(builder.toString());
		}
		return builder.toString();
	}

	public String batchInsert(@Param("list") List<Object> poList, @Param("map") Map<String, Object> map) {
		return BatchType.BatchInsert.name();
	}

	public String update(@Param("po") Object po, @Param("versionable") boolean versionable, @Param("columns") Set<String> columns) {
		if (!(po instanceof CommonPO)) {
			return null;
		}
		CommonPO commonPO = (CommonPO) po;
		if (commonPO.getId() == null) {
			throw new RuntimeException("id is null when update");
		}
		Class<?> poClass = po.getClass();
		Table table = ORMapping.get(poClass);
		if (table == null) {
			throw new RuntimeException("not a jpa standard class:" + poClass.getName());
		}
		StringBuilder builder = new StringBuilder();
		builder.append("update");
		builder.append(BLANK);
		builder.append(getTableNameById(table, ((CommonPO) po).getId()));
		builder.append(BLANK).append("set").append(BLANK);
		if (table.hasVersionColumn()) {
			Column versionColumn = table.getVersionColumn();
			builder.append(versionColumn.getSqlName()).append(BLANK).append("=").append(BLANK)
					.append(versionColumn.getSqlName()).append(" + 1 ,");
		}
		if (CollectionUtils.isNotEmpty(columns)) {
			table.getColumns().stream().filter(Column::isUpdatable).filter(column -> columns.contains(column.getJavaName())).forEach(column -> {
				builder.append(BLANK).append(column.getSqlName());
				builder.append("=");
				builder.append("#{").append("po.").append(column.getJavaName()).append(",jdbcType=").append(column.getSqlTypeName()).append("}");
				builder.append(",");
			});
		} else {
			table.getColumns().stream().filter(Column::isUpdatable).forEach(column -> {
				builder.append(BLANK).append(column.getSqlName());
				builder.append("=");
				builder.append("#{").append("po.").append(column.getJavaName()).append(",jdbcType=").append(column.getSqlTypeName()).append("}");
				builder.append(",");
			});
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(BLANK).append("where").append(BLANK);
		builder.append("id = #{po.id,jdbcType=VARCHAR}");
		if (versionable && table.hasVersionColumn()) {
			Column versionColumn = table.getVersionColumn();
			builder.append(BLANK).append("AND").append(BLANK);
			builder.append(versionColumn.getSqlName()).append(BLANK).append("=").append(BLANK);
			builder.append("#{po.").append(versionColumn.getJavaName()).append("}");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(builder.toString());
		}
		return builder.toString();
	}

	public String updateSelective(@Param("po") Object po, @Param("versionable") boolean versionable, @Param("columns") Set<String> includeColumns) {
		if (!(po instanceof CommonPO)) {
			return null;
		}
		CommonPO commonPO = (CommonPO) po;
		if (commonPO.getId() == null) {
			throw new RuntimeException("id is null when update");
		}
		Class<?> poClass = po.getClass();
		Table table = ORMapping.get(poClass);
		if (table == null) {
			throw new RuntimeException("not a jpa standard class:" + poClass.getName());
		}
		StringBuilder builder = new StringBuilder();
		builder.append("update");
		builder.append(BLANK);
		builder.append(getTableNameById(table, ((CommonPO) po).getId()));
		builder.append(BLANK);
		builder.append("set").append(BLANK);
		if (table.hasVersionColumn()) {
			Column versionColumn = table.getVersionColumn();
			builder.append(versionColumn.getSqlName()).append(BLANK).append("=").append(BLANK)
					.append(versionColumn.getSqlName()).append(" + 1 ,");
		}
		table.getColumns().stream().filter(Column::isUpdatable).forEach(column -> {
			Object fieldValue = ReflectionUtils.getFieldValue(commonPO, column.getJavaName());
			boolean include = false;
			if (CollectionUtils.isNotEmpty(includeColumns) && includeColumns.contains(column.getJavaName())) {
				include = true;
			} else if (fieldValue != null) {
				include = true;
			}
			if (include) {
				builder.append(BLANK);
				String jdbcType = column.getSqlTypeName();
				builder.append(column.getSqlName()).append("=");
				builder.append("#{po.").append(column.getJavaName()).append(",jdbcType=").append(jdbcType).append("}");
				builder.append(",");
			}
		});
		builder.deleteCharAt(builder.length() - 1);
		builder.append(BLANK).append("where").append(BLANK);
		builder.append("id = #{po.id,jdbcType=VARCHAR}");
		if (versionable && table.hasVersionColumn()) {
			Column versionColumn = table.getVersionColumn();
			builder.append(BLANK).append("AND").append(BLANK);
			builder.append(versionColumn.getSqlName()).append(BLANK).append("=").append(BLANK);
			builder.append("#{po.").append(versionColumn.getJavaName()).append("}");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(builder.toString());
		}
		return builder.toString();
	}

	public String batchUpdateSelective(@Param("clazz") Class<? extends CommonPO> clazz
			, @Param("list") List<? extends CommonPO> poList, @Param("includeColumns") Set<String> columns) {
		return BatchType.BatchUpdateSelective.name();
	}

	public String batchUpdate(@Param("clazz") Class<? extends CommonPO> clazz
			, @Param("list") List<? extends CommonPO> poList, @Param("includeColumns") Set<String> columns) {
		return BatchType.BatchUpdate.name();
	}

	public String shardingGetList(@Param("idList") List<String> idList, @Param("clazz") Class<?> clazz) {
		return BatchType.BatchGetList.name();
	}

	public String shardingGetByIdList(@Param("idList") List<String> idList, @Param("clazz") Class<?> clazz) {
		return BatchType.BatchGetByIdList.name();
	}

	public String delete(@Param("map") Map<String, Object> map, @Param("clazz") Class clazz) {
		Table table = ORMapping.get(clazz);
		if (table == null) {
			throw new RuntimeException("not a jpa standard class:" + clazz.getName());
		}
		StringBuilder builder = new StringBuilder();
		builder.append("update");
		builder.append(BLANK);
		builder.append(getTableNameById(table, (String) map.get("id")));
		builder.append(BLANK);
		builder.append("set").append(BLANK);
		builder.append("update_time = #{map.updateTime,jdbcType=TIMESTAMP}");
		builder.append(",version = version + 1");
		builder.append(",update_by = #{map.updateBy,jdbcType=VARCHAR}");
		builder.append(",is_deleted = 1");
		builder.append(BLANK).append("where").append(BLANK);
		builder.append("id = #{map.id,jdbcType=VARCHAR}");
		if (logger.isDebugEnabled()) {
			logger.debug(builder.toString());
		}
		return builder.toString();
	}

	public String deleteByCriteria(@Param("clazz") Class clazz, @Param("criteria") Criteria criteria, @Param("map") Map<String, Object> parameterMap) {
		Table table = ORMapping.get(clazz);
		if (table == null) {
			throw new RuntimeException("not a jpa standard class:" + clazz.getName());
		}
		SqlBuilder sql = new SqlBuilder();
		AtomicInteger counter = new AtomicInteger(1);
		Map<String, String> tableAliasMap = new ConcurrentHashMap<>();
		String rootAlias = createTableAlias(table.getSqlName(), counter);
		tableAliasMap.put(ROOT_TABLE_ALIAS_KEY, rootAlias);
		sql.UPDATE(getTableNameByCriteria(table, criteria) + " AS " + rootAlias);
		sql.SET(rootAlias + "." + "update_time = #{map.updateTime,jdbcType=TIMESTAMP}");
		sql.SET(rootAlias + "." + "update_by = #{map.updateBy,jdbcType=VARCHAR}");
		sql.SET(rootAlias + "." + "is_deleted = 1");
		sql.SET(rootAlias + "." + "version = " + rootAlias + ".version + 1");
		List<Condition> conditions = criteria.getConditions();
		if (CollectionUtils.isNotEmpty(conditions)) {
			for (Condition condition : conditions) {
				buildCondition(parameterMap, tableAliasMap, counter, sql, condition, table, criteria);
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug(sql.toString());
		}
		return sql.toString();
	}

	public String get(@Param("clazz") Class clazz, @Param("id") String id) {
		if (!ReflectionUtils.isSubClass(clazz, CommonPO.class)) {
			return null;
		}
		Table table = ORMapping.get(clazz);
		if (table == null) {
			throw new RuntimeException("not a jpa standard class:" + clazz.getName());
		}
		StringBuilder builder = new StringBuilder();
		builder.append("select");
		builder.append(BLANK);
		buildSelectItem(null, table, builder);
		builder.append(BLANK);
		builder.append("from");
		builder.append(getTableNameById(table, id));
		builder.append(BLANK).append("where").append(BLANK);
		//no is deleted
		builder.append("id = #{id,jdbcType=VARCHAR}");
		if (logger.isDebugEnabled()) {
			logger.debug(builder.toString());
		}
		return builder.toString();
	}


	public String findByCriteria(@Param("clazz") Class clazz, @Param("criteria") Criteria criteria, @Param("map") Map<String, Object> map) {
		if (!ReflectionUtils.isSubClass(clazz, CommonPO.class)) {
			return null;
		}
		Table table = ORMapping.get(clazz);
		if (table == null) {
			throw new RuntimeException("not a jpa standard class:" + clazz.getName());
		}
		PropertySelector selector = criteria.getSelector();
		SqlBuilder sql = new SqlBuilder();
		AtomicInteger counter = new AtomicInteger(1);
		String tableName = getTableNameByCriteria(table, criteria);
		Map<String, String> tableAliasMap = new ConcurrentHashMap<>();
		String rootAlias = createTableAlias(tableName, counter);
		tableAliasMap.put(ROOT_TABLE_ALIAS_KEY, rootAlias);
		List<Condition> conditions = criteria.getConditions();
		Set<String> pathPrexs = new HashSet<>();
		if (null != selector) {
			List<String> userSelector = new ArrayList<>();
			if (null != selector.getExcludes()) {
				userSelector.addAll(selector.getExcludes());
			}
			if (null != selector.getIncludes()) {
				userSelector.addAll(selector.getIncludes());
			}
			userSelector.forEach(include -> {
				int index = include.lastIndexOf(".");
				String pathPrex = index > -1 ? include.substring(0, index) : null;
				pathPrexs.add(pathPrex);
			});
		}
		List<Field> fields = ReflectionUtils.getDeclaredFields(clazz);
		Map<String, Field> mainTableFieldsMap = new HashMap<>();
		fields.forEach(field -> {
			mainTableFieldsMap.put(field.getName(), field);
		});
		List<Column> mainTableBaseColumns = new ArrayList<>();
		//默认返回主类全部根字段
		table.getColumns().stream().filter(column -> {
			//关联表的类型不会出现在column中
			Field tableField = mainTableFieldsMap.get(column.getJavaName());
			if (null != tableField) {
				Annotation OneToMany = tableField.getAnnotation(javax.persistence.OneToMany.class);
				Annotation OneToOne = tableField.getAnnotation(javax.persistence.OneToOne.class);
				Annotation ManyToOne = tableField.getAnnotation(javax.persistence.ManyToOne.class);
				if (!pathPrexs.contains(null) && null == OneToMany && null == OneToOne && null == ManyToOne) {
					mainTableBaseColumns.add(column);
				}
			}

			if (selector == null ||
					(CollectionUtils.isEmpty(selector.getIncludes()) && CollectionUtils.isEmpty(selector.getExcludes()))) {
				//防止单表查询时字段重复添加
				if (mainTableBaseColumns.contains(column)) {
					mainTableBaseColumns.remove(column);
				}
				return true;
			}
			List<String> includes = selector.getIncludes();
			if (CollectionUtils.isNotEmpty(includes)) {
				includes.forEach(include -> {
					JoinType joinType = JoinType.LEFT;
					//检查查询条件中是否设置了对应属性的joinType，如果没有设定，就默认使用LEFT，如果设定了，就用查询条件中指定的joinType
					if (CollectionUtils.isNotEmpty(conditions)) {
						for (Condition condition : conditions) {
							if (StringUtils.isNotEmpty(condition.getPropertyName()) && condition.getPropertyName().equals(include)) {
								joinType = condition.getJoinType();
							}
						}
					}
					resolvePropertyCascade(tableAliasMap, table, rootAlias, counter, sql, joinType, null, include, criteria);
				});
				return includes.contains(column.getJavaName());
			} else if (CollectionUtils.isNotEmpty(selector.getExcludes())) {
				return !selector.getExcludes().contains(column.getJavaName());
			}
			return false;
		}).forEach(column -> {
			sql.SELECT(rootAlias + "." + column.getSqlName() + " AS " + "\"" + column.getJavaName() + "\"");
		});
		mainTableBaseColumns.forEach(column -> {
			sql.SELECT(rootAlias + "." + column.getSqlName() + " AS " + "\"" + column.getJavaName() + "\"");
		});
		sql.FROM(tableName + " AS " + rootAlias);
		if (CollectionUtils.isNotEmpty(conditions)) {
			for (Condition condition : conditions) {
				buildCondition(map, tableAliasMap, counter, sql, condition, table, criteria);
			}
		}
		buildOrder(tableAliasMap, counter, sql, table, criteria);
		return sql.toString();
	}

	public String countByCondition(@Param("clazz") Class clazz, @Param("criteria") Criteria criteria, @Param("map") Map<String, Object> map) {
		if (!ReflectionUtils.isSubClass(clazz, CommonPO.class)) {
			return null;
		}
		Table table = ORMapping.get(clazz);
		if (table == null) {
			throw new RuntimeException("not a jpa standard class:" + clazz.getName());
		}
		SqlBuilder sql = new SqlBuilder();
		AtomicInteger counter = new AtomicInteger(1);
		Map<String, String> tableAliasMap = new ConcurrentHashMap<>();
		String rootAlias = createTableAlias(table.getSqlName(), counter);
		tableAliasMap.put(ROOT_TABLE_ALIAS_KEY, rootAlias);
		sql.SELECT("count(1)");
		sql.FROM(getTableNameByCriteria(table, criteria) + BLANK + "AS" + BLANK + rootAlias);
		List<Condition> conditions = criteria.getConditions();
		if (CollectionUtils.isNotEmpty(conditions)) {
			for (Condition condition : conditions) {
				buildCondition(map, tableAliasMap, counter, sql, condition, table, criteria);
			}
		}
		return sql.toString();
	}


	public void buildSelectItem(PropertySelector selector, Table table, StringBuilder builder) {
		table.getColumns().stream().filter(column -> {
			if (selector == null ||
					(CollectionUtils.isEmpty(selector.getIncludes()) && CollectionUtils.isEmpty(selector.getIncludes()))) {
				return true;
			}
			if (CollectionUtils.isNotEmpty(selector.getIncludes())) {
				return selector.getIncludes().contains(column.getJavaName());
			} else if (CollectionUtils.isNotEmpty(selector.getExcludes())) {
				return !selector.getExcludes().contains(column.getJavaName());
			}
			return false;
		}).forEach(column -> {
			builder.append(BLANK).append(column.getSqlName()).append(BLANK)
					.append("as").append(BLANK)
					.append("\"").append(column.getJavaName()).append("\"");
			builder.append(",");
		});
		builder.deleteCharAt(builder.length() - 1);
	}

	private String getTableNameById(Table table, String id) {
		return table.getSqlName();
	}

	private String getTableNameByPO(Table table, CommonPO object) {
		return table.getSqlName();
	}

	private String getTableNameByCriteria(Table table, Criteria criteria) {
		return table.getSqlName();

	}

}
