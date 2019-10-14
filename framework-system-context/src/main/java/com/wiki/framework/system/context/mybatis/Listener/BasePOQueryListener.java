package com.wiki.framework.system.context.mybatis.Listener;

import com.wiki.framework.common.util.Assertion;
import com.wiki.framework.common.util.ReflectionUtils;
import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.mybatis.mybatis.OrmErrorCode;
import com.wiki.framework.mybatis.mybatis.listener.impl.CommonQueryListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.AbstractCriteriaUpdateListener;
import com.wiki.framework.mybatis.query.v2.Criteria;
import com.wiki.framework.mybatis.query.v2.Operator;
import com.wiki.framework.system.context.SystemContext;
import com.wiki.framework.system.context.error.SystemContextErrorCode;
import com.wiki.framework.system.context.mybatis.po.BasePO;
import com.wiki.framework.system.context.mybatis.po.TenantPO;

import java.util.Map;

public class BasePOQueryListener extends AbstractCriteriaUpdateListener {

	//last
	public static final int Order = CommonQueryListener.Order - 1;

	@Override
	public int getOrder() {
		return Order;
	}

	@Override
	protected boolean shouldApply(Class<?> clazz) {
		return ReflectionUtils.isInterfaceOfRecur(clazz, TenantPO.class);
	}

	@Override
	protected void doApply(Class<?> clazz, Criteria criteria, Map<String, Object> map) {
		Assertion.isTrue(criteria != null, OrmErrorCode.NullCriteria.toError(clazz));
		String tenantId = SystemContext.getTenantId();
		Assertion.isTrue(StringUtil.isNotBlank(tenantId), SystemContextErrorCode.TenenantIdNotPresented);
		criteria.and(BasePO::getTenantId, Operator.equal, tenantId);
	}
}
