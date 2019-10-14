package com.wiki.framework.mybatis.mybatis.listener.spi;

import com.wiki.framework.mybatis.query.v2.Criteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractCriteriaUpdateListener implements CriteriaUpdateListener {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	abstract protected boolean shouldApply(Class<?> clazz);

	abstract protected void doApply(Class<?> clazz, Criteria criteria,  Map<String, Object> map);

	@Override
	public void apply(Class<?> clazz, Criteria criteria, Map<String, Object> map) {
		if (shouldApply(clazz)) {
			if (logger.isDebugEnabled()) {
				logger.debug("applied listener");
			}
			doApply(clazz, criteria, map);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("skip listener");
			}
		}
	}
}
