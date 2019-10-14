package com.wiki.framework.system.context.mybatis.Listener;

import com.wiki.framework.mybatis.mybatis.listener.spi.AbstractPOUpdateListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.POUpdateListener;
import com.wiki.framework.mybatis.po.CommonPO;
import com.wiki.framework.system.context.SystemContext;

import java.util.Date;

public class UserIdPOPreUpdateListener extends AbstractPOUpdateListener implements POUpdateListener.Pre {

	public static final int Order = Integer.MIN_VALUE;

	@Override
	public int getOrder() {
		return Order;
	}

	@Override
	public boolean shouldApply(CommonPO po) {
		return po != null;
	}

	@Override
	protected void doApply(CommonPO po) {
		if (po.getUpdateBy() == null) {
			po.setUpdateBy(SystemContext.getUserId());
		}
	}
}
