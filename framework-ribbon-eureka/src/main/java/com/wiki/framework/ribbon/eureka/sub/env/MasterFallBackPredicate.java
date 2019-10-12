package com.wiki.framework.ribbon.eureka.sub.env;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * only matches when remove env is master
 */
public class MasterFallBackPredicate extends SubEnvPredicate {

	private static Logger logger = LoggerFactory.getLogger(MasterFallBackPredicate.class);

	public MasterFallBackPredicate(IRule rule) {
		super(rule);
	}

	public MasterFallBackPredicate(IRule rule, IClientConfig clientConfig) {
		super(rule, clientConfig);
	}

	@Override
	boolean predicate(DiscoveryEnabledServer server) {
		String remoteSubEnv = extractRemoteSubEnv(server);
		boolean matched = Constants.SUB_ENV_DEFAULT_ENV.equalsIgnoreCase(remoteSubEnv);
		if (logger.isDebugEnabled()) {
			logger.debug("server matched {},  remote {} - id {}", matched, remoteSubEnv, server.getInstanceInfo().getId());
		}
		return matched;
	}
}
