package com.wiki.framework.mybatis.mybatis.listener.impl;

import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.common.util.UUIDUtils;
import com.wiki.framework.mybatis.mybatis.listener.spi.AbstractPOUpdateListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.POUpdateListener;
import com.wiki.framework.mybatis.po.CommonPO;

import java.util.Date;

public class CommonPOPreInsertListener extends AbstractPOUpdateListener implements POUpdateListener.Insert, POUpdateListener.Pre {

	public static final int Order = Integer.MIN_VALUE;

	@Override
	public boolean shouldApply(CommonPO po) {
		return po != null;
	}

	@Override
	protected void doApply(CommonPO po) {
		if (StringUtil.isBlank(po.getId())) {
			po.setId(UUIDUtils.getUUID());
		}
		po.setVersion(0L);
		if (po.getCreateTime() == null) {
			po.setCreateTime(new Date());
		}
		if (po.getUpdateTime() == null) {
			po.setUpdateTime(new Date());
		}
		po.setIsDeleted(0);
	}

	@Override
	public int getOrder() {
		return Order;
	}
}
