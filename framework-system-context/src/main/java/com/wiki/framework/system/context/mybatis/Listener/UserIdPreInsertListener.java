package com.wiki.framework.system.context.mybatis.Listener;

import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.common.util.UUIDUtils;
import com.wiki.framework.mybatis.mybatis.listener.impl.CommonPOPreInsertListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.AbstractPOUpdateListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.POUpdateListener;
import com.wiki.framework.mybatis.po.CommonPO;
import com.wiki.framework.system.context.SystemContext;

import java.util.Date;

public class UserIdPreInsertListener extends AbstractPOUpdateListener implements POUpdateListener.Insert, POUpdateListener.Pre {

	public static final int Order = CommonPOPreInsertListener.Order;

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
		if (po.getCreateBy() == null) {
			po.setCreateBy(SystemContext.getUserId());
		}
	}
	
}
