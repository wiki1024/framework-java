package com.wiki.framework.web.client.rest.template.autoconfig;

import com.wiki.framework.web.client.rest.template.factory.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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

	@Configuration
	@ConditionalOnClass(LoadBalanced.class)
	public static class FrameworkLoadBalancedRestTemplateConfiguration {
		@Bean
		@Primary
		@LoadBalanced
		public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.setRequestFactory(clientHttpRequestFactory);
			return restTemplate;
		}
	}

	@Configuration
	@ConditionalOnMissingClass("org.springframework.cloud.client.loadbalancer.LoadBalanced")
	public static class FrameworkNormalRestTemplateConfiguration {
		@Bean
		@Primary
		public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.setRequestFactory(clientHttpRequestFactory);
			return restTemplate;
		}
	}

}
