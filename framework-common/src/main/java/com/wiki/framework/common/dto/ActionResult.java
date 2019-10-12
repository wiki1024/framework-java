package com.wiki.framework.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActionResult<T> implements Serializable {

	private boolean success;
	/**
	 * 错误消息
	 */
	private List<ErrorInfo> errors;
	/**
	 * 返回数据
	 */
	private T data;

	public ActionResult() {
		this.success = true;
	}

	public ActionResult(boolean success, T data, String errorMessage, String errorCode) {
		this.success = success;
		this.data = data;
		ErrorInfo error = new ErrorInfo(errorCode, errorMessage);
		this.errors = Collections.singletonList(error);
	}

	public ActionResult(boolean success, List<ErrorInfo> errors, T data) {
		this.success = success;
		this.errors = errors;
		this.data = data;
	}

	public ActionResult(boolean success, List<ErrorInfo> errors) {
		this.success = success;
		this.errors = errors;
	}

	public ActionResult(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

	public ActionResult(T data) {
		this.success = true;
		this.data = data;
	}

	public void addError(ErrorInfo errorInfo) {
		this.success = false;
		if (errors == null) {
			errors = new ArrayList<>();
		}
		errors.add(errorInfo);
	}

	public void addErrors(List<ErrorInfo> errorInfoList) {
		if (errorInfoList != null && errorInfoList.size() > 0) {
			for (ErrorInfo errorInfo : errorInfoList) {
				this.addError(errorInfo);
			}
		}
	}

	public void addError(String errorCode, String errorMsg) {
		ErrorInfo errorInfo = new ErrorInfo(errorCode, errorMsg);
		this.addError(errorInfo);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<ErrorInfo> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorInfo> errors) {
		this.errors = errors;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}