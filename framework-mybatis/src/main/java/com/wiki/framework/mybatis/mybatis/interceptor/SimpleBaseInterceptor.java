package com.wiki.framework.mybatis.mybatis.interceptor;

import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimpleBaseInterceptor implements NamedInterceptor {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (shouldApply(invocation)) {
			if (logger.isDebugEnabled()) {
				logger.debug("applied interceptor {}", getName());
			}
			return doIntercept(invocation);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("pass interceptor {}", getName());
		}
		return invocation.proceed();
	}

	/**
	 * @param invocation call invocation.proceed if need to fo to next intercept other wise return int or any non-proxy
	 * @return
	 */
	protected abstract Object doIntercept(Invocation invocation) throws Throwable;


	protected abstract boolean shouldApply(Invocation invocation);

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}
}
