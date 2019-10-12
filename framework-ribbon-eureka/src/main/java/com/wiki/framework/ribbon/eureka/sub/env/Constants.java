package com.wiki.framework.ribbon.eureka.sub.env;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.IClientConfigKey;

final class Constants {

	static final String SUB_ENV_PROPERTY_KEY = "subEnv";
	static final String SUB_ENV_GROUP_PROPERTY_KEY = "subEnv.group";
	static final IClientConfigKey<String> SUB_ENV_CONFIG_KEY = new CommonClientConfigKey<String>(SUB_ENV_PROPERTY_KEY){};
	static final IClientConfigKey<Boolean> SUB_ENV_ALLOW_CROSS_CONFIG_KEY = new CommonClientConfigKey<Boolean>("allowSubEnvCross"){};
	static final String SUB_ENV_DEFAULT_ENV = "master";

}
