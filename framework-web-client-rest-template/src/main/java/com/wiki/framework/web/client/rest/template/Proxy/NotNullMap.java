package com.wiki.framework.web.client.rest.template.Proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/8/23 下午4:06
 */
public class NotNullMap<String, Object> extends HashMap<String, Object> {

	public NotNullMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public NotNullMap(int initialCapacity) {
		super(initialCapacity);
	}

	public NotNullMap() {
	}

	public NotNullMap(Map<? extends String, ? extends Object> m) {
		this.putAll(m);
	}

	@Override
	public Object put(String key, Object value) {
		if (value == null) {
			return super.put(key, (Object) "");
		} else {
			return super.put(key, value);
		}
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		if (m != null) {
			for (Entry<? extends String, ? extends Object> entry : m.entrySet()) {
				this.put(entry.getKey(), entry.getValue());
			}
		}
	}
}
