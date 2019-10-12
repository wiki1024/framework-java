package com.wiki.framework.mybatis.mybatis.util;


import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.mybatis.query.v2.Condition;
import com.wiki.framework.mybatis.query.v2.Criteria;
import com.wiki.framework.mybatis.query.v2.Operator;

public class BaseMapperUtil {

	public static Criteria createIdCriteria(String id) {
		Criteria criteria = new Criteria();
		Condition condition = new Condition();
		if (StringUtil.isBlank(id)) {
			condition.setPropertyName("id");
			condition.setOperator(Operator.isNull);
		} else {
			condition.setPropertyName("id");
			condition.setOperator(Operator.equal);
			condition.setValue(id);
		}
		criteria.addCriterion(condition);
		return criteria;
	}
}
