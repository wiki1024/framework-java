package com.wiki.framework.system.context.autoconfig;

import com.wiki.framework.system.context.web.filter.SystemContextFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class SystemContextConfiguration {

	@Configuration
	@ConditionalOnClass(Filter.class)
	public static class SystemContextWebFilterConfiguration {

		@Bean
		public FilterRegistrationBean cleanThreadStoreFilterRegistration() {
			SystemContextFilter filter = new SystemContextFilter();
			FilterRegistrationBean<SystemContextFilter> bean = new FilterRegistrationBean<>(filter);
			bean.addUrlPatterns("*");
			bean.setOrder(SystemContextFilter.Order);
			return bean;
		}
	}
}
