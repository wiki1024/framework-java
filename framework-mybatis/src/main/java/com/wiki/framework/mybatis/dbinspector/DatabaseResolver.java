package com.wiki.framework.mybatis.dbinspector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * @author yangtao
 * @email walle1027@gmail.com
 */
@SuppressWarnings("ConstantConditions")
public class DatabaseResolver {
	/**
	 * Logger for this class
	 */
	private static final Logger _log = LoggerFactory.getLogger(DatabaseResolver.class);
	private static DatabaseResolver instance = new DatabaseResolver();
	// Properties props;
	public String catalog;
	public String schema;

	private DatabaseResolver() {

	}

	public static DatabaseResolver getInstance() {
		return instance;
	}

	public Table getTable(Connection conn, String sqlTableName) throws Exception {
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getTables(conn.getCatalog(), null, sqlTableName, null);
		while (rs.next()) {
			return createTable(conn, rs);
		}
		return null;
	}

	public List<Table> getTables(Connection conn, String tableNamePattern) throws Exception {
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getTables(catalog, schema, tableNamePattern, null);
		List<Table> tables = new ArrayList<>();
		while (rs.next()) {
			tables.add(createTable(conn, rs));
		}
		return tables;
	}

	private Table createTable(Connection connection, ResultSet rs) throws SQLException {
		ResultSetMetaData rsMetaData = rs.getMetaData();
		String schemaName = rs.getString("TABLE_SCHEM") == null ? "" : rs.getString("TABLE_SCHEM");
		String realTableName = rs.getString("TABLE_NAME");
		String tableType = rs.getString("TABLE_TYPE");
		if (!"table".equalsIgnoreCase(tableType)) {
			return null;
		}
		Table table = new Table();
		table.setTableName(realTableName);
		if ("SYNONYM".equals(tableType) && isOracleDataBase(connection)) {
			table.setOwnerSynonymName(getSynonymOwner(connection, realTableName));
		}
		retriveTableColumns(connection, table);
		DatabaseMetaData metaData = connection.getMetaData();
		//初始化索引
		ResultSet indexInfo = metaData.getIndexInfo(catalog, schema, realTableName, false, true);
		List<Index> indices = new ArrayList<>();
		while (indexInfo.next()) {
			Index index = new Index();
			index.setName(indexInfo.getString("INDEX_NAME"));
			index.setColumnList(indexInfo.getString("COLUMN_NAME"));
			index.setUnique(Boolean.valueOf(indexInfo.getString("NON_UNIQUE")));
			indices.add(index);
		}
		table.setIndices(indices);
		return table;
	}

	public List<Table> getAllTables(Connection conn) throws SQLException {
		DatabaseMetaData dbMetaData = conn.getMetaData();
		ResultSet rs = dbMetaData.getTables(catalog, schema, null, null);
		List<Table> tables = new ArrayList<>();
		while (rs.next()) {
			Table table = createTable(conn, rs);
			if (table != null) {
				tables.add(table);
			}
		}
		return tables;
	}

	private boolean isOracleDataBase(Connection connection) {
		boolean ret = false;
		try {
			ret = (getMetaData(connection).getDatabaseProductName().toLowerCase().contains("oracle"));
		} catch (Exception ignore) {
		}
		return ret;
	}

