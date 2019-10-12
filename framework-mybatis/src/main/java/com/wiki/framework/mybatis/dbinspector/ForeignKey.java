/*
 * Created on Jan 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.wiki.framework.mybatis.dbinspector;

import java.util.List;

/**
 * @author chris
 * <p/>
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
class ForeignKey {

	protected String relationShip = null;
	protected String firstRelation = null;
	protected String secondRelation = null;
	protected Table parentTable;
	protected String tableName;
	protected ListHashtable columns;
	protected ListHashtable parentColumns;

	public ForeignKey(Table aTable, String tblName) {
		super();
		parentTable = aTable;
		tableName = tblName;
		columns = new ListHashtable();
		parentColumns = new ListHashtable();
	}

	/**
	 * @return Returns the tableName.
	 */
	public String getTableName() {
		return tableName;
	}

	public String getParentTableName() {
		return parentTable.getTableName();
	}

	/**
	 * @param col
	 * @param seq
	 */
	public void addColumn(String col, String parentCol, Integer seq) {
		columns.put(seq, col);
		parentColumns.put(seq, parentCol);
	}

	public String getColumn(String parentCol) {
		// return the associated column given the parent column
		Object key = parentColumns.getKeyForValue(parentCol);
		String col = (String) columns.get(key);
		// System.out.println("get Column for" +parentCol);
		// System.out.println("key = "+key);
		// System.out.println("col="+col);
		// System.out.println("ParentColumns = "+parentColumns.toString());
		return col;
	}

	public ListHashtable getColumns() {
		return columns;
	}


	@SuppressWarnings("unchecked")
	private boolean hasAllPrimaryKeys(List pkeys, ListHashtable cols) {
		boolean hasAll = true;
		// if size is not equal then false
		int numKeys = pkeys.size();
		if (numKeys != cols.size()) {
			return false;
		}

		for (int i = 0; i < numKeys; i++) {
			Column col = (Column) pkeys.get(i);
			String colName = col.getSqlName();
			if (!cols.contains(colName)) {
				return false;
			}
		}

		return hasAll;
	}

	@SuppressWarnings("unchecked")
	public boolean isParentColumnsFromPrimaryKey() {
		boolean isFrom = true;
		@SuppressWarnings("unused")
		List keys = parentTable.getPrimaryKeyColumns();
		int numKeys = getParentColumns().size();
		for (int i = 0; i < numKeys; i++) {
			String pcol = (String) getParentColumns().getOrderedValue(i);
			if (!primaryKeyHasColumn(pcol)) {
				isFrom = false;
				break;
			}
		}
		return isFrom;
	}

	private boolean primaryKeyHasColumn(String aColumn) {
		boolean isFound = false;
		int numKeys = parentTable.getPrimaryKeyColumns().size();
		for (int i = 0; i < numKeys; i++) {
			Column sqlCol = (Column) parentTable.getPrimaryKeyColumns().get(i);
			String colname = sqlCol.getSqlName();
			if (colname.equals(aColumn)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}

	@SuppressWarnings("unchecked")
	public boolean getHasImportedKeyColumn(String aColumn) {
		boolean isFound = false;
		List cols = getColumns().getOrderedValues();
		int numCols = cols.size();
		for (int i = 0; i < numCols; i++) {
			String col = (String) cols.get(i);
			if (col.equals(aColumn)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}


	/**
	 * @return Returns the parentTable.
	 */
	public Table getParentTable() {
		return parentTable;
	}


	/**
	 * @return Returns the parentColumns.
	 */
	public ListHashtable getParentColumns() {
		return parentColumns;
	}

	@SuppressWarnings("unchecked")
	public boolean getHasImportedKeyParentColumn(String aColumn) {
		boolean isFound = false;
		List cols = getParentColumns().getOrderedValues();
		int numCols = cols.size();
		for (int i = 0; i < numCols; i++) {
			String col = (String) cols.get(i);
			if (col.equals(aColumn)) {
				isFound = true;
				break;
			}
		}
		return isFound;
	}
}
