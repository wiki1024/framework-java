package com.wiki.framework.mybatis.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/10/9 上午9:21
 */
public class Table {

	private static Logger logger = LoggerFactory.getLogger(Table.class);
	/**
	 * db schema
	 */
	private String schema;
	/**
	 * db catalog
	 */
	private String catalog;
	/**
	 * 表名称
	 */
	private String sqlName;
	/**
	 * 表注释
	 */
	private String comment;
	/**
	 * oracle 数据owner
	 */
	private String ownerSynonymName;
	/**
	 * 列
	 */
	private List<Column> columns;
	/**
	 * 索引
	 */
	private List<Index> indices;

	/**
	 * 版本列
	 */
	private Column versionColumn;
	/**
	 * 关联的外键
	 */
	private ForeignKeys exportKeys;
	/**
	 * 被关联的外键
	 */
	private ForeignKeys importKeys;
	/**
	 * 表对应的java对象简名称
	 */
	private String simpleJavaName;
	/**
	 * java对象的全名
	 */
	private String javaName;
	/**
	 * 是否分表
	 */
	private boolean sharding;
	/**
	 * 分表别名
	 */
	private String shardingAlias;
	/**
	 * 分表个数
	 */
	private int shardingCount;
	/**
	 * 数据库连接信息
	 */
	private Database database;
	/**
	 * 是否有扩展字段
	 */
	private boolean isExtTable;
	/**
	 * 表的关联对象
	 */
	private List<Join> joins;

	@Override
	public String toString() {
		return "Table{" +
				"schema='" + schema + '\'' +
				", catalog='" + catalog + '\'' +
				", sqlName='" + sqlName + '\'' +
				", comment='" + comment + '\'' +
				", ownerSynonymName='" + ownerSynonymName + '\'' +
				", columns=" + columns +
				", indices=" + indices +
				", versionColumn=" + versionColumn +
				", exportKeys=" + exportKeys +
				", importKeys=" + importKeys +
				", simpleJavaName='" + simpleJavaName + '\'' +
				", javaName='" + javaName + '\'' +
				", sharding=" + sharding +
				", shardingAlias='" + shardingAlias + '\'' +
				", shardingCount=" + shardingCount +
				", database=" + database +
				", isExtTable=" + isExtTable +
				", joins=" + joins +
				'}';
	}

	public void addIndex(Index index) {
		if (this.indices == null) {
			this.indices = new ArrayList<>();
		}
		this.indices.add(index);
	}

	public void addColumn(Column column) {
		if (this.columns == null) {
			this.columns = new ArrayList<>();
		}
		this.columns.add(column);
	}

	public void addJoin(Join join) {
		if (this.joins == null) {
			this.joins = new ArrayList<>();
		}
		this.joins.add(join);
	}


	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getSqlName() {
		return sqlName;
	}

	public void setSqlName(String sqlName) {
		this.sqlName = sqlName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Index> getIndices() {
		return indices;
	}

	public void setIndices(List<Index> indices) {
		this.indices = indices;
	}


	public ForeignKeys getExportKeys() {
		return exportKeys;
	}

	public void setExportKeys(ForeignKeys exportKeys) {
		this.exportKeys = exportKeys;
	}

	public ForeignKeys getImportKeys() {
		return importKeys;
	}

	public void setImportKeys(ForeignKeys importKeys) {
		this.importKeys = importKeys;
	}

	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public String getOwnerSynonymName() {
		return ownerSynonymName;
	}

	public void setOwnerSynonymName(String ownerSynonymName) {
		this.ownerSynonymName = ownerSynonymName;
	}

	public String getSimpleJavaName() {
		return simpleJavaName;
	}

	public void setSimpleJavaName(String simpleJavaName) {
		this.simpleJavaName = simpleJavaName;
	}

	public boolean isSharding() {
		return sharding;
	}

	public void setSharding(boolean sharding) {
		this.sharding = sharding;
	}

	public int getShardingCount() {
		return shardingCount;
	}

	public void setShardingCount(int shardingCount) {
		this.shardingCount = shardingCount;
	}

	public String getShardingAlias() {
		return shardingAlias;
	}

	public void setShardingAlias(String shardingAlias) {
		this.shardingAlias = shardingAlias;
	}


	public Column getVersionColumn() {
		return versionColumn;
	}

	public void setVersionColumn(Column versionColumn) {
		this.versionColumn = versionColumn;
	}

	public List<Join> getJoins() {
		return joins;
	}

	public void setJoins(List<Join> joins) {
		this.joins = joins;
	}

	public boolean hasVersionColumn() {
		return versionColumn != null;
	}

	public boolean isExtTable() {
		return isExtTable;
	}

	public void setExtTable(boolean extTable) {
		this.isExtTable = extTable;
	}
}
