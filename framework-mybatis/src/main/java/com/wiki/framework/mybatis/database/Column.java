package com.wiki.framework.mybatis.database;

import java.util.Objects;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/10/9 上午9:23
 */
public class Column implements Comparable<Column> {
	/**
	 * 关联的表
	 */
	private Table table;
	/**
	 * The java.sql.Types type
	 */
	private int sqlType;
	/**
	 * The sql typename. provided by JDBC driver
	 */
	private String sqlTypeName;
	/**
	 * 数据库列名称
	 */
	private String sqlName;
	/**
	 * 长度
	 */
	private int length;
	/**
	 * 精度
	 */
	private int precision;
	/**
	 * 刻度
	 */
	private int scale;
	/**
	 * 是否为空
	 */
	private boolean nullable;
	/**
	 * 是否是索引
	 */
	private boolean indexed;
	/**
	 * 是否是唯一索引
	 */
	private boolean unique;
	/**
	 * 是否可以新增
	 */
	private boolean insertable = true;
	/**
	 * 是否可以修改
	 */
	private boolean updatable = true;
	/**
	 * 默认值
	 */
	private String defaultValue;
	/**
	 * 是否是分表列
	 */
	private boolean isSharding;
	/**
	 * 是否是主键
	 */
	private boolean isPk;
	/**
	 * 是否是外键
	 */
	private boolean isFk;
	/**
	 * 列注释
	 */
	private String sqlComment;
	/**
	 * java类型名称
	 */
	private String javaTypeName;
	/**
	 * java类型名称
	 */
	private Class<?> javaType;
	/**
	 * java名称
	 */
	private String javaName;
	/**
	 * columnDefinition
	 */
	private String columnDefinition;

	@Override
	public String toString() {
		return "Column{" +
				"sqlType=" + sqlType +
				", sqlTypeName='" + sqlTypeName + '\'' +
				", sqlName='" + sqlName + '\'' +
				", length=" + length +
				", precision=" + precision +
				", scale=" + scale +
				", nullable=" + nullable +
				", indexed=" + indexed +
				", unique=" + unique +
				", insertable=" + insertable +
				", updatable=" + updatable +
				", defaultValue='" + defaultValue + '\'' +
				", isSharding=" + isSharding +
				", isPk=" + isPk +
				", isFk=" + isFk +
				", sqlComment='" + sqlComment + '\'' +
				", javaTypeName='" + javaTypeName + '\'' +
				", javaType=" + javaType +
				", javaName='" + javaName + '\'' +
				", columnDefinition='" + columnDefinition + '\'' +
				'}';
	}

	public Column(Table table) {
		this.table = table;
	}

	public void copyFrom(Column column) {
		this.sqlType = column.getSqlType();
		this.sqlTypeName = column.getSqlTypeName();
		this.sqlName = column.getSqlName();
		this.length = column.getLength();
		this.precision = column.getPrecision();
		this.scale = column.getScale();
		this.nullable = column.isNullable();
		this.indexed = column.isIndexed();
		this.unique = column.isUnique();
		this.insertable = column.isInsertable();
		this.updatable = column.isUpdatable();
		this.defaultValue = column.getDefaultValue();
		this.isSharding = column.isSharding();
		this.isPk = column.isPk();
		this.isFk = column.isFk();
		this.sqlComment = column.getSqlComment();
		this.javaTypeName = column.getJavaTypeName();
		this.javaType = column.getJavaType();
		this.javaName = column.getJavaName();
		this.columnDefinition = column.getColumnDefinition();
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

	public String getSqlTypeName() {
		return sqlTypeName;
	}

	public void setSqlTypeName(String sqlTypeName) {
		this.sqlTypeName = sqlTypeName;
	}

	public String getSqlName() {
		return sqlName;
	}

	public void setSqlName(String sqlName) {
		this.sqlName = sqlName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isPk() {
		return isPk;
	}

	public void setPk(boolean pk) {
		isPk = pk;
	}

	public boolean isFk() {
		return isFk;
	}

	public void setFk(boolean fk) {
		isFk = fk;
	}

	public String getJavaTypeName() {
		return javaTypeName;
	}

	public void setJavaTypeName(String javaTypeName) {
		this.javaTypeName = javaTypeName;
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}

	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	public String getSqlComment() {
		return sqlComment;
	}

	public void setSqlComment(String sqlComment) {
		this.sqlComment = sqlComment;
	}

	public String getColumnDefinition() {
		return columnDefinition;
	}

	public void setColumnDefinition(String columnDefinition) {
		this.columnDefinition = columnDefinition;
	}

	public boolean isSharding() {
		return isSharding;
	}

	public void setSharding(boolean sharding) {
		isSharding = sharding;
	}

	public boolean isInsertable() {
		return insertable;
	}

	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}

	public boolean isUpdatable() {
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	@Override
	public int compareTo(Column o) {
		return this.javaName.compareTo(o.javaName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Column column = (Column) o;
		return Objects.equals(sqlName, column.sqlName);
	}

	@Override
	public int hashCode() {

		return Objects.hash(sqlName);
	}
}
