package com.wiki.framework.mybatis.mybatis.listener.impl;



import com.wiki.framework.mybatis.mybatis.listener.spi.PreUpdateListener;
import com.wiki.framework.mybatis.po.CommonPO;

import java.util.Date;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/1/2 上午10:47
 */
public class DefaultPreUpdateListener implements PreUpdateListener {
	@Override
	public boolean preUpdate(Object object) {
		if (object == null) {
			return true;
		}
		if (object instanceof CommonPO) {
			CommonPO po = (CommonPO) object;
			if (po.getUpdateTime() == null) {
				po.setUpdateTime(new Date());
			}
			if (po.getUpdateTime() == null) {
				po.setUpdateTime(new Date());
			}
			if (po.getIsDeleted() == null) {
				po.setIsDeleted(0);
			}
		}
		return true;
	}
}
