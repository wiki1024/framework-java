package com.wiki.framework.ribbon.eureka.sub.env;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.wiki.framework.common.context.ThreadStore;
import com.wiki.framework.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * match only when self and remote have the same subEnv,
 * return true when the subEnv is master
 */
public class ExactSubEnvPredicate extends SubEnvPredicate {

	private static Logger logger = LoggerFactory.getLogger(ExactSubEnvPredicate.class);
	private volatile String configTargetSubEnv = "__empty__";

	ApplicationInfoManager applicationInfoManager;
	private String contextKey;

	public ExactSubEnvPredicate(IRule rule, ApplicationInfoManager applicationInfoManager, String contextKey) {
		super(rule);
		this.applicationInfoManager = applicationInfoManager;
		this.contextKey = contextKey;
	}

	public ExactSubEnvPredicate(IRule rule, IClientConfig clientConfig, ApplicationInfoManager applicationInfoManager, String contextKey) {
		super(rule, clientConfig);
		this.applicationInfoManager = applicationInfoManager;
		this.contextKey = contextKey;
		initDynamicProperties(clientConfig);
	}

	private void initDynamicProperties(IClientConfig clientConfig) {
		configTargetSubEnv = clientConfig.getPropertyAsString(Constants.SUB_ENV_CONFIG_KEY, "__empty__");
		if (!configTargetSubEnv.equalsIgnoreCase("__empty__")) {
			if (logger.isDebugEnabled()) {
				logger.debug("set target env for {} as {} from ribbon config", clientConfig.getClientName(), configTargetSubEnv);
			}
			return;
		}
		Map<String, String> selfMeta = applicationInfoManager == null ? new HashMap<>() : applicationInfoManager.getInfo().getMetadata();
		if (selfMeta.containsKey(Constants.SUB_ENV_PROPERTY_KEY)) {
			configTargetSubEnv = selfMeta.get(Constants.SUB_ENV_PROPERTY_KEY);
			if (logger.isDebugEnabled()) {
				logger.debug("set target env for {} use {} from self meta", clientConfig.getClientName(), configTargetSubEnv);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("set target env for {} is empty", clientConfig.getClientName());
			}
		}
	}

	/**
	 * 优先走设置的子环境
	 * 如果没有设置子环境　=> 找header的子环境 => 默认master
	 *
	 * @param server
	 * @return
	 */
	boolean predicate(DiscoveryEnabledServer server) {
		String remoteSubEnv = extractRemoteSubEnv(server);
		String targetSubEnv = configTargetSubEnv;
		String sysSubEnv = ThreadStore.get(contextKey);
		if (targetSubEnv.equalsIgnoreCase("__empty__")) {
			if (StringUtil.isBlank(sysSubEnv)) {
				targetSubEnv = Constants.SUB_ENV_DEFAULT_ENV;
			} else {
				targetSubEnv = sysSubEnv;
			}
		}
		boolean matched = targetSubEnv.equalsIgnoreCase(remoteSubEnv);
		if (logger.isDebugEnabled()) {
			logger.debug("server matched {},config {}, header {}, target {}, remote {} - id {}", matched, configTargetSubEnv, sysSubEnv, targetSubEnv, remoteSubEnv, server.getInstanceInfo().getId());
		}
		return matched;
	}
}
