package com.wiki.framework.web.autoconfig;

import com.wiki.framework.web.exception.DefaultExceptionHandler;
import com.wiki.framework.web.exception.WebExceptionHandler;
import com.wiki.framework.web.filter.CleanThreadStoreFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FrameworkWebConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public WebExceptionHandler defaultExceptionHandler() {
		return new DefaultExceptionHandler();
	}

	@Bean
	public FilterRegistrationBean cleanThreadStoreFilterRegistration() {
		CleanThreadStoreFilter cleanThreadStoreFilter = new CleanThreadStoreFilter();
		FilterRegistrationBean<CleanThreadStoreFilter> bean = new FilterRegistrationBean<>(cleanThreadStoreFilter);
		bean.addUrlPatterns("*");
		bean.setOrder(CleanThreadStoreFilter.Order);
		return bean;
	}
}