	private String getSynonymOwner(Connection connection, String synonymName) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String ret = null;
		try {
			String tableSql = "select table_owner from sys.all_synonyms where table_name=? and owner=?";
			ps = connection.prepareStatement(tableSql);
			ps.setString(1, synonymName);
			ps.setString(2, schema);
			rs = ps.executeQuery();
			if (rs.next()) {
				ret = rs.getString(1);
			} else {
				String databaseStructure = getDatabaseStructureInfo(connection);
				throw new RuntimeException("Wow! Synonym " + synonymName + " not found. How can it happen? " + databaseStructure);
			}
		} catch (SQLException e) {
			String databaseStructure = getDatabaseStructureInfo(connection);
			_log.error(e.getMessage(), e);
			throw new RuntimeException("Exception in getting synonym owner " + databaseStructure);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception ignored) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception ignored) {
				}
			}
		}
		return ret;
	}

	private String getDatabaseStructureInfo(Connection connection) {
		ResultSet schemaRs = null;
		ResultSet catalogRs = null;
		String nl = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer(nl);
		// Let's give the user some feedback. The exception
		// is probably related to incorrect schema configuration.
		sb.append("Configured schema:").append(schema).append(nl);
		sb.append("Configured catalog:").append(catalog).append(nl);

		try {
			schemaRs = getMetaData(connection).getSchemas();
			sb.append("Available schemas:").append(nl);
			while (schemaRs.next()) {
				sb.append("  ").append(schemaRs.getString("TABLE_SCHEM")).append(nl);
			}
		} catch (SQLException e2) {
			_log.warn("Couldn't get schemas", e2);
			sb.append("  ?? Couldn't get schemas ??").append(nl);
		} finally {
			try {
				schemaRs.close();
			} catch (Exception ignore) {
			}
		}
		try {
			catalogRs = getMetaData(connection).getCatalogs();
			sb.append("Available catalogs:").append(nl);
			while (catalogRs.next()) {
				sb.append("  ").append(catalogRs.getString("TABLE_CAT")).append(nl);
			}
		} catch (SQLException e2) {
			_log.warn("Couldn't get catalogs", e2);
			sb.append("  ?? Couldn't get catalogs ??").append(nl);
		} finally {
			try {
				catalogRs.close();
			} catch (Exception ignore) {
			}
		}
		return sb.toString();
	}

	private DatabaseMetaData getMetaData(Connection connection) throws SQLException {
		return connection.getMetaData();
	}

	@SuppressWarnings("unchecked")
	private void retriveTableColumns(Connection connection, Table table) throws SQLException {
		_log.debug("-------setColumns(" + table.getTableName() + ")");
		List primaryKeys = getTablePrimaryKeys(connection, table);
		table.setPrimaryKeyColumns(primaryKeys);

		// get the indices and unique columns
		List indices = new LinkedList();
		// maps index names to a list of columns in the index
		Map uniqueIndices = new HashMap();
		// maps column names to the index name.
		Map uniqueColumns = new HashMap();
		ResultSet indexRs = null;
		try {
			if (table.getOwnerSynonymName() != null) {
				indexRs = getMetaData(connection).getIndexInfo(catalog, table.getOwnerSynonymName(), table.getTableName(), false, true);
			} else {
				indexRs = getMetaData(connection).getIndexInfo(catalog, schema, table.getTableName(), false, true);
			}
			while (indexRs.next()) {
				String columnName = indexRs.getString("COLUMN_NAME");
				if (columnName != null) {
					_log.debug("index:" + columnName);
					indices.add(columnName);
				}

				// now look for unique columns
				String indexName = indexRs.getString("INDEX_NAME");
				boolean nonUnique = indexRs.getBoolean("NON_UNIQUE");

				if (!nonUnique && columnName != null && indexName != null) {
					List list = (List) uniqueColumns.get(indexName);
					if (list == null) {
						list = new ArrayList();
						uniqueColumns.put(indexName, list);
					}
					list.add(columnName);
					uniqueIndices.put(columnName, indexName);
					_log.debug("unique:" + columnName + " (" + indexName + ")");
				}
			}
		} catch (Throwable t) {
			// Bug #604761 Oracle getIndexInfo() needs major grants
			// http://sourceforge.net/tracker/index.php?func=detail&aid=604761&
			// group_id=36044&atid=415990
		} finally {
			if (indexRs != null) {
				indexRs.close();
			}
		}

		List columns = getTableColumns(connection, table, primaryKeys, indices, uniqueIndices, uniqueColumns);

		for (Object object : columns) {
			Column column = (Column) object;
			table.addColumn(column);
		}

		// In case none of the columns were primary keys, issue a warning.
		if (primaryKeys.size() == 0) {
			_log.warn("WARNING: The JDBC driver didn't report any primary key columns in " + table.getTableName());
		}
	}

	@SuppressWarnings("unchecked")
	private List getTableColumns(Connection connection, Table table, List primaryKeys, List indices, Map uniqueIndices, Map uniqueColumns) throws SQLException {
		// get the columns
		List columns = new LinkedList();
		ResultSet columnRs = getColumnsResultSet(connection, table);

		while (columnRs.next()) {
			int sqlType = columnRs.getInt("DATA_TYPE");
			String sqlTypeName = columnRs.getString("TYPE_NAME");
			String columnName = columnRs.getString("COLUMN_NAME");
			String columnDefaultValue = columnRs.getString("COLUMN_DEF");
			String remarks = columnRs.getString("REMARKS");
			// if columnNoNulls or columnNullableUnknown assume "not nullable"
			boolean isNullable = (DatabaseMetaData.columnNullable == columnRs.getInt("NULLABLE"));
			int size = columnRs.getInt("COLUMN_SIZE");
			int decimalDigits = columnRs.getInt("DECIMAL_DIGITS");

			boolean isPk = primaryKeys.contains(columnName);
			boolean isIndexed = indices.contains(columnName);
			String uniqueIndex = (String) uniqueIndices.get(columnName);
			List columnsInUniqueIndex = null;
			if (uniqueIndex != null) {
				columnsInUniqueIndex = (List) uniqueColumns.get(uniqueIndex);
			}

			boolean isUnique = columnsInUniqueIndex != null && columnsInUniqueIndex.size() == 1;
			if (isUnique) {
				_log.debug("unique column:" + columnName);
			}
			Column column = new Column(table, sqlType, sqlTypeName, columnName,
					size, decimalDigits, isPk, isNullable, isIndexed, isUnique,
					columnDefaultValue);
			column.setComment(remarks);
			columns.add(column);
		}
		columnRs.close();
		return columns;
	}

	private ResultSet getColumnsResultSet(Connection connection, Table table) throws SQLException {
		ResultSet columnRs;
		if (table.getOwnerSynonymName() != null) {
			columnRs = getMetaData(connection).getColumns(catalog, table.getOwnerSynonymName(), table.getTableName(), null);
		} else {
			columnRs = getMetaData(connection).getColumns(catalog, schema, table.getTableName(), null);
		}
		return columnRs;
	}

	private List<String> getTablePrimaryKeys(Connection connection, Table table) throws SQLException {
		// get the primary keys
		List<String> primaryKeys = new LinkedList<>();
		ResultSet primaryKeyRs;
		if (table.getOwnerSynonymName() != null) {
			primaryKeyRs = getMetaData(connection).getPrimaryKeys(catalog, table.getOwnerSynonymName(), table.getTableName());
		} else {
			primaryKeyRs = getMetaData(connection).getPrimaryKeys(catalog, schema, table.getTableName());
		}
		while (primaryKeyRs.next()) {
			String columnName = primaryKeyRs.getString("COLUMN_NAME");
			_log.debug("primary key:" + columnName);
			primaryKeys.add(columnName);
		}
		primaryKeyRs.close();
		return primaryKeys;
	}
}
