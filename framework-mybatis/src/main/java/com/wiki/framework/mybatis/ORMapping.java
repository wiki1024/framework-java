package com.wiki.framework.mybatis;

import com.wiki.framework.mybatis.database.*;


import com.wiki.framework.common.util.DataType;
import com.wiki.framework.common.util.ReflectionUtils;
import com.wiki.framework.common.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Id;
import javax.persistence.Version;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.sql.Types.LONGVARCHAR;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/10/9 下午3:38
 */
public class ORMapping {
	private static Logger logger = LoggerFactory.getLogger(ORMapping.class);
	private static final Map<Class<?>, Table> orm = new ConcurrentHashMap<>();
	private static final Map<String, Class<?>> nameMap = new ConcurrentHashMap<>();

	public static Table get(Class<?> clazz) {
		javax.persistence.Table clazzAnnotation = clazz.getAnnotation(javax.persistence.Table.class);
		if (clazzAnnotation == null) {
			return null;
		}
		Table jpaTable = orm.computeIfAbsent(clazz, k -> {
			Table table = new Table();
			table.setSimpleJavaName(k.getSimpleName());
			table.setJavaName(k.getName());
			javax.persistence.Table tableAnno = k.getAnnotation(javax.persistence.Table.class);
			if (tableAnno == null) {
				throw new RuntimeException("not jpa standard class:" + k.getName());
			}
			String name = tableAnno.name();
			if (StringUtils.isBlank(name)) {
				name = k.getSimpleName();
			}
			table.setSqlName(name);
			table.setCatalog(tableAnno.catalog());
			table.setSchema(tableAnno.schema());

			//处理列
			List<Field> fields = ReflectionUtils.getDeclaredFields(k);
			List<Column> columns = fields.stream().filter(f -> {
				javax.persistence.Column columnAnno = f.getAnnotation(javax.persistence.Column.class);
				return columnAnno != null;
			}).map(field -> {
				javax.persistence.Column columnAnno = field.getAnnotation(javax.persistence.Column.class);
				Column column = new Column(table);
				column.setJavaName(field.getName());
				column.setJavaTypeName(field.getType().getName());
				column.setJavaType(field.getType());
				column.setPk(field.getAnnotation(Id.class) != null);
				//TODO 计算是否fk
				column.setFk(false);
				column.setLength(columnAnno.length());
				column.setScale(columnAnno.scale());
				column.setPrecision(columnAnno.precision());
				column.setNullable(columnAnno.nullable());
				column.setUnique(columnAnno.unique());
				column.setUpdatable(columnAnno.updatable());
				column.setInsertable(columnAnno.insertable());

				//自动设置columnDef
				JdbcType jdbcType = autoGuessJdbcType(field.getType());
				column.setSqlType(jdbcType.TYPE_CODE);
				column.setSqlTypeName(jdbcType.name());

				String columnName = columnAnno.name();
				if (StringUtils.isBlank(columnName)) {
					columnName = camelToUnderline(field.getName());
				}
				column.setSqlName(columnName);
				String definition = columnAnno.columnDefinition();
				if (StringUtils.isNotBlank(definition)) {
					column.setColumnDefinition(definition);
				}
				//检查是否索引列
				if (table.getIndices() != null) {
					column.setIndexed(table.getIndices().stream().filter(index -> index.getColumnList().contains(column.getSqlName())).count() > 0);
				}
				//版本列
				if (field.getAnnotation(Version.class) != null) {
					table.setVersionColumn(column);
				}
				return column;
			}).collect(Collectors.toList());
			if (null == columns || columns.size() == 0) {
				columns = getAnnotationsFromGetMethod(k, table);
			} else {
				columns.addAll(getAnnotationsFromGetMethod(k, table));
			}
			columns.forEach(j -> {
				if (j.isPk()) {
					table.addColumn(j);
				}
			});
			columns.forEach(j -> {
				if (!j.isPk()) {
					table.addColumn(j);
				}
			});

			//处理索引
			javax.persistence.Index[] indexes = tableAnno.indexes();
			Arrays.stream(indexes).forEach(t -> {
				String indexName = t.name();
				String columnList = t.columnList();
				boolean unique = t.unique();
				Index index = new Index(table);
				index.setName(indexName);
				index.setColumnList(columnList);
				index.setUnique(unique);
				index.setTable(table);
				table.addIndex(index);
				if (unique) {
					String[] split = columnList.split(",");
					buildConstraint(table, indexName, split);
				}

			});


			return table;
		});
		String sqlTableName = jpaTable.getSqlName();
		nameMap.putIfAbsent(sqlTableName, clazz);
		return jpaTable;
	}

	public static Class<?> getPoClazz(String sqlTableName) {
		return nameMap.get(sqlTableName);
	}

