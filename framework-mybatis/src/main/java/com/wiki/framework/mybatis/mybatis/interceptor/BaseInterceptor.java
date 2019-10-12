package com.wiki.framework.mybatis.mybatis.interceptor;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public abstract class BaseInterceptor<T> implements NamedInterceptor {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @param invocation call invocation.proceed if need to fo to next intercept other wise return int or any non-proxy
	 * @param context
	 * @return
	 */
	protected abstract Object doIntercept(Invocation invocation, T context) throws Throwable;

	protected abstract T preProcess(Invocation invocation);

	protected abstract boolean shouldApply(Invocation invocation, T context);

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	protected Object getObjFromMyBatisArgs(Object[] args, String key) {
		Object arg = args[1];
		if (arg instanceof Map) {
			Map argMap = (Map) arg;
			try {
				return argMap.get(key);
			} catch (BindingException be) {
				if (logger.isDebugEnabled()) {
					logger.error("expected mybatis exception", be);
				}
			}
		}
		return null;
	}

	protected List getPoListFromMyBatisArgs(Object[] args) {
		Object arg = args[1];
		if (arg instanceof Map) {
			Map argMap = (Map) arg;
			try {
				return (List) argMap.get("list");
			} catch (BindingException be) {
				if (logger.isDebugEnabled()) {
					logger.error("fuck mybatis", be);
				}
			}
		}
		return null;
	}

}
