package com.wiki.framework.web.exception;

import com.wiki.framework.common.dto.ActionResult;
import com.wiki.framework.common.error.ErrorCode;
import com.wiki.framework.common.error.RuntimeExecException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class DefaultExceptionHandler implements WebExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

	@ExceptionHandler
	@ResponseBody
	@Override
	public ActionResult<String> handleException(HttpServletRequest request, Throwable e) {
		logger.error("request:" + request.getRequestURI() + " occurs error => " + e.getMessage(), e);

		ActionResult<String> result = new ActionResult<>();
		result.setSuccess(false);

		try {
			if (e instanceof RuntimeExecException) {
				result.addError(((RuntimeExecException) e).getErrorInfo());
			} else {
				result.addError(ErrorCode.ServerError.toError());
			}
		} catch (Throwable ex) {
			logger.error("error hanlding error", ex);
			result.addError(ErrorCode.ServerError.toError());
		}
		return result;
	}
}
