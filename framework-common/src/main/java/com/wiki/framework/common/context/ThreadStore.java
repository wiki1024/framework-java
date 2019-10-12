package com.wiki.framework.common.context;

import com.wiki.framework.common.util.Assertion;
import com.wiki.framework.common.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class ThreadStore {

	private transient static ThreadLocal<Map<String, String>> contextMap = new ThreadLocal<Map<String, String>>();

	private static Map<String, String> getContextMap() {
		Map<String, String> map = contextMap.get();
		if (map != null) {
			return map;
		} else {
			contextMap.set(new HashMap<>());
		}
		return contextMap.get();
	}

	private static String convertKey(String key) {
		return key.toLowerCase();
	}

	public static String put(String key, String value) {
		Assertion.isTrue(StringUtil.isNotBlank(key), "key cannot be null");
		Map<String, String> contextMap = getContextMap();
		return contextMap.put(convertKey(key), value);
	}

	public static String get(String key) {
		Assertion.isTrue(StringUtil.isNotBlank(key), "key cannot be null");
		Map<String, String> contextMap = getContextMap();
		return contextMap.get(convertKey(key));
	}

	public static String remove(String key) {
		Assertion.isTrue(StringUtil.isNotBlank(key), "key cannot be null");
		Map<String, String> contextMap = getContextMap();
		return contextMap.remove(convertKey(key));
	}

	public static boolean contains(String key) {
		Assertion.isTrue(StringUtil.isNotBlank(key), "key cannot be null");
		Map<String, String> contextMap = getContextMap();
		return contextMap.containsKey(convertKey(key));
	}

	public static void clean() {
		contextMap.remove();
	}
}
