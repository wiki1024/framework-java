package com.wiki.framework.mybatis.database;

import java.util.List;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/10/9 上午9:09
 */
public class Database {
	private String host;
	private int port;
	private String name;
	private DbType type;
	private String connParam;
	private String username;
	private String password;
	private String schema;
	private String catalog;

	private List<Table> tables;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public DbType getType() {
		return type;
	}

	public void setType(DbType type) {
		this.type = type;
	}

	public String getConnParam() {
		return connParam;
	}

	public void setConnParam(String connParam) {
		this.connParam = connParam;
	}

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
