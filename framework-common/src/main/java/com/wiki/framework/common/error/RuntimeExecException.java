package com.wiki.framework.common.error;

import com.wiki.framework.common.dto.ErrorInfo;
import com.wiki.framework.common.util.StringUtil;

public class RuntimeExecException extends RuntimeException {

	private ErrorInfo errorInfo;


	public RuntimeExecException(ErrorInfo errorInfo) {
		this.errorInfo = errorInfo;
	}

	@Override
	public String getMessage() {
		return StringUtil.format("code {}, error {}", errorInfo.getCode(), errorInfo.getFormatedMessage());
	}

	public ErrorInfo getErrorInfo() {
		return errorInfo;
	}

	public static RuntimeExecException fromError(ErrorInfo errorInfo) {
		return new RuntimeExecException(errorInfo);
	}
}
