package com.wiki.framework.mybatis.database;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/10/9 上午9:15
 */
public enum DbType {
	mysql("com.mysql.jdbc.Driver") {
		@Override
		public String getJdbcUrl(String host, int port, String name, String connParam) {
			return "jdbc:mysql://" + host + ":" + port + "/" + name + (connParam == null ? "" : "?" + connParam);
		}
	}, postgres("org.postgresql.Driver") {
		@Override
		public String getJdbcUrl(String host, int port, String name, String connParam) {
			return "jdbc:postgresql://" + host + ":" + port + "/" + name;
		}
	}, oracle("oracle.jdbc.driver.OracleDriver") {
		@Override
		public String getJdbcUrl(String host, int port, String name, String connParam) {
			return "jdbc:oracle:thin:@//" + host + ":" + port + "/" + name;
		}
	};
	private final String driverClass;

	DbType(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public abstract String getJdbcUrl(String host, int port, String name, String connParam);
}
