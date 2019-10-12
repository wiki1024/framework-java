package com.wiki.framework.common.dto;

import com.wiki.framework.common.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * err code, framework owns 0000_0000 to 9999_9999
 * @author Thomason
 * @version 1.0
 * @since 2017/5/23 17:19
 */
public class ErrorInfo implements Serializable {
	/**
	 * 消息是否做过国际化
	 */
	private boolean internationalized = false;
	/**
	 * 消息code
	 */
	private String code;

	private String message;

	private List<Object> arguments;

	public ErrorInfo() {
	}

	public ErrorInfo(String code) {
		this.code = code;
	}

	public ErrorInfo(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public ErrorInfo(Integer code, String message) {
		this.code = null != code ? code.toString() : null;
		this.message = message;
	}

	public static ErrorInfo create(String code, String message, Object... arguments) {
		ErrorInfo errorInfo = new ErrorInfo(code, message);
		if (arguments != null && arguments.length > 0) {
			errorInfo.arguments = Arrays.asList(arguments);
		}
		return errorInfo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Object> getArguments() {
		return arguments;
	}

	public void setArguments(List<Object> arguments) {
		this.arguments = arguments;
	}

	public boolean isInternationalized() {
		return internationalized;
	}

	public void setInternationalized(boolean internationalized) {
		this.internationalized = internationalized;
	}

	public String getFormatedMessage() {
		if (CollectionUtils.isEmpty(arguments)) return message;
		return StringUtil.format(message, arguments.toArray());
	}
}
