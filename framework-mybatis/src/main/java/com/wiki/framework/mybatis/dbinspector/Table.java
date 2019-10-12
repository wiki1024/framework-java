package com.wiki.framework.mybatis.dbinspector;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomason
 * @version 1.0
 * @since 2015/3/11 14:40
 */

public class Table {
	public static final String PKTABLE_NAME = "PKTABLE_NAME";
	public static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
	public static final String FKTABLE_NAME = "FKTABLE_NAME";
	public static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
	public static final String KEY_SEQ = "KEY_SEQ";
	// 表的所有列
	@SuppressWarnings("unchecked")
	List<Column> columns = new ArrayList();
	// 表的主键列
	@SuppressWarnings("unchecked")
	List primaryKeyColumns = new ArrayList();
	private String schema = DatabaseResolver.getInstance().schema;
	// 表在数据库里面的名称
	private String tableName;
	// 表的外键
	private String foreignKey;
	//索引
	private List<Index> indices;
	/**
	 * the name of the owner of the synonym if this table is a synonym
	 */
	private String ownerSynonymName = null;

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public String getOwnerSynonymName() {
		return ownerSynonymName;
	}

	public void setOwnerSynonymName(String ownerSynonymName) {
		this.ownerSynonymName = ownerSynonymName;
	}

	@SuppressWarnings("unchecked")
	public List getPrimaryKeyColumns() {
		return primaryKeyColumns;
	}

	public void setPrimaryKeyColumns(List primaryKeyColumns) {
		this.primaryKeyColumns = primaryKeyColumns;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@SuppressWarnings("unchecked")
	public void addColumn(Column column) {
		columns.add(column);
	}

	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @param schema the schema to set
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * @param foreignKey the foreignKey to set
	 */
	public void setForeignKey(String foreignKey) {
		this.foreignKey = foreignKey;
	}

	/**
	 * @return the foreignKey
	 */
	public String getForeignKeyColumnName() {
		return foreignKey;
	}

	public List<Index> getIndices() {
		return indices;
	}

	public void setIndices(List<Index> indices) {
		this.indices = indices;
	}
}
