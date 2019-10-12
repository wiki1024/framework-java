package com.wiki.framework.common.context;

import com.wiki.framework.common.util.Assertion;
import com.wiki.framework.common.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class ThreadStore {

	private transient static ThreadLocal<Map<String, String>> contextMap = new ThreadLocal<Map<String, String>>();
	private transient static ThreadLocal<Map<String, String>> idMap = new ThreadLocal<Map<String, String>>();

	public static void setContextMap(Map<String, String> contextMap) {
		Assertion.isTrue(contextMap != null, "id map cannot be null");
		ThreadStore.contextMap.set(contextMap);
	}

	public static void setIdMap(Map<String, String> idMap) {
		Assertion.isTrue(idMap != null, "id map cannot be null");
		ThreadStore.idMap.set(idMap);
	}

	public static Map<String, String> getContextMap() {
		Map<String, String> map = contextMap.get();
		if (map != null) {
			return map;
		} else {
			contextMap.set(new HashMap<>());
		}
		return contextMap.get();
	}

	public static Map<String, String> getIdMap() {
		Map<String, String> map = idMap.get();
		if (map != null) {
			return map;
		} else {
			idMap.set(new HashMap<>());
		}
		return idMap.get();
	}

	private static String convertKey(String key) {
		return key.toLowerCase();
	}

	public static String put(String key, String value) {
		Assertion.isTrue(StringUtil.isNotBlank(key), "key cannot be null");
		boolean isId = StringUtil.endsWithIgnoreCase(key, "Id");
		String convertKey = convertKey(key);
		return isId ? getIdMap().put(convertKey, value) : getContextMap().put(convertKey, value);
	}

	public static String get(String key) {
		Assertion.isTrue(StringUtil.isNotBlank(key), "key cannot be null");
		boolean isId = StringUtil.endsWithIgnoreCase(key, "Id");
		String convertKey = convertKey(key);
		return isId ? getIdMap().get(convertKey) : getContextMap().get(convertKey);
	}

	public static String remove(String key) {
		Assertion.isTrue(StringUtil.isNotBlank(key), "key cannot be null");
		boolean isId = StringUtil.endsWithIgnoreCase(key, "Id");
		String convertKey = convertKey(key);
		return isId ? getIdMap().remove(convertKey) : getContextMap().remove(convertKey);
	}

	public static boolean contains(String key) {
		Assertion.isTrue(StringUtil.isNotBlank(key), "key cannot be null");
		boolean isId = StringUtil.endsWithIgnoreCase(key, "Id");
		String convertKey = convertKey(key);
		return isId ? getIdMap().containsKey(convertKey) : getContextMap().containsKey(convertKey);
	}

	public static void clean() {
		contextMap.remove();
		idMap.remove();
	}
}
