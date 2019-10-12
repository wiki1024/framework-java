package com.wiki.framework.common.error;

import com.wiki.framework.common.dto.ErrorInfo;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 业务异常  所有自定义业务异常必须继承此类
 */
public class BusinessException extends RuntimeException implements Serializable {
	private List<ErrorInfo> errors;

	/**
	 */
	public BusinessException() {
		super();
	}

	/**
	 * @param errorInfos 错误消息
	 * @param throwable  抛出的异常
	 */
	public BusinessException(List<ErrorInfo> errorInfos, Throwable throwable) {
		super(throwable);
		this.errors = errorInfos;
	}

	/**
	 * @param errorInfos 错误消息
	 */
	public BusinessException(List<ErrorInfo> errorInfos) {
		super();
		this.errors = errorInfos;
	}

	/**
	 * @param errorInfo 错误消息
	 */
	public BusinessException(ErrorInfo... errorInfo) {
		super();
		this.errors = new ArrayList<>();
		this.errors.addAll(Arrays.asList(errorInfo));
	}


	/**
	 * @param errorInfo 错误消息
	 */
	protected void addErrors(ErrorInfo... errorInfo) {
		if (errors == null) {
			errors = new ArrayList<>();
		}
		this.errors.addAll(Arrays.asList(errorInfo));
	}

	public List<ErrorInfo> getErrors() {
		return errors;
	}

	@Override
	public String getMessage() {
		if (CollectionUtils.isNotEmpty(errors)) {
			StringBuilder builder = new StringBuilder();
			for (ErrorInfo errorInfo : errors) {
				builder.append(errorInfo.getCode()).append(":").append(errorInfo.getMessage());
				builder.append("\n");
			}
			if (builder.length() > 0) {
				builder.deleteCharAt(builder.length() - 1);
			}
			return builder.toString();
		}
		return null;
	}
}
