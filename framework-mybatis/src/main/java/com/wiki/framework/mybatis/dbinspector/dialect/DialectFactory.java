package com.wiki.framework.mybatis.dbinspector.dialect;


import com.wiki.framework.mybatis.dbinspector.dialect.impl.MysqlDialect;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/8/13 上午10:20
 */
public class DialectFactory {
	public static Dialect getDialect(String dbType) {
		switch (dbType) {
			case "mysql":
				return new MysqlDialect();
			default:
				return new MysqlDialect();
		}
	}
}
