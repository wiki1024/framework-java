package com.wiki.framework.system.context;

import java.util.Locale;

/**
 * 定义框架系统常量的类
 *
 * @author Thomason
 * @version 1.2
 * @since 2009-03-10
 */
public interface SystemConstant {
	String GLOBAL_NS = "com.taimeitech";
	String DOT = ".";
	String FRAMEWORK_NS = GLOBAL_NS + DOT + "framework";

	String INTERNATIONALIZED_MSG_CODE = "__internationalized__";
	//默认的租户Id
	String DEFAULT_TENANT_ID = "default";
	//默认的项目Id
	String DEFAULT_PROJECT_ID = "default";
	//全局的国际化应用ID
	String I18N_GLOBAL_ID = "global";
	//全局的国际化应用名称
	String I18N_GLOBAL_NAME = "全局";
	/**
	 * 系统其它异常错误代码
	 */
	String SERVER_ERROR = "500";
	String SERVER_ERROR_TEXT = "server error";
	//*********************公共的true false 常量********************//
	Integer TRUE = 1;
	Integer FALSE = 0;
	//********************默认的区域******************************//
	String DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE.toString();
	//********************filter order******************************//
	int FilterOrder = Integer.MIN_VALUE;
	/***************公共header********************/
	String CONTEXT_HEADER_PREFIX = "TM-Header-";
	String HEADER_TOKEN = CONTEXT_HEADER_PREFIX + "Token";
	String ENVIRONMENT_TOKEN = CONTEXT_HEADER_PREFIX + "Environment-Token";
	/**
	 * 用户Id
	 */
	String HEADER_USER_ID = CONTEXT_HEADER_PREFIX + "UserId";
	/**
	 * 用户账号
	 */
	String HEADER_ACCOUNT_ID = CONTEXT_HEADER_PREFIX + "AccountId";
	/**
	 * 用户名称
	 */
	String HEADER_ACCOUNT_NAME = CONTEXT_HEADER_PREFIX + "AccountName";
	/**
	 * 用户姓名
	 */
	String HEADER_USER_NAME = CONTEXT_HEADER_PREFIX + "UserName";
	/**
	 * 客户端IP
	 */
	String HEADER_USER_IP = CONTEXT_HEADER_PREFIX + "UserIp";
	/**
	 * 客户端浏览信息
	 */
	String HEADER_USER_AGENT = CONTEXT_HEADER_PREFIX + "UserAgent";
	/**
	 * 租户Id
	 */
	String HEADER_TENANT_ID = CONTEXT_HEADER_PREFIX + "TenantId";
	/**
	 * 项目Id
	 */
	String HEADER_PROJECT_ID = CONTEXT_HEADER_PREFIX + "ProjectId";
	/**
	 * 应用Id
	 */
	String HEADER_APP_ID = CONTEXT_HEADER_PREFIX + "AppId";
	/**
	 * 区域和语言
	 */
	String HEADER_LOCALE = CONTEXT_HEADER_PREFIX + "Locale";
	/**
	 * 时区
	 */
	String HEADER_TIME_ZONE = CONTEXT_HEADER_PREFIX + "TimeZone";
	/**
	 * 系统域名
	 */
	String HEADER_SERVER_HOST = CONTEXT_HEADER_PREFIX + "ServerHost";
	/**
	 * 登录平台
	 */
	String HEADER_PLATFORM = CONTEXT_HEADER_PREFIX + "Platform";
	/**
	 * 设备型号
	 */
	String HEADER_DEVICE = CONTEXT_HEADER_PREFIX + "Device";
	/**
	 * TraceId
	 */
	String HEADER_TRACE_ID = CONTEXT_HEADER_PREFIX + "TraceId";
	/**
	 * NodeId
	 */
	String HEADER_NODE_ID = CONTEXT_HEADER_PREFIX + "NodeId";

	/**
	 * A new property source for apollo config
	 * */
	String APOLLO_BOOTSTRAP_PROPERTY_SOURCE_NAME = "ApolloBootstrapPropertySources";

	/**
	 * A switch of apollo feature
	 * */
	String TAIMEI_CONFIG_CENTER_ENABLED = "com.taimei.configCenter.enabled";

	/**
	 * local properties file path for using ConfigService(see com.ctrip.framework.apollo.internals.DefaultConfig)
	 * */
	String TAIMEI_CONFIG_CENTER_LOCAL_PROPERTIES = "com.taimei.configCenter.localProperties";

	String TRACE_LOGGER_NAME = "traceLogger";

	static String tokenName() {
		return "token";
	}
}
