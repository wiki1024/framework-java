package com.wiki.framework.ribbon.eureka.sub.env;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.wiki.framework.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A predicate that is composed from one or more predicates in "AND" relationship.
 * It also has the functionality of "fallback" to one of more different predicates.
 * If the primary predicate yield too few filtered servers from the {@link #getEligibleServers(List, Object)}
 * API, it will try the fallback predicates one by one, until the number of filtered servers
 * exceeds certain number threshold or percentage threshold.
 *
 * @author awang
 */
public class ChainPredicate extends AbstractServerPredicate {

	private static Logger logger = LoggerFactory.getLogger(ChainPredicate.class);

	private AbstractServerPredicate delegate;

	private List<AbstractServerPredicate> fallbacks = Lists.newArrayList();

	private int minimalFilteredServers = 1;

	private float minimalFilteredPercentage = 0;

	private String clientName;

	private ApplicationInfoManager applicationInfoManager;

	public static Builder withPredicate(String clientName, AbstractServerPredicate primaryPredicate) {
		return new Builder(clientName, primaryPredicate);
	}

	public static Builder withPredicates(String clientName, AbstractServerPredicate... primaryPredicates) {
		return new Builder(clientName, primaryPredicates);
	}

	@Override
	public boolean apply(PredicateKey input) {
		return delegate.apply(input);
	}

	/**
	 * Get the filtered servers from primary predicate, and if the number of the filtered servers
	 * are not enough, trying the fallback predicates
	 */
	@Override
	public List<Server> getEligibleServers(List<Server> servers, Object loadBalancerKey) {
		String selfGroup = getSelfGroup(applicationInfoManager);
		String remoteGroup = extractGroup(servers);
		boolean strictMode = false;
		if (StringUtil.isNotBlank(selfGroup) && StringUtil.isNotBlank(remoteGroup) && selfGroup.equalsIgnoreCase(remoteGroup)) {
			//如果调用方和被调用方都在同一个组里，则为严格模式，不允许fallback
			strictMode = true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("self group {}, remote service {}, group {}, strict {}", selfGroup, clientName, remoteGroup, strictMode);
		}
		List<Server> result = super.getEligibleServers(servers, loadBalancerKey);
		if (!strictMode) {
			Iterator<AbstractServerPredicate> i = fallbacks.iterator();
			while (!(result.size() >= minimalFilteredServers && result.size() > (int) (servers.size() * minimalFilteredPercentage))
					&& i.hasNext()) {
				AbstractServerPredicate predicate = i.next();
				result = predicate.getEligibleServers(servers, loadBalancerKey);
			}
		}
		return result;
	}

	private String getSelfGroup(ApplicationInfoManager applicationInfoManager) {
		if (applicationInfoManager == null) return "";
		Map<String, String> metadata = applicationInfoManager.getInfo().getMetadata();
		if (metadata.containsKey(Constants.SUB_ENV_GROUP_PROPERTY_KEY)) {
			return metadata.get(Constants.SUB_ENV_GROUP_PROPERTY_KEY);
		}
		return "";
	}

	private String extractGroup(List<Server> servers) {
		String targetGroup = "";
		for (Server server : servers) {
			DiscoveryEnabledServer s = (DiscoveryEnabledServer) server;
			Map<String, String> metadata = s.getInstanceInfo().getMetadata();
			if (metadata.containsKey(Constants.SUB_ENV_GROUP_PROPERTY_KEY)) {
				targetGroup = metadata.get(Constants.SUB_ENV_GROUP_PROPERTY_KEY);
				break;
			}
		}
		return targetGroup;
	}

	public static class Builder {

		private ChainPredicate toBuild;

		Builder(String clientName, AbstractServerPredicate primaryPredicate) {
			toBuild = new ChainPredicate();
			toBuild.delegate = primaryPredicate;
			toBuild.clientName = clientName;
		}

		Builder(String clientName, AbstractServerPredicate... primaryPredicates) {
			toBuild = new ChainPredicate();
			toBuild.clientName = clientName;
			Predicate<PredicateKey> chain = Predicates.<PredicateKey>and(primaryPredicates);
			toBuild.delegate = AbstractServerPredicate.ofKeyPredicate(chain);
		}

		public Builder addFallbackPredicate(AbstractServerPredicate fallback) {
			toBuild.fallbacks.add(fallback);
			return this;
		}

		public Builder setFallbackThresholdAsMinimalFilteredNumberOfServers(int number) {
			toBuild.minimalFilteredServers = number;
			return this;
		}

		public Builder setFallbackThresholdAsMinimalFilteredPercentage(float percent) {
			toBuild.minimalFilteredPercentage = percent;
			return this;
		}

		public Builder setApplicationInfoManager(ApplicationInfoManager applicationInfoManager) {
			toBuild.applicationInfoManager = applicationInfoManager;
			return this;
		}

		public ChainPredicate build() {
			return toBuild;
		}
	}
}
