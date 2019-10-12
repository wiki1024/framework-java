package com.wiki.framework.mybatis.database;

import java.util.ArrayList;
import java.util.List;

public class Join {
	private Relation relation;
	private String fieldName;
	private String targetTableName;
	private List<JoinColumn> joinColumns;
	private Class targetEntity;

	public Join(Relation relation, String fieldName, Class targetEntity) {
		this.relation = relation;
		this.fieldName = fieldName;
		this.targetEntity = targetEntity;
	}

	public void addJoinColumn(JoinColumn joinColumn) {
		if (this.joinColumns == null) {
			this.joinColumns = new ArrayList<>();
		}
		this.joinColumns.add(joinColumn);
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public String getTargetTableName() {
		return targetTableName;
	}

	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}

	public List<JoinColumn> getJoinColumns() {
		return joinColumns;
	}

	public void setJoinColumns(List<JoinColumn> joinColumns) {
		this.joinColumns = joinColumns;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(Class targetEntity) {
		this.targetEntity = targetEntity;
	}
}
