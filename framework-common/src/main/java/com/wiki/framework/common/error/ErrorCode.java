package com.wiki.framework.common.error;


public enum ErrorCode implements BaseErrorCode {
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


	@Override
	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String getMeessage() {
		return message;
	}
}
