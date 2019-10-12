package com.wiki.framework.mybatis.query;

/**
 * @author Thomason
 * @version 1.0
 * @since 11-4-27  下午4:40
 */
public enum MatchMode {
	//精确匹配
	EXACT,
	BEFORE,//前匹配
	AFTER, //后匹配
	ANYWHERE //任意匹配
}
