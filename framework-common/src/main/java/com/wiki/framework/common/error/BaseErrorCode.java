package com.wiki.framework.common.error;

import com.wiki.framework.common.dto.ErrorInfo;

public interface  BaseErrorCode {


	String getErrorCode();
	String getMeessage();



	default ErrorInfo toError() {
		return new ErrorInfo(getErrorCode(), getMeessage());
	}

	default ErrorInfo toError(Object... args) {
		return ErrorInfo.create(getErrorCode(), getMeessage(), args);
	}

	default RuntimeExecException toException() {
		return RuntimeExecException.fromError(toError());
	}

	default RuntimeExecException toException(Object... args) {
		return RuntimeExecException.fromError(toError(args));
	}
}