	private static void buildConstraint(Table table, String indexName, String[] sqlColumnNames) {
		List<Column> allColumns = table.getColumns();
		List<Column> constraintColumns = new ArrayList<>();
		for (String sqlColumnName : sqlColumnNames) {
			Optional<Column> any = allColumns.stream().filter(c -> c.getSqlName().equals(StringUtil.trim(sqlColumnName))).findAny();
			if (any.isPresent()) {
				constraintColumns.add(any.get());
			} else {
				throw new RuntimeException(sqlColumnName + "cannot be found in" + indexName);
			}
		}
	}

	/**
	 * 取get方法上的column注解
	 *
	 * @param clazz clazz
	 * @return columns
	 */
	private static List<Column> getAnnotationsFromGetMethod(final Class clazz, Table table) {
		String methodName = "get";
		List<Column> columns = new ArrayList<>();
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {

			Method[] methods = superClass.getDeclaredMethods();
			for (Method method : methods) {
				if (!method.getName().startsWith(methodName)) {
					continue;
				}
				Column column = new Column(table);
				column.setPk(false);
				Annotation[] annotations = method.getDeclaredAnnotations();
				javax.persistence.Column columnAnno = null;
				for (Annotation annotation : annotations) {
					if (Column.class.getName().equals(annotation.annotationType().getName())) {
						columnAnno = (javax.persistence.Column) annotation;
					}
					if (Id.class.equals(annotation.annotationType())) {
						column.setPk(true);
					}
					//版本列
					if (Version.class.equals(annotation.annotationType())) {
						table.setVersionColumn(column);
					}
				}
				if (null == columnAnno) {
					continue;
				}
				String fieldName = StringUtil.replace(method.getName(), methodName, StringUtils.EMPTY);
				fieldName = StringUtils.uncapitalize(fieldName);
				column.setJavaName(fieldName);
				column.setJavaTypeName(method.getReturnType().getName());
				column.setJavaType(method.getReturnType());
				//TODO 计算是否fk
				column.setFk(false);

				//自动设置columnDef
				JdbcType jdbcType = autoGuessJdbcType(method.getReturnType());
				//text
				if (String.class.equals(method.getReturnType()) && LONGVARCHAR == columnAnno.length()) {
					column.setSqlType(LONGVARCHAR);
				} else {
					column.setLength(columnAnno.length());
				}
				column.setScale(columnAnno.scale());
				column.setPrecision(columnAnno.precision());
				column.setNullable(columnAnno.nullable());
				column.setUnique(columnAnno.unique());
				column.setUpdatable(columnAnno.updatable());
				column.setInsertable(columnAnno.insertable());

				column.setSqlType(jdbcType.TYPE_CODE);
				column.setSqlTypeName(jdbcType.name());

				String sqlName = columnAnno.name();
				if (StringUtils.isBlank(sqlName)) {
					sqlName = camelToUnderline(fieldName);
				}
				column.setSqlName(sqlName);
				String definition = columnAnno.columnDefinition();
				if (StringUtils.isNotBlank(definition)) {
					column.setColumnDefinition(definition);
				}
				//检查是否索引列
				if (table.getIndices() != null) {
					column.setIndexed(table.getIndices().stream().anyMatch(index -> index.getColumnList().contains(column.getSqlName())));
				}
				columns.add(column);
			}
		}
		return columns;
	}

	private static JdbcType autoGuessJdbcType(Class<?> fieldType) {
		int dataType = DataType.getDataType(fieldType);
		if (DataType.isSimpleType(dataType)) {
			switch (dataType) {
				case DataType.DT_Byte:
				case DataType.DT_short:
					return JdbcType.TINYINT;
				case DataType.DT_int:
				case DataType.DT_Integer:
					return JdbcType.INTEGER;
				case DataType.DT_Long:
				case DataType.DT_long:
				case DataType.DT_BigInteger:
					return JdbcType.BIGINT;
				case DataType.DT_Double:
				case DataType.DT_double:
					return JdbcType.DECIMAL;
				case DataType.DT_Float:
				case DataType.DT_float:
					return JdbcType.DECIMAL;
				case DataType.DT_Character:
				case DataType.DT_char:
				case DataType.DT_String:
					return JdbcType.VARCHAR;
				case DataType.DT_Date:
				case DataType.DT_DateTime:
					return JdbcType.TIMESTAMP;
				case DataType.DT_Boolean:
					return JdbcType.TINYINT;
				case DataType.DT_BigDecimal:
					return JdbcType.DECIMAL;
				default:
					return JdbcType.VARCHAR;
			}
		} else if (dataType == DataType.DT_ENUM) {
			return JdbcType.VARCHAR;
		} else {
			return JdbcType.BLOB;
		}
	}

	private static String camelToUnderline(String s) {
		if (s == null || "".equals(s.trim())) {
			return "";
		}
		int len = s.length();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append("_");
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
