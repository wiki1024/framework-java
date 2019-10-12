package com.wiki.framework.mybatis.dbinspector;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/9/5 下午2:25
 */
class Index {
	private String name;
	private String columnList;
	private boolean unique;
	private String ddl;

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

	public String getDdl() {
		return ddl;
	}

	public void setDdl(String ddl) {
		this.ddl = ddl;
	}
}
