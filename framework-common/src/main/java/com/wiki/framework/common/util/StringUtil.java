package com.wiki.framework.common.util;


import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.message.FormattedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Collection;

public class StringUtil extends StringUtils {

	private static final String SQL_REGEX = "('.+--)|(--)|(\\|)|(%7C)";
	private static Logger logger = LoggerFactory.getLogger(StringUtil.class);

	public static int stringToInt(String str) {
		int value = 0;
		if (str == null || str.length() == 0) {
			return value;
		}

		try {
			value = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
		}

		return value;
	}

	public static long stringToLong(String str) {
		long value = 0L;

		if (str == null || str.length() == 0) {
			return value;
		}

		try {
			value = Long.parseLong(str);
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
		}

		return value;
	}

	public static String encoderString(String srcStr) {
		String desStr = "";
		try {
			desStr = URLEncoder.encode(srcStr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return desStr;
	}

	public static String decodeString(String srcStr) {
		String desStr = "";

		if (!isEmpty(srcStr)) {
			try {
				desStr = java.net.URLDecoder.decode(srcStr, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return desStr;
	}

	public static String createRandomStr(boolean numberFlag, int length) {
		String retStr = "";
		String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
		int len = strTable.length();
		boolean bDone = true;
		do {
			retStr = "";
			int count = 0;
			for (int i = 0; i < length; i++) {
				double dblR = Math.random() * len;
				int intR = (int) Math.floor(dblR);
				char c = strTable.charAt(intR);
				if (('0' <= c) && (c <= '9')) {
					count++;
				}
				retStr += strTable.charAt(intR);
			}
			if (count >= 2) {
				bDone = false;
			}
		} while (bDone);

		return retStr;
	}

	public static Boolean isParamsEmpty(String... params) {
		for (int i = 0; i < params.length; i++) {
			if (isEmpty(params[i])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds '+' to decimal numbers that are positive (MySQL doesn't understand
	 * them otherwise
	 *
	 * @param dString The value as a string
	 * @return String the string with a '+' added (if needed)
	 */
	public static String fixDecimalExponent(String dString) {
		int ePos = dString.indexOf('E');

		if (ePos == -1) {
			ePos = dString.indexOf('e');
		}

		if (ePos != -1) {
			if (dString.length() > (ePos + 1)) {
				char maybeMinusChar = dString.charAt(ePos + 1);

				if (maybeMinusChar != '-' && maybeMinusChar != '+') {
					StringBuilder strBuilder = new StringBuilder(dString.length() + 1);
					strBuilder.append(dString.substring(0, ePos + 1));
					strBuilder.append('+');
					strBuilder.append(dString.substring(ePos + 1, dString.length()));
					dString = strBuilder.toString();
				}
			}
		}

		return dString;
	}

	/**
	 * Takes care of the fact that Sun changed the output of
	 * BigDecimal.toString() between JDK-1.4 and JDK 5
	 *
	 * @param decimal the big decimal to stringify
	 * @return a string representation of 'decimal'
	 */
	public static String consistentToString(BigDecimal decimal) {
		if (decimal == null) {
			return null;
		}

//        if (toPlainStringMethod != null) {
//            try {
//                return (String) toPlainStringMethod.invoke(decimal, (Object[]) null);
//            } catch (InvocationTargetException invokeEx) {
//                // that's okay, we fall-through to decimal.toString()
//            } catch (IllegalAccessException accessEx) {
//                // that's okay, we fall-through to decimal.toString()
//            }
//        }

		return decimal.toString();
	}

	public static String formatPath(String url) {
		if (url == null) {
			return null;
		}
		return url.replaceAll("/+/", "/");
	}

	/**
	 * 判断包含
	 *
	 * @param urls 授权url集合
	 * @param url  请求url
	 * @return 是否匹配
	 */
	public static boolean contains(Collection<String> urls, String url) {
		boolean flag = false;
		final String path = StringUtil.formatPath(url);
		for (String s : urls) {
			if (StringUtil.simpleWildcardMatch(s, path)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * 通配符匹配
	 *
	 * @param pattern 模式
	 * @param str     字符串
	 * @return 是否匹配
	 */
	public static boolean simpleWildcardMatch(String pattern, String str) {
		return wildcardMatch(pattern, str, "*");
	}

	/**
	 * 通配符匹配
	 *
	 * @param pattern  模式
	 * @param str      字符
	 * @param wildcard 通配符
	 * @return 是否匹配
	 */
	public static boolean wildcardMatch(String pattern, String str, String wildcard) {
		if (StringUtils.isEmpty(pattern) || StringUtils.isEmpty(str)) {
			return false;
		}
		final boolean startWith = pattern.startsWith(wildcard);
		final boolean endWith = pattern.endsWith(wildcard);
		String[] array = StringUtils.split(pattern, wildcard);
		int currentIndex = -1;
		int lastIndex = -1;
		switch (array.length) {
			case 0:
				return true;
			case 1:
				currentIndex = str.indexOf(array[0]);
				if (startWith && endWith) {
					return currentIndex >= 0;
				}
				if (startWith) {
					return currentIndex + array[0].length() == str.length();
				}
				if (endWith) {
					return currentIndex == 0;
				}
				return str.equals(pattern);
			default:
				for (String part : array) {
					currentIndex = str.indexOf(part);
					if (currentIndex > lastIndex) {
						lastIndex = currentIndex;
						continue;
					}
					return false;
				}
				return true;
		}
	}

	public static String escapeSql(String sql) {
		if (sql == null) {
			return null;
		}
		return sql.replaceAll(SQL_REGEX, "");
	}

	public static String format(String template, Object... objects) {
		return new FormattedMessage(template, objects).getFormattedMessage();
	}
}
