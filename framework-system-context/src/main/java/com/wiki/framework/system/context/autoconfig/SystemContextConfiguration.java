package com.wiki.framework.system.context.autoconfig;

import com.wiki.framework.system.context.web.client.SystemContextHttpClientHeaderInjector;
import com.wiki.framework.system.context.web.filter.SystemContextFilter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class SystemContextConfiguration {

	@Configuration
	@ConditionalOnClass(Filter.class)
	public static class SystemContextWebFilterConfiguration {

		@Bean
		public FilterRegistrationBean systemContextFilterRegistration() {
			SystemContextFilter filter = new SystemContextFilter();
			FilterRegistrationBean<SystemContextFilter> bean = new FilterRegistrationBean<>(filter);
			bean.addUrlPatterns("*");
			bean.setOrder(SystemContextFilter.Order);
			return bean;
		}
	}

	@Configuration
	@ConditionalOnClass({ClientHttpRequestInterceptor.class, RestTemplate.class})
	public static class SystemContextHttpClientInterceptor {

		@Autowired(required = false)
		private List<RestTemplate> restTemplates = Collections.emptyList();

		@Bean
		public SystemContextHttpClientHeaderInjector systemContextHttpClientHeaderInjector() {
			return new SystemContextHttpClientHeaderInjector();
		}

		@Bean
		public InitializingBean restTemplateSystemContextInjectorInit(SystemContextHttpClientHeaderInjector systemContextHttpClientHeaderInjector) {
			return () -> {
				for (RestTemplate restTemplate : restTemplates) {
					List<ClientHttpRequestInterceptor> list = new ArrayList<>(
							restTemplate.getInterceptors());
					list.add(systemContextHttpClientHeaderInjector);
					restTemplate.setInterceptors(list);
				}
			};
		}

	}
}
