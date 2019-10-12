package com.wiki.framework.mybatis.dbinspector.processor;

import java.sql.Connection;
import java.util.List;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/8/7 下午5:03
 */
public interface DdlProcessor {
	/**
	 * 处理ddl语句的方法
	 *
	 * @param connection 数据库连接池
	 * @param ddlList    ddl 语句
	 * @throws Exception 任何异常
	 */
	void execute(Connection connection, List<String> ddlList) throws Exception;
}
