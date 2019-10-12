package com.wiki.framework.common.log4j;

import com.wiki.framework.common.context.ThreadStore;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.spi.ThreadContextMap;

import java.util.HashMap;
import java.util.Map;

public class ThreadStoreContextMap implements ThreadContextMap {
	@Override
	public void clear() {
		throw new NotImplementedException("no");
	}

	@Override
	public boolean containsKey(String key) {
		String s = ThreadStore.get(key);
		return s != null;
	}

	@Override
	public String get(String key) {
		return ThreadStore.get(key);
	}

	@Override
	public Map<String, String> getCopy() {
		Map<String, String> contextMap = ThreadStore.getContextMap();

		return contextMap == null ? new HashMap<>() : new HashMap<>(contextMap);
	}

	@Override
	public Map<String, String> getImmutableMapOrNull() {
		return ThreadStore.getIdMap();
	}

	@Override
	public boolean isEmpty() {
		return ThreadStore.getContextMap() == null && ThreadStore.getIdMap() == null;
	}

	@Override
	public void put(String key, String value) {
		ThreadStore.put(key, value);
	}

	@Override
	public void remove(String key) {
		ThreadStore.remove(key);
	}
}
