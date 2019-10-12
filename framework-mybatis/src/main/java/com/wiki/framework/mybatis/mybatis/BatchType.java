package com.wiki.framework.mybatis.mybatis;

public enum BatchType {
	None,
	BatchInsert,
	BatchUpdateSelective,
	BatchUpdate,
	BatchGetList,
	BatchGetByIdList,
}
