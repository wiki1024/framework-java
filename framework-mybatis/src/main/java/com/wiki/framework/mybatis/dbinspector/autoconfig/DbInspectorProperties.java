package com.wiki.framework.mybatis.dbinspector.autoconfig;

import com.wiki.framework.mybatis.dbinspector.dialect.impl.MysqlDialect;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author thomason
 * @version 1.0
 * @since 2019/1/3 2:15 PM
 */

public class DbInspectorProperties {
	/**
	 * 数据库方言，默认mysql
	 */
	private String dialectClass = MysqlDialect.class.getName();

	/**
	 * 是否自动启用，默认启用
	 */
	private boolean enabled = true;

	/**
	 * 是否同步检查表结构
	 */
	private boolean async = false;

	/**
	 * 处理ddl语句的实现类，默认是consoleDdlProcessor 直接将ddl打印在控制台
	 * 如果想自动执行ddl语句，可以将此值设置为jdbcDdlProcessor
	 */
	private String ddlProcessorBeanName = "jdbcDdlProcessor";

	public String getDialectClass() {
		return dialectClass;
	}

	public void setDialectClass(String dialectClass) {
		this.dialectClass = dialectClass;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	public String getDdlProcessorBeanName() {
		return ddlProcessorBeanName;
	}

	public void setDdlProcessorBeanName(String ddlProcessorBeanName) {
		this.ddlProcessorBeanName = ddlProcessorBeanName;
	}
}
