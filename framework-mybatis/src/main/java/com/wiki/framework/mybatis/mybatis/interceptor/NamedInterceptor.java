package com.wiki.framework.mybatis.mybatis.interceptor;

import org.apache.ibatis.plugin.Interceptor;


/**
 * 打上名字，方便debug，同时将自定义的{@link Interceptor}与第三方区别开
 */
public interface NamedInterceptor extends Interceptor {

	String getName();
}
