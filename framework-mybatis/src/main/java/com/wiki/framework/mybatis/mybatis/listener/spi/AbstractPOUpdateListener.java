package com.wiki.framework.mybatis.mybatis.listener.spi;

import com.wiki.framework.mybatis.po.CommonPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPOUpdateListener implements POUpdateListener {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	public abstract boolean shouldApply(CommonPO po);

	protected abstract void doApply(CommonPO po);

	@Override
	public void apply(CommonPO po) {
		if (shouldApply(po)) {
			if (logger.isDebugEnabled()) {
				logger.debug("applied listener");
			}
			doApply(po);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("skip listener");
			}
		}
	}
}
