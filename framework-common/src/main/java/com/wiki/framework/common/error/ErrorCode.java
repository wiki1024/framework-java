package com.wiki.framework.common.error;

import com.wiki.framework.common.dto.ErrorInfo;

public enum ErrorCode {
	ServerError("500", "server error"),


	General("0000_0001", ""),
	IllegalArgument("0000_0010", ""),
	;

	String errorCode;
	String message;

	ErrorCode(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
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
