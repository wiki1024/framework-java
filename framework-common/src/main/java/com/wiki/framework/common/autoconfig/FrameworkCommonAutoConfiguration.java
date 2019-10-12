package com.wiki.framework.common.autoconfig;

import com.wiki.framework.common.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

@Configuration
public class FrameworkCommonAutoConfiguration {

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public void setApplicationContext() {
		SpringUtils.setApplicationContext(applicationContext);
	}
}
