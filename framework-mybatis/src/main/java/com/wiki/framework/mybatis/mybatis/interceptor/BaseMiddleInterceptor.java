package com.wiki.framework.mybatis.mybatis.interceptor;

import org.apache.ibatis.plugin.Invocation;

/**
 * 执行完后向后传
 *
 * @param <T> context return by preprocess, more like a intermediate result
 */
public abstract class BaseMiddleInterceptor<T> extends BaseInterceptor<T> {


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		T context = preProcess(invocation);
		if (shouldApply(invocation, context)) {
			if (logger.isDebugEnabled()) {
				logger.debug("applied interceptor {}", getName());
			}
			doIntercept(invocation, context);
			return invocation.proceed();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("pass interceptor {}", getName());
		}
		return invocation.proceed();
	}

}
