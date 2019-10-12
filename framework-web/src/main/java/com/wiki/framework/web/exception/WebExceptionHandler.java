package com.wiki.framework.web.exception;

import com.wiki.framework.common.dto.ActionResult;

import javax.servlet.http.HttpServletRequest;

public interface WebExceptionHandler {

	ActionResult<String> handleException(HttpServletRequest request, Throwable e);
}
