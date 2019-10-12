package com.wiki.framework.mybatis.database;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/10/9 上午9:23
 */
public class Index {
	/**
	 * 索引名称
	 */
	private String name;
	/**
	 * 索引列集合
	 */
	private String columnList;
	/**
	 * 是否唯一索引
	 */
	private boolean unique;
	/**
	 * 索引所在的表
	 */
	private Table table;

	public Index(Table table) {
		this.table = table;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColumnList() {
		return columnList;
	}

	public void setColumnList(String columnList) {
		this.columnList = columnList;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}
}
