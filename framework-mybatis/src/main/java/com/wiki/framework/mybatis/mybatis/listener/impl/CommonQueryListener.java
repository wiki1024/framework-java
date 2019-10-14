package com.wiki.framework.mybatis.mybatis.listener.impl;

import com.wiki.framework.common.util.Assertion;
import com.wiki.framework.mybatis.mybatis.ErrorCode;
import com.wiki.framework.mybatis.mybatis.listener.spi.AbstractCriteriaUpdateListener;
import com.wiki.framework.mybatis.query.v2.Criteria;
import com.wiki.framework.mybatis.query.v2.Operator;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

public class CommonQueryListener extends AbstractCriteriaUpdateListener {

	//last
	public static final int Order = Integer.MAX_VALUE;

	@Override
	public int getOrder() {
		return Order;
	}

	@Override
	protected boolean shouldApply(Class<?> clazz) {
		return true;
	}

	@Override
	protected void doApply(Class<?> clazz, Criteria criteria,  Map<String, Object> map) {
		Assertion.isTrue(criteria != null, ErrorCode.NullCriteria.toError(clazz));
		//prevent 脱表
		Assertion.isTrue(CollectionUtils.isNotEmpty(criteria.getConditions()), ErrorCode.NoConditionPresented.toError(clazz));
		criteria.and("isDeleted", Operator.equal, "0");
	}


}
