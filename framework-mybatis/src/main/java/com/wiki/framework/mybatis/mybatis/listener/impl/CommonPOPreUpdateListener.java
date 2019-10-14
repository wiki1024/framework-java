package com.wiki.framework.mybatis.mybatis.listener.impl;

import com.wiki.framework.mybatis.mybatis.listener.spi.AbstractPOUpdateListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.POUpdateListener;
import com.wiki.framework.mybatis.po.CommonPO;

import java.util.Date;

public class CommonPOPreUpdateListener extends AbstractPOUpdateListener implements POUpdateListener.Pre {

	public static final int Order = Integer.MIN_VALUE;

	@Override
	public boolean shouldApply(CommonPO po) {
		return po != null;
	}

	@Override
	protected void doApply(CommonPO po) {
		if (po.getUpdateTime() == null) {
			po.setUpdateTime(new Date());
		}
		if (po.getUpdateTime() == null) {
			po.setUpdateTime(new Date());
		}
		if (po.getIsDeleted() == null) {
			po.setIsDeleted(0);
		}
	}

	@Override
	public int getOrder() {
		return Order;
	}
}
