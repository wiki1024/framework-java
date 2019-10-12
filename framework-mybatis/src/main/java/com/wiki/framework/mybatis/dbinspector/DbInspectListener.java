package com.wiki.framework.mybatis.dbinspector;

import com.wiki.framework.mybatis.database.Table;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/10/18 下午2:50
 */
public interface DbInspectListener {

	/**
	 * 在扫描包之前运行的方法
	 *
	 * @param packagesToScan 要扫描的包
	 */
	void beforeScan(List<String> packagesToScan);

	/**
	 * 在扫描包之后执行的方法
	 *
	 * @param tables 要扫描的包
	 */
	void afterScan(List<Table> tables);

	/**
	 * 在扫描执行前运行的方法
	 * 注意：
	 * 如果是集群部署模式下，由于使用了分布式锁，只有一个实例会执行此方法
	 * 如果此方法抛出异常，不会继续
	 * 如果使用了datasource的getConnection方法，确保执行完成后，关闭连接，防止出现连接池泄露
	 *
	 * @param dataSource 数据源
	 * @param jpaTables  扫描到的jpa的表
	 * @throws Exception 异常
	 */
	void beforeInspectOnDatasource(DataSource dataSource, List<Table> jpaTables) throws Exception;

	/**
	 * 在扫描完成且执行完数据库更新后执行的方法
	 * 注意：
	 * 如果是集群部署模式下，由于使用了分布式锁，只有一个实例会执行此方法
	 * 此方法如果抛出异常，在async=false的情况下会阻止程序正常启动
	 * 如果使用了datasource的getConnection方法，确保执行完成后，关闭连接，防止出现连接池泄露
	 *
	 * @param dataSource 数据源
	 * @param jpaTables  扫描到的jpa的表
	 * @throws Exception 异常
	 */
	void afterInspectOnDatasource(DataSource dataSource, List<Table> jpaTables) throws Exception;
}
