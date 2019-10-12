package com.wiki.framework.mybatis.mybatis.listener.impl;


import com.wiki.framework.common.util.UUIDUtils;
import com.wiki.framework.mybatis.mybatis.listener.spi.PreInsertListener;
import com.wiki.framework.mybatis.po.CommonPO;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/1/2 上午10:43
 */
public class DefaultPreInsertListener implements PreInsertListener {
	@Override
	public boolean preInsert(Object object) {
		if (object == null) {
			return true;
		}

		if (object instanceof CommonPO) {
			CommonPO po = (CommonPO) object;
			if (StringUtils.isBlank(po.getId())) {
				po.setId(UUIDUtils.getUUID());
			}
			po.setVersion(0L);
			if (po.getCreateTime() == null) {
				po.setCreateTime(new Date());
			}
			if (po.getUpdateTime() == null) {
				po.setUpdateTime(new Date());
			}

			po.setIsDeleted(0);
		}

		return true;
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
