/*
 * Created on Jan 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.wiki.framework.mybatis.database;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/10/9 上午9:15
 */
public class ForeignKey {

	private final String pkTable;

	private final String fkTable;

	private final String pkColumn;

	private final int seq;

	private final String fkColumn;

	public ForeignKey(String pkTable, String fkTable, String pkColumn, int seq, String fkColumn) {
		this.pkTable = pkTable;
		this.fkTable = fkTable;
		this.pkColumn = pkColumn;
		this.seq = seq;
		this.fkColumn = fkColumn;
	}

	public String getPkTable() {
		return pkTable;
	}

	public String getFkTable() {
		return fkTable;
	}

	public String getPkColumn() {
		return pkColumn;
	}

	public int getSeq() {
		return seq;
	}

	public String getFkColumn() {
		return fkColumn;
	}
}
