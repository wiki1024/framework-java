package com.wiki.framework.mybatis.mybatis;

import com.wiki.framework.common.util.ReflectionUtils;
import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.mybatis.database.Column;
import com.wiki.framework.mybatis.database.Table;
import com.wiki.framework.mybatis.po.CommonPO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Invocation;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.wiki.framework.mybatis.mybatis.MybatisSqlBuilder.BLANK;


public interface BatchOperation {
	SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	SimpleDateFormat tsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

	default BatchType getBatchType(Invocation invocation) {
		final Object[] args = invocation.getArgs();
		final MappedStatement ms = (MappedStatement) args[0];
		SqlSource sqlSource = ms.getSqlSource();
		BoundSql boundSql = sqlSource.getBoundSql(args[1]);
		String sql = boundSql.getSql();
		MappedStatement mappedStatement = MybatisUtils.copyFromNewSql(ms, boundSql, sql);
		args[0] = mappedStatement;
		try {
			BatchType batchType = BatchType.valueOf(sql);
			return batchType;
		} catch (IllegalArgumentException ex) {
			return BatchType.None;
		}
	}

	default UpdateType getUpdateType(Invocation invocation) {
		final Object[] args = invocation.getArgs();
		final MappedStatement ms = (MappedStatement) args[0];
		SqlSource sqlSource = ms.getSqlSource();
		return getUpdateType(sqlSource);
	}

	default UpdateType getUpdateType(SqlSource sqlSource) {
		if (sqlSource instanceof ProviderSqlSource) {
			ProviderSqlSource providerSqlSource = (ProviderSqlSource) sqlSource;
			Class<?> providerType = (Class<?>) ReflectionUtils.getFieldValue(providerSqlSource, "providerType");
			Method providerMethod = (Method) ReflectionUtils.getFieldValue(providerSqlSource, "providerMethod");
			if ("com.taimeitech.framework.common.mybatis.MybatisSqlBuilder".equals(providerType.getName()) || ("com.taimeitech.framework.sharding.mybatis.ShardingSqlBuilder".equals(providerType.getName()))) {

				if ((Objects.equals(providerMethod.getName(), "insert"))) {
					return UpdateType.Insert;
				}
				if ((Objects.equals(providerMethod.getName(), "update"))) {
					return UpdateType.Update;
				}
				if ((Objects.equals(providerMethod.getName(), "updateSelective"))) {
					return UpdateType.UpdateSelective;
				}
				if ((Objects.equals(providerMethod.getName(), "delete"))) {
					return UpdateType.Delete;
				}
				if ((Objects.equals(providerMethod.getName(), "deleteByCriteria"))) {
					return UpdateType.DeleteByCondition;
				}
				if ((Objects.equals(providerMethod.getName(), "deleteByCriteria"))) {
					return UpdateType.DeleteByCriteria;
				}
				if ((Objects.equals(providerMethod.getName(), "batchInsert") || Objects.equals(providerMethod.getName(), "_batchInsert"))) {
					return UpdateType.BatchInsert;
				}
				if ((Objects.equals(providerMethod.getName(), "batchUpdateSelective"))) {
					return UpdateType.BatchUpdateSelective;
				}
			}
		}
		return UpdateType.None;
	}

