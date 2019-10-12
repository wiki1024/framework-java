package com.wiki.framework.mybatis.mybatis.listener.impl;


import com.wiki.framework.mybatis.mybatis.listener.spi.PreDeleteListener;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/1/2 上午10:47
 */
public class DefaultPreDeleteListener implements PreDeleteListener {

	@Override
	public boolean preDelete(Object object) {
		return true;
	}
}
