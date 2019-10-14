package com.wiki.framework.common.util;

import com.wiki.framework.common.dto.ErrorInfo;
import com.wiki.framework.common.error.ErrorCode;
import com.wiki.framework.common.error.RuntimeExecException;

public class Assertion {

	public static void isTrue(boolean expression, String message, Object... args) {
		if (!expression) {
			ErrorInfo errorInfo = ErrorInfo.create(ErrorCode.IllegalArgument.getErrorCode(), message, args);
			throw RuntimeExecException.fromError(errorInfo);
		}
	}

	public static void isTrue(boolean expression, ErrorInfo errorInfo) {
		if (!expression) {
			throw RuntimeExecException.fromError(errorInfo);
		}
	}
}
