package com.wiki.framework.ribbon.eureka.sub.env;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import com.wiki.framework.common.util.SpringUtils;
import com.wiki.framework.ribbon.eureka.sub.env.autoconfig.SubEnvProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubEnvRule extends PredicateBasedRule {

	private static Logger logger = LoggerFactory.getLogger(SubEnvRule.class);
	private final SubEnvProperties subEnvProperties;

	private ChainPredicate compositePredicate;

	private ApplicationInfoManager applicationInfoManager;

	public SubEnvRule() {
		super();
		this.subEnvProperties = SpringUtils.getBean(SubEnvProperties.class);
		this.applicationInfoManager = SpringUtils.getBean(ApplicationInfoManager.class);
		this.compositePredicate = createCompositePredicate(applicationInfoManager);
	}


	private static AbstractServerPredicate alwaysTrue() {
		return new AbstractServerPredicate() {
			@Override
			public boolean apply(PredicateKey input) {
				if (logger.isDebugEnabled()) {
					logger.debug("allow cross sub env , fall back on always true ");
				}
				return true;
			}
		};
	}

	@Override
	public void initWithNiwsConfig(IClientConfig clientConfig) {
		super.initWithNiwsConfig(clientConfig);
		boolean allowCross = clientConfig.getPropertyAsBoolean(Constants.SUB_ENV_ALLOW_CROSS_CONFIG_KEY, false);
		if (logger.isDebugEnabled()) {
			logger.debug("allow cross : {} ", allowCross);
		}
		this.compositePredicate = createCompositePredicate(clientConfig, allowCross, applicationInfoManager);
	}

	private ChainPredicate createCompositePredicate(ApplicationInfoManager applicationInfoManager) {
		ZoneAvoidancePredicate zonePredicate = new ZoneAvoidancePredicate(this, null);
		ExactSubEnvPredicate exactSubEnvPredicate = new ExactSubEnvPredicate(this, applicationInfoManager, subEnvProperties.getContextKey());
		ChainPredicate.Builder builder = ChainPredicate.withPredicates("", zonePredicate, exactSubEnvPredicate);

		MasterFallBackPredicate masterFallBackPredicate = new MasterFallBackPredicate(this);
		AvailabilityP availabilityP = new AvailabilityP(this, null);
		builder.addFallbackPredicate(masterFallBackPredicate);
		return builder.build();
	}

	private ChainPredicate createCompositePredicate(IClientConfig clientConfig, boolean allowCross, ApplicationInfoManager applicationInfoManager) {

		ZoneAvoidancePredicate zonePredicate = new ZoneAvoidancePredicate(this, clientConfig);
		ExactSubEnvPredicate exactSubEnvPredicate = new ExactSubEnvPredicate(this, clientConfig, applicationInfoManager, subEnvProperties.getContextKey());
		ChainPredicate.Builder builder = ChainPredicate.withPredicates(clientConfig.getClientName(), zonePredicate, exactSubEnvPredicate);
		MasterFallBackPredicate masterFallBackPredicate = new MasterFallBackPredicate(this, clientConfig);
		builder.addFallbackPredicate(masterFallBackPredicate);
		if (allowCross) {
			AvailabilityP availabilityP = new AvailabilityP(this, clientConfig);
			builder
					.addFallbackPredicate(availabilityP)
					.addFallbackPredicate(alwaysTrue());
		}
		return builder.build();

	}

	@Override
	public AbstractServerPredicate getPredicate() {
		return compositePredicate;
	}

	public static class AvailabilityP extends AvailabilityPredicate {

		public AvailabilityP(IRule rule, IClientConfig clientConfig) {
			super(rule, clientConfig);
		}

		@Override
		public boolean apply(PredicateKey input) {
			boolean matched = super.apply(input);
			if (logger.isDebugEnabled()) {
				logger.debug("allow cross sub env, fall back on AvailabilityPredicate: {} ", matched);
			}
			return matched;
		}
	}


}
