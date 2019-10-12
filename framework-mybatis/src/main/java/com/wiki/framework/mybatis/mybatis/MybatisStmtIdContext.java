package com.wiki.framework.mybatis.mybatis;

import java.util.HashSet;
import java.util.Set;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/5/23 下午3:00
 */
public class MybatisStmtIdContext {
	private static final Set<String> ignoreTenantStmtIdSet = new HashSet<>();

	public static void addIgnoreTenantStmtId(String stmtId) {
		ignoreTenantStmtIdSet.add(stmtId);
	}

	public static boolean isIgnoreTenant(String stmtId) {
		return ignoreTenantStmtIdSet.contains(stmtId);
	}
}
