package com.wiki.framework.system.context;

import com.wiki.framework.common.context.ThreadStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SystemContext {

	private static Logger logger = LoggerFactory.getLogger(SystemContext.class);

	public static String getTenantId() {
		return get(SystemConstant.HEADER_TENANT_ID);
	}

	public static void setTenantId(String tenantId) {
		put(SystemConstant.HEADER_TENANT_ID, tenantId);
	}

	public static String getUserId() { return get(SystemConstant.HEADER_USER_ID); }

	public static void setUserId(String userId) {
		put(SystemConstant.HEADER_USER_ID, userId);
	}

	public static String getAccountId() {
		return get(SystemConstant.HEADER_ACCOUNT_ID);
	}

	public static void setAccountId(String accountId) {
		put(SystemConstant.HEADER_ACCOUNT_ID, accountId);
	}

	public static String getAccountName() {
		return get(SystemConstant.HEADER_ACCOUNT_NAME);
	}

	public static void setAccountName(String accountName) {
		put(SystemConstant.HEADER_ACCOUNT_NAME, accountName);
	}

	public static String getProjectId() {
		return get(SystemConstant.HEADER_PROJECT_ID);
	}

	public static void setProjectId(String projectId) {
		put(SystemConstant.HEADER_PROJECT_ID, projectId);
	}

	public static String getAppId() {
		return get(SystemConstant.HEADER_APP_ID);
	}

	public static void setAppId(String appId) {
		put(SystemConstant.HEADER_APP_ID, appId);
	}

	public static void put(String key, String val) {
		ThreadStore.put(key, val);
	}

	public static String get(String key) {
		return ThreadStore.get(key);
	}
}