	default int doOneBatchInsert(Connection conn, List poList, List<Column> columns, String sql) throws SQLException {
		int[] resultArr = null;
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Object o : poList) {
				preparePoForInsert(ps, (CommonPO) o, columns);
			}
			resultArr = ps.executeBatch();
		}
		if (resultArr != null) {
			int sum = 0;
			for (int i : resultArr) {
				sum = sum + i;
			}
			return sum;
		} else {
			return 0;
		}
	}

	default void preparePoForInsert(PreparedStatement ps, CommonPO po, List<Column> columns) throws SQLException {
		for (int i = 0; i < columns.size(); i++) {
			Column column = columns.get(i);
			ps.setObject(i + 1, ReflectionUtils.getFieldValue(po, column.getJavaName()), column.getSqlType());
		}
		ps.addBatch();
	}

	default String buildUpdateSelective(List<Column> columns, StringBuilder builder, String tableSqlName,
	                                    CommonPO po, Table table, Set<String> includeColumns) {
		boolean hasInclude = CollectionUtils.isNotEmpty(includeColumns);
		builder.append("update")
				.append(BLANK)
				.append(tableSqlName)
				.append(BLANK)
				.append("set").append(BLANK);
		if (table.hasVersionColumn()) {
			Column versionColumn = table.getVersionColumn();
			builder.append(versionColumn.getSqlName()).append(BLANK).append("=").append(BLANK)
					.append(versionColumn.getSqlName()).append(" + 1 ,");
		}
		for (Column column : columns) {
			if (!column.isUpdatable()) {
				continue;
			}
			Object fieldValue = ReflectionUtils.getFieldValue(po, column.getJavaName());
			boolean include = false;
			if (hasInclude && includeColumns.contains(column.getJavaName())) {
				include = true;
			} else if (fieldValue != null) {
				include = true;
			}
			if (include) {
				builder.append(BLANK);
				builder.append(column.getSqlName()).append("=").append(getSqlForVal(fieldValue, column)).append(",");
			}
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(BLANK).append("where").append(BLANK);
		builder.append(" id = ").append("'").append(po.getId()).append("'");
		return builder.toString();
	}

	default String buildUpdate(List<Column> columns, StringBuilder builder, String tableSqlName,
	                           CommonPO po, Table table, Set<String> includeColumns) {
		boolean hasInclude = CollectionUtils.isNotEmpty(includeColumns);
		builder.append("update")
				.append(BLANK)
				.append(tableSqlName)
				.append(BLANK)
				.append("set").append(BLANK);
		if (table.hasVersionColumn()) {
			Column versionColumn = table.getVersionColumn();
			builder.append(versionColumn.getSqlName()).append(BLANK).append("=").append(BLANK)
					.append(versionColumn.getSqlName()).append(" + 1 ,");
		}
		for (Column column : columns) {
			if (!column.isUpdatable()) {
				continue;
			}
			boolean include = false;
			if (hasInclude) {
				if (includeColumns.contains(column.getJavaName())) {
					include = true;
				}
			} else {
				include = true;
			}
			if (include) {
				Object fieldValue = ReflectionUtils.getFieldValue(po, column.getJavaName());
				builder.append(BLANK);
				builder.append(column.getSqlName()).append("=").append(getSqlForVal(fieldValue, column)).append(",");
			}
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(BLANK).append("where").append(BLANK);
		builder.append(" id = ").append("'").append(po.getId()).append("'");
		return builder.toString();
	}

	default String buildInsertSql(String tableSqlName, List<Column> columns, StringBuilder builder) {
		builder.append("insert into");
		builder.append(BLANK);
		builder.append(tableSqlName);
		builder.append(BLANK).append("(");
		for (Column column : columns) {
			if (column.isInsertable()) {
				builder.append(BLANK).append(column.getSqlName()).append(",");
			}
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append(")").append(BLANK).append("values").append(BLANK).append("(");
		for (Column column : columns) {
			if (column.isInsertable()) {
				builder.append(BLANK).append("?").append(",");
			}
		}
		builder.deleteCharAt(builder.length() - 1).append(")");
		return builder.toString();
	}

	default String getSqlForVal(Object val, Column column) {

		if (val == null) {
			return "null";
		}

		return "'" + StringUtils.replace(getSqlForNotNullValue(val, column), "'", "''") + "'";
	}

	default String getSqlForNotNullValue(Object val, Column column) {
		try {
			switch (column.getSqlType()) {
				case Types.BOOLEAN:

					if (val instanceof Boolean) {
						return getBoolean((Boolean) val);

					} else if (val instanceof String) {
						return getBoolean("true".equalsIgnoreCase((String) val) || !"0".equalsIgnoreCase((String) val));

					} else if (val instanceof Number) {
						int intValue = ((Number) val).intValue();

						return getBoolean(intValue != 0);

					} else {
						throw new RuntimeException("No conversion from " + val.getClass().getName() + " to Types.BOOLEAN possible.");
					}

				case Types.BIT:
				case Types.TINYINT:
				case Types.SMALLINT:
				case Types.INTEGER:
				case Types.BIGINT:
				case Types.REAL:
				case Types.FLOAT:
				case Types.DOUBLE:
				case Types.DECIMAL:
				case Types.NUMERIC:
					int scale = 0;
					if (val instanceof BigDecimal) {
						scale = ((BigDecimal) val).scale();
					}
					return getNumericObject(val, column.getSqlType(), scale);


				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
					if (val instanceof BigDecimal) {
						return ((StringUtil.fixDecimalExponent(StringUtil.consistentToString((BigDecimal) val))));
					} else {
						return (val.toString());
					}


				case Types.BINARY:
				case Types.VARBINARY:
				case Types.LONGVARBINARY:
				case Types.BLOB:
				case Types.CLOB:
					throw new RuntimeException("No conversion from " + val.getClass().getName() + " to BINARY, VARBINARY, LONGVARBINARY, BLOB, CLOB .");


				case Types.DATE:
				case Types.TIMESTAMP:

					java.util.Date parameterAsDate;

					if (val instanceof String) {
						ParsePosition pp = new ParsePosition(0);
						java.text.DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String) val, false), Locale.US);
						parameterAsDate = sdf.parse((String) val, pp);

					} else {
						parameterAsDate = (java.util.Date) val;
					}

					switch (column.getSqlType()) {
						case Types.DATE:
							if (parameterAsDate instanceof java.sql.Date) {
								return ddf.format(parameterAsDate);
							} else {
								return ddf.format(new java.sql.Date(parameterAsDate.getTime()));
							}
						case Types.TIMESTAMP:

							if (parameterAsDate instanceof Timestamp) {
								return tsdf.format((Timestamp) parameterAsDate) + ".0";
							} else {
								return tsdf.format(new Timestamp(parameterAsDate.getTime())) + ".0";
							}

					}

					break;

				case Types.TIME:

					if (val instanceof String) {
						java.text.DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String) val, true), Locale.US);

						return (new Time(sdf.parse((String) val).getTime())).toString();

					} else if (val instanceof Timestamp) {
						Timestamp xT = (Timestamp) val;
						return (new Time(xT.getTime())).toString();
					} else {
						return val.toString();
					}

				default:
					throw new RuntimeException("type not supported" + val.getClass().getName());
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		throw new RuntimeException("invalid column for getSqlValue " + val.getClass().getName());
	}

	default String getNumericObject(Object val, int sqlType, int scale) {
		Number parameterAsNum;
		if (val instanceof Boolean) {
			return (Boolean) val ? "1" : "0";
		} else if (val instanceof String) {
			switch (sqlType) {
				case Types.BIT:
					if ("1".equals(val) || "0".equals(val)) {
						parameterAsNum = Integer.valueOf((String) val);
					} else {
						boolean parameterAsBoolean = "true".equalsIgnoreCase((String) val);

						parameterAsNum = parameterAsBoolean ? Integer.valueOf(1) : Integer.valueOf(0);
					}

					break;

				case Types.TINYINT:
				case Types.SMALLINT:
				case Types.INTEGER:
					parameterAsNum = Integer.valueOf((String) val);

					break;

				case Types.BIGINT:
					parameterAsNum = Long.valueOf((String) val);

					break;

				case Types.REAL:
					parameterAsNum = Float.valueOf((String) val);

					break;

				case Types.FLOAT:
				case Types.DOUBLE:
					parameterAsNum = Double.valueOf((String) val);

					break;

				case Types.DECIMAL:
				case Types.NUMERIC:
				default:
					parameterAsNum = new BigDecimal((String) val);
			}
		} else {
			parameterAsNum = (Number) val;
		}

		switch (sqlType) {
			case Types.BIT:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
				return String.valueOf(parameterAsNum.intValue());


			case Types.BIGINT:
				return String.valueOf(parameterAsNum.longValue());


			case Types.REAL:
				return StringUtil.fixDecimalExponent(String.valueOf(parameterAsNum.floatValue()));


			case Types.FLOAT:
			case Types.DOUBLE:
				return StringUtil.fixDecimalExponent(String.valueOf(parameterAsNum.doubleValue()));


			case Types.DECIMAL:
			case Types.NUMERIC:

				if (parameterAsNum instanceof BigDecimal) {
					BigDecimal scaledBigDecimal = null;

					try {
						scaledBigDecimal = ((BigDecimal) parameterAsNum).setScale(scale);
					} catch (ArithmeticException ex) {
						try {
							scaledBigDecimal = ((BigDecimal) parameterAsNum).setScale(scale, BigDecimal.ROUND_HALF_UP);
						} catch (ArithmeticException arEx) {
							throw new RuntimeException("Can't set scale of '" + scale + "' for DECIMAL argument '" + parameterAsNum + "'");
						}
					}

					return StringUtil.fixDecimalExponent(StringUtil.consistentToString(scaledBigDecimal));
				} else if (parameterAsNum instanceof java.math.BigInteger) {
					return StringUtil.fixDecimalExponent(StringUtil.consistentToString(new BigDecimal((java.math.BigInteger) parameterAsNum, scale)));
				} else {
					return StringUtil.fixDecimalExponent(StringUtil.consistentToString(new BigDecimal(parameterAsNum.doubleValue())));
				}
		}
		throw new RuntimeException("convert to number fail " + val.getClass().getName() + "   " + val.toString());
	}

	default String getBoolean(boolean b) {
		return b ? "1" : "0";
	}

	default String getDateTimePattern(String dt, boolean toTime) throws Exception {
		//
		// Special case
		//
		int dtLength = (dt != null) ? dt.length() : 0;

		if ((dtLength >= 8) && (dtLength <= 10)) {
			int dashCount = 0;
			boolean isDateOnly = true;

			for (int i = 0; i < dtLength; i++) {
				char c = dt.charAt(i);

				if (!Character.isDigit(c) && (c != '-')) {
					isDateOnly = false;

					break;
				}

				if (c == '-') {
					dashCount++;
				}
			}

			if (isDateOnly && (dashCount == 2)) {
				return "yyyy-MM-dd";
			}
		}

		//
		// Special case - time-only
		//
		boolean colonsOnly = true;

		for (int i = 0; i < dtLength; i++) {
			char c = dt.charAt(i);

			if (!Character.isDigit(c) && (c != ':')) {
				colonsOnly = false;

				break;
			}
		}

		if (colonsOnly) {
			return "HH:mm:ss";
		}

		int n;
		int z;
		int count;
		int maxvecs;
		char c;
		char separator;
		StringReader reader = new StringReader(dt + " ");
		ArrayList<Object[]> vec = new ArrayList<Object[]>();
		ArrayList<Object[]> vecRemovelist = new ArrayList<Object[]>();
		Object[] nv = new Object[3];
		Object[] v;
		nv[0] = Character.valueOf('y');
		nv[1] = new StringBuilder();
		nv[2] = Integer.valueOf(0);
		vec.add(nv);

		if (toTime) {
			nv = new Object[3];
			nv[0] = Character.valueOf('h');
			nv[1] = new StringBuilder();
			nv[2] = Integer.valueOf(0);
			vec.add(nv);
		}

		while ((z = reader.read()) != -1) {
			separator = (char) z;
			maxvecs = vec.size();

			for (count = 0; count < maxvecs; count++) {
				v = vec.get(count);
				n = ((Integer) v[2]).intValue();
				c = getSuccessor(((Character) v[0]).charValue(), n);

				if (!Character.isLetterOrDigit(separator)) {
					if ((c == ((Character) v[0]).charValue()) && (c != 'S')) {
						vecRemovelist.add(v);
					} else {
						((StringBuilder) v[1]).append(separator);

						if ((c == 'X') || (c == 'Y')) {
							v[2] = Integer.valueOf(4);
						}
					}
				} else {
					if (c == 'X') {
						c = 'y';
						nv = new Object[3];
						nv[1] = (new StringBuilder(((StringBuilder) v[1]).toString())).append('M');
						nv[0] = Character.valueOf('M');
						nv[2] = Integer.valueOf(1);
						vec.add(nv);
					} else if (c == 'Y') {
						c = 'M';
						nv = new Object[3];
						nv[1] = (new StringBuilder(((StringBuilder) v[1]).toString())).append('d');
						nv[0] = Character.valueOf('d');
						nv[2] = Integer.valueOf(1);
						vec.add(nv);
					}

					((StringBuilder) v[1]).append(c);

					if (c == ((Character) v[0]).charValue()) {
						v[2] = Integer.valueOf(n + 1);
					} else {
						v[0] = Character.valueOf(c);
						v[2] = Integer.valueOf(1);
					}
				}
			}

			int size = vecRemovelist.size();

			for (int i = 0; i < size; i++) {
				v = vecRemovelist.get(i);
				vec.remove(v);
			}

			vecRemovelist.clear();
		}

		int size = vec.size();

		for (int i = 0; i < size; i++) {
			v = vec.get(i);
			c = ((Character) v[0]).charValue();
			n = ((Integer) v[2]).intValue();

			boolean bk = getSuccessor(c, n) != c;
			boolean atEnd = (((c == 's') || (c == 'm') || ((c == 'h') && toTime)) && bk);
			boolean finishesAtDate = (bk && (c == 'd') && !toTime);
			boolean containsEnd = (((StringBuilder) v[1]).toString().indexOf('W') != -1);

			if ((!atEnd && !finishesAtDate) || (containsEnd)) {
				vecRemovelist.add(v);
			}
		}

		size = vecRemovelist.size();

		for (int i = 0; i < size; i++) {
			vec.remove(vecRemovelist.get(i));
		}

		vecRemovelist.clear();
		v = vec.get(0); // might throw exception

		StringBuilder format = (StringBuilder) v[1];
		format.setLength(format.length() - 1);

		return format.toString();
	}

	default char getSuccessor(char c, int n) {
		return ((c == 'y') && (n == 2)) ? 'X'
				: (((c == 'y') && (n < 4)) ? 'y' : ((c == 'y') ? 'M' : (((c == 'M') && (n == 2)) ? 'Y'
				: (((c == 'M') && (n < 3)) ? 'M' : ((c == 'M') ? 'd' : (((c == 'd') && (n < 2)) ? 'd' : ((c == 'd') ? 'H' : (((c == 'H') && (n < 2))
				? 'H' : ((c == 'H') ? 'm'
				: (((c == 'm') && (n < 2)) ? 'm' : ((c == 'm') ? 's' : (((c == 's') && (n < 2)) ? 's' : 'W'))))))))))));
	}

}
