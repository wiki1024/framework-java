package com.wiki.framework.system.context.mybatis.Listener;

import com.wiki.framework.common.util.Assertion;
import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.mybatis.mybatis.listener.impl.CommonPOPreInsertListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.AbstractPOUpdateListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.POUpdateListener;
import com.wiki.framework.mybatis.po.CommonPO;
import com.wiki.framework.system.context.SystemContext;
import com.wiki.framework.system.context.error.SystemContextErrorCode;
import com.wiki.framework.system.context.mybatis.po.BasePO;
import com.wiki.framework.system.context.mybatis.po.TenantPO;

public class BasePOPreInsertListener extends AbstractPOUpdateListener implements POUpdateListener.Insert, POUpdateListener.Pre {

	public static final int Order = CommonPOPreInsertListener.Order + 1;

	@Override
	public int getOrder() {
		return Order;
	}

	@Override
	public boolean shouldApply(CommonPO po) {
		return po != null && po instanceof TenantPO;
	}

	@Override
	protected void doApply(CommonPO po) {
		TenantPO tenantPO = (TenantPO) po;
		if (StringUtil.isBlank(tenantPO.getTenantId())) {
			String tenantId = SystemContext.getTenantId();
			Assertion.isTrue(StringUtil.isNotBlank(tenantId), SystemContextErrorCode.TenenantIdNotPresented);
			tenantPO.setTenantId(tenantId);
		}
	}

}
