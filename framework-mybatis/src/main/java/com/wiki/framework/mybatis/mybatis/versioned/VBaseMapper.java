package com.wiki.framework.mybatis.mybatis.versioned;

import com.wiki.framework.common.dto.ErrorInfo;
import com.wiki.framework.mybatis.mybatis.BaseMapper;
import com.wiki.framework.mybatis.mybatis.ErrorCode;
import com.wiki.framework.mybatis.po.CommonPO;

import java.util.Set;

public interface VBaseMapper<T extends CommonPO> extends BaseMapper<T> {

	@Override
	default int _doUpdate(T po, Set<String> columns) {
		int rows = this._update(po, true, columns);
		if(rows == 0){
			throw ErrorCode.OptimisticLockError.toException(po.getClass().getSimpleName(), po.getId(), po.getVersion());
		}
		return rows;
	}

	@Override
	default int _doUpdateSelective(T po, Set<String> columns) {
		int rows = this._updateSelective(po, true, columns);
		if(rows == 0){
			throw ErrorCode.OptimisticLockError.toException(po.getClass().getSimpleName(), po.getId(), po.getVersion());
		}
		return rows;
	}

}
