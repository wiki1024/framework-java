package com.wiki.framework.system.context.error;

import com.wiki.framework.common.error.BaseErrorCode;

public enum SystemContextErrorCode implements BaseErrorCode {


	TenenantIdNotPresented("0006_0001", "TenenantIdNotPresented"),
	IllegalArgument("0000_0010", ""),
	;

	String errorCode;
	String message;

	SystemContextErrorCode(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}


	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getMeessage() {
		return message;
	}
}

