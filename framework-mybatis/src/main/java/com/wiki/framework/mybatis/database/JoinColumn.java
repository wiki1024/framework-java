package com.wiki.framework.mybatis.database;

public class JoinColumn {
	private String name;

	private String referencedColumnName = "id";

	public JoinColumn(String name) {
		this.name = name;
	}

	public JoinColumn(String name, String referencedColumnName) {
		this.name = name;
		this.referencedColumnName = referencedColumnName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReferencedColumnName() {
		return referencedColumnName;
	}

	public void setReferencedColumnName(String referencedColumnName) {
		this.referencedColumnName = referencedColumnName;
	}
}
