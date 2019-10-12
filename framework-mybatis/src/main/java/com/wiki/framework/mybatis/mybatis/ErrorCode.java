package com.wiki.framework.mybatis.mybatis;

import com.wiki.framework.common.dto.ErrorInfo;
import com.wiki.framework.common.error.RuntimeExecException;

public enum ErrorCode {

	OptimisticLockError("0005_0001", "OptimisticLockError po {}, id {}, intent version {}");

	String errorCode;
	String message;

	ErrorCode(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public ErrorInfo toError() {
		return new ErrorInfo(errorCode, message);
	}

	public ErrorInfo toError(Object... args) {
		return ErrorInfo.create(errorCode, message, args);
	}

	public RuntimeExecException toException() {
		return RuntimeExecException.fromError(toError());
	}

	public RuntimeExecException toException(Object... args) {
		return RuntimeExecException.fromError(toError(args));
	}
}
