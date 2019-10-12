package com.wiki.framework.common.log4j;

import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.PropertySource;

import java.util.Map;
import java.util.Properties;

public class Log4JPropertySource implements PropertySource {

	private static final String PREFIX = "log4j2.";

	private Properties properties = new Properties();

	public Log4JPropertySource() {
		properties.setProperty("log4j2.threadContextMap", "com.wiki.framework.common.log4j.ThreadStoreContextMap");
	}

	@Override
	public int getPriority() {
		return Integer.MIN_VALUE;
	}

	@Override
	public void forEach(BiConsumer<String, String> action) {
		for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
			action.accept(((String) entry.getKey()), ((String) entry.getValue()));
		}
	}

	@Override
	public CharSequence getNormalForm(Iterable<? extends CharSequence> tokens) {
		return PREFIX + Util.joinAsCamelCase(tokens);
	}
}
