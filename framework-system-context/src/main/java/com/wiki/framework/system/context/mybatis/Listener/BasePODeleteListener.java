package com.wiki.framework.system.context.mybatis.Listener;

import com.wiki.framework.common.util.Assertion;
import com.wiki.framework.common.util.ReflectionUtils;
import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.mybatis.mybatis.OrmErrorCode;
import com.wiki.framework.mybatis.mybatis.listener.spi.AbstractCriteriaUpdateListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.CriteriaUpdateListener;
import com.wiki.framework.mybatis.query.v2.Criteria;
import com.wiki.framework.mybatis.query.v2.Operator;
import com.wiki.framework.system.context.SystemContext;
import com.wiki.framework.system.context.error.SystemContextErrorCode;
import com.wiki.framework.system.context.mybatis.po.TenantPO;

import java.util.Date;
import java.util.Map;

public class BasePODeleteListener extends AbstractCriteriaUpdateListener implements CriteriaUpdateListener.Delete {

	//last
	public static final int Order = Integer.MAX_VALUE;

	@Override
	public int getOrder() {
		return Order;
	}

	@Override
	protected boolean shouldApply(Class<?> clazz) {
		return ReflectionUtils.isInterfaceOf(clazz, TenantPO.class);
	}

	@Override
	protected void doApply(Class<?> clazz, Criteria criteria, Map<String, Object> map) {
		Assertion.isTrue(criteria != null, OrmErrorCode.NullCriteria.toError(clazz));
		String tenantId = SystemContext.getTenantId();
		Assertion.isTrue(StringUtil.isNotBlank(tenantId), SystemContextErrorCode.TenenantIdNotPresented);
		criteria.and(TenantPO::getTenantId, Operator.equal, tenantId);
		if (map != null) {
			map.put("updateBy", SystemContext.getUserId());
		}
	}
}
