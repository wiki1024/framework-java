package com.wiki.framework.mybatis.mybatis.listener.spi;


import com.wiki.framework.mybatis.mybatis.listener.OrderedListener;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/1/2 上午10:26
 */
public interface PostInsertListener extends OrderedListener {
	void postInsert(Object object);
}
