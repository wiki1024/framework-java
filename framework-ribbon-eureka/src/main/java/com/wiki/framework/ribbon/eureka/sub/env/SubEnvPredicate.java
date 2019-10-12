package com.wiki.framework.ribbon.eureka.sub.env;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public abstract class SubEnvPredicate extends AbstractServerPredicate {

	private static Logger logger = LoggerFactory.getLogger(SubEnvPredicate.class);

	public SubEnvPredicate(IRule rule) {
		super(rule);
	}

	public SubEnvPredicate(IRule rule, IClientConfig clientConfig) {
		super(rule, clientConfig);
	}

	@Override
	public boolean apply(PredicateKey input) {
		return input != null
				&& input.getServer() instanceof DiscoveryEnabledServer
				&& predicate((DiscoveryEnabledServer) input.getServer());
	}


	String extractRemoteSubEnv(DiscoveryEnabledServer server) {
		String remoteSubEnv = Constants.SUB_ENV_DEFAULT_ENV;
		Map<String, String> remoteMeta = server.getInstanceInfo().getMetadata();
		if (remoteMeta.containsKey(Constants.SUB_ENV_PROPERTY_KEY)) {
			remoteSubEnv = remoteMeta.get(Constants.SUB_ENV_PROPERTY_KEY);
			if (logger.isDebugEnabled()) {
				logger.debug("predicate choose: remote env is {}", remoteSubEnv);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("predicate choose: remote env is master by default");
			}
		}
		return remoteSubEnv;
	}

	abstract boolean predicate(DiscoveryEnabledServer server);
}
