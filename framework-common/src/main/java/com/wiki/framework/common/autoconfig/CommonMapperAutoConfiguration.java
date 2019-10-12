package com.wiki.framework.common.autoconfig;

import com.wiki.framework.common.mapping.MapperFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonMapperAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public MapperFactoryBean createMapper() {
		MapperFactoryBean mapperFactoryBean = new MapperFactoryBean();
		return mapperFactoryBean;
	}
}
