package com.wiki.framework.mybatis.mybatis.listener.spi;

import com.wiki.framework.mybatis.mybatis.listener.OrderedListener;
import com.wiki.framework.mybatis.po.CommonPO;

public interface POUpdateListener extends OrderedListener {
	void apply(CommonPO po);

	/**
	 * 0 all
	 * 1, pre
	 * -1, after
	 *
	 * @return
	 */
	int direction();

	/**
	 * 1, update
	 * 2, insert
	 *
	 * @return
	 */
	default int type() {
		return 1;
	}

	interface Pre extends POUpdateListener {

		@Override
		default int direction() {
			return 1;
		}
	}

	interface Post extends POUpdateListener {

		@Override
		default int direction() {
			return -1;
		}
	}

	interface Insert extends POUpdateListener {

		@Override
		default int type() {
			return 2;
		}
	}
}
