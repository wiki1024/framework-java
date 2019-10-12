package com.wiki.framework.mybatis.database;

public class DuplicateUniqueConstaint extends RuntimeException {

	public DuplicateUniqueConstaint(String uqName) {
		super(uqName + "重复定义");
	}
}
