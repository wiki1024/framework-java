package com.wiki.framework.web.client.rest.template.autoconfig;

import com.wiki.framework.web.client.rest.template.Factory.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FrameworkRestTemplateConfiguration {

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		return new ClientHttpRequestFactoryBuilder().create();
	}

}
