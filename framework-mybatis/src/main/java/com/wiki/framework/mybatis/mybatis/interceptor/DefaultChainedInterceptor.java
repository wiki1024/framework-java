package com.wiki.framework.mybatis.mybatis.interceptor;


import org.apache.commons.lang3.NotImplementedException;
import org.apache.ibatis.plugin.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

public class DefaultChainedInterceptor implements ChainedInterceptor {

	Logger logger = LoggerFactory.getLogger(DefaultChainedInterceptor.class);

	private SortedMap<Integer, NamedInterceptor> chain = new TreeMap<>();


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		throw new NotImplementedException("should never be called");
	}

	@Override
	public Object plugin(Object target) {
		Object proxy = target;
		for (Map.Entry<Integer, NamedInterceptor> entry : chain.entrySet()) {
			proxy = entry.getValue().plugin(proxy);
			if (logger.isDebugEnabled()) {
				logger.debug("plugin order {} for {}", entry.getKey(), entry.getValue().getName());
			}
		}
		return proxy;
	}

	@Override
	public void setProperties(Properties properties) {

	}

	@Override
	public void addInterceptor(int order, NamedInterceptor interceptor) {
		if (order < 0) {
			throw new NotImplementedException("order " + order + " should be positive");
		}
		if (chain.containsKey(order)) {
			throw new DuplicateOrderException("order " + order + " already exist");
		}
		chain.put(order, interceptor);
	}
}
