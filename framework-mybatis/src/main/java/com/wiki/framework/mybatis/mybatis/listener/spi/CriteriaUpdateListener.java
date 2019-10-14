package com.wiki.framework.mybatis.mybatis.listener.spi;

import com.wiki.framework.mybatis.mybatis.listener.OrderedListener;
import com.wiki.framework.mybatis.query.v2.Criteria;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface CriteriaUpdateListener extends OrderedListener {
	void apply(Class<?> clazz, Criteria criteria, Map<String, Object> map);

	/**
	 * 1, query
	 * 2, delete
	 *
	 * @return
	 */
	default int type() {
		return 1;
	}

	interface Delete extends CriteriaUpdateListener {
		@Override
		default int type() {
			return 2;
		}
	}
}
