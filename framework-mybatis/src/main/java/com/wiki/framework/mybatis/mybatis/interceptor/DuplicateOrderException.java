package com.wiki.framework.mybatis.mybatis.interceptor;

public class DuplicateOrderException extends RuntimeException {

	public DuplicateOrderException(String message) {
		super(message);
	}
}
