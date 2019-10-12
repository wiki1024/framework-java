package com.wiki.framework.mybatis.database;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/10/11 上午9:14
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Sharding {
	int count() default 10;

	//分表时的别名
	String alias() default "";

	//分表的列
	String[] columns();
}
