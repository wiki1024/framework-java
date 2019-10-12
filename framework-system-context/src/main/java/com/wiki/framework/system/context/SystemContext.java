package com.wiki.framework.system.context;

import com.wiki.framework.common.context.ThreadStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SystemContext {

	private static Logger logger = LoggerFactory.getLogger(SystemContext.class);


	public static String getTenantId() {
		return ThreadStore.get(SystemConstant.HEADER_TENANT_ID);
	}

	public static void setTenantId(String tenantId) {
		ThreadStore.put(SystemConstant.HEADER_TENANT_ID, tenantId);
	}

	public static void put(String key, String val) {
		ThreadStore.put(key, val);
	}
}
