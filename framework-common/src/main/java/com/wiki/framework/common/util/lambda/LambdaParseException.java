package com.wiki.framework.common.util.lambda;

public class LambdaParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LambdaParseException(String message) {
		super(message);
	}

	public LambdaParseException(Throwable throwable) {
		super(throwable);
	}

	public LambdaParseException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
