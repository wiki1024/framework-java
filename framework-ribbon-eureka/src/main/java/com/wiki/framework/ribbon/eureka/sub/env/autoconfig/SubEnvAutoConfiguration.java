package com.wiki.framework.ribbon.eureka.sub.env.autoconfig;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.loadbalancer.IRule;
import com.wiki.framework.ribbon.eureka.sub.env.SubEnvPropertiesFactory;
import com.wiki.framework.ribbon.eureka.sub.env.SubEnvRule;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@RibbonClients(defaultConfiguration = SubEnvAutoConfiguration.DefaultRibbionConfiguration.class)
public class SubEnvAutoConfiguration {

	@Bean
	public PropertiesFactory propertiesFactory() {
		return new SubEnvPropertiesFactory();
	}

	@Bean
	public SubEnvProperties subEnvProperties(){
		return  new SubEnvProperties();
	}

	@Configuration
	public static class DefaultRibbionConfiguration {

		@Bean
		public IRule ribbonRule() {
			SubEnvRule subEnvRule = new SubEnvRule();
			return subEnvRule;
		}
	}
}
