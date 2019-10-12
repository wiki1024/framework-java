package com.wiki.framework.mybatis.dbinspector;

import java.sql.Types;
import java.util.HashMap;

/**
 * @author yangtao
 * @email walle1027@gmail.com
 */
public class DatabaseDataTypesUtils {

	private final static IntStringMap PREFERRED_JAVA_TYPE_FOR_SQL_TYPE = new IntStringMap();

	static {
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.TINYINT, "java.lang.Byte");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.SMALLINT, "java.lang.Short");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.INTEGER, "java.lang.Integer");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.BIGINT, "java.lang.Long");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.REAL, "java.lang.Float");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.FLOAT, "java.lang.Double");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.DOUBLE, "java.lang.Double");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.DECIMAL, "java.math.BigDecimal");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.NUMERIC, "java.math.BigDecimal");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.BIT, "java.lang.Boolean");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.CHAR, "java.lang.String");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.VARCHAR, "java.lang.String");
		// according to resultset.gif, we should use java.io.Reader, but String
		// is more convenient for EJB
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.LONGVARCHAR, "java.lang.String");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.BINARY, "byte[]");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.VARBINARY, "byte[]");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.LONGVARBINARY,
				"java.io.InputStream");

		// 根据项目需要，自己修改了类型对应关系
		// _preferredJavaTypeForSqlType.put(Types.DATE, "java.sql.Date");
		// _preferredJavaTypeForSqlType.put(Types.TIME, "java.sql.Time");
		// _preferredJavaTypeForSqlType.put(Types.TIMESTAMP,
		// "java.sql.Timestamp");

		// 把所有的时间类型都改成java.util.date类型的数据
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.DATE, "java.util.Date");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.TIME, "java.util.Date");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.TIMESTAMP, "java.util.Date");

		// 把clob，类型的数据全部改成String型
		// _preferredJavaTypeForSqlType.put(Types.CLOB, "java.sql.Clob");
		// _preferredJavaTypeForSqlType.put(Types.BLOB, "java.sql.Blob");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.CLOB, "java.lang.String");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.BLOB, "java.sql.Blob");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.NCLOB, "java.lang.String");

		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.ARRAY, "java.sql.Array");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.REF, "java.sql.Ref");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.STRUCT, "java.lang.Object");
		PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.put(Types.JAVA_OBJECT, "java.lang.Object");
	}

	public static boolean isFloatNumber(int sqlType, int size, int decimalDigits) {
		String javaType = getPreferredJavaType(sqlType, size, decimalDigits);
		if (javaType.endsWith("Float") || javaType.endsWith("Double")
				|| javaType.endsWith("BigDecimal")) {
			return true;
		}
		return false;
	}

	public static boolean isIntegerNumber(int sqlType, int size,
	                                      int decimalDigits) {
		String javaType = getPreferredJavaType(sqlType, size, decimalDigits);
		if (javaType.endsWith("Long") || javaType.endsWith("Integer")
				|| javaType.endsWith("Short")) {
			return true;
		}
		return false;
	}

	public static boolean isDate(int sqlType, int size, int decimalDigits) {
		String javaType = getPreferredJavaType(sqlType, size, decimalDigits);
		if (javaType.endsWith("Date") || javaType.endsWith("Timestamp")) {
			return true;
		}
		return false;
	}

	public static String getPreferredJavaType(int sqlType, int size,
	                                          int decimalDigits) {
		if ((sqlType == Types.DECIMAL || sqlType == Types.NUMERIC)
				&& decimalDigits == 0) {
			// 根据项目需求自己修改了数据类型，不再判断布尔，字节，短字节等类型
			// 如果有需要，去掉下面的注释
			// if (size == 1) {
			// https://sourceforge.net/tracker/?func=detail&atid=415993&aid=
			// 662953&group_id=36044
			// return "java.lang.Boolean";
			// } else if (size < 3) {
			// return "java.lang.Byte";
			// } else if (size < 5) {
			// return "java.lang.Short";
			// } else
			if (size < 10) {
				return "java.lang.Integer";
			} else if (size < 19) {
				return "java.lang.Long";
			} else {
				return "java.math.BigDecimal";
			}
		}
		String result = PREFERRED_JAVA_TYPE_FOR_SQL_TYPE.getString(sqlType);
		if (result == null) {
			// oracle 不识别nclob等数据类型 暂时不能识别的类型先处理成String
			// result = "java.lang.Object";
			result = "java.lang.String";
		}
		return result;
	}

	@SuppressWarnings({"serial", "unchecked"})
	private static class IntStringMap extends HashMap {

		public String getString(int i) {
			return (String) get(new Integer(i));
		}

		public String[] getStrings(int i) {
			return (String[]) get(new Integer(i));
		}

		@SuppressWarnings("unchecked")
		public void put(int i, String s) {
			put(new Integer(i), s);
		}

		public void put(int i, String[] sa) {
			put(new Integer(i), sa);
		}
	}

}
