package com.wiki.framework.mybatis.autoconfig;


import com.wiki.framework.mybatis.mybatis.listener.MybatisListenerContainer;
import com.wiki.framework.mybatis.mybatis.listener.impl.CommonDeleteListener;
import com.wiki.framework.mybatis.mybatis.listener.impl.CommonPOPreInsertListener;
import com.wiki.framework.mybatis.mybatis.listener.impl.CommonPOPreUpdateListener;
import com.wiki.framework.mybatis.mybatis.listener.impl.CommonQueryListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/1/2 下午12:41
 */
@Configuration
public class MybatisListenerAutoConfiguration {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;


	@PostConstruct
	public void afterPropertiesSet() throws Exception {
		MybatisListenerContainer.registListener(new CommonPOPreInsertListener());
		MybatisListenerContainer.registListener(new CommonPOPreUpdateListener());
		MybatisListenerContainer.registListener(new CommonQueryListener());
		MybatisListenerContainer.registListener(new CommonDeleteListener());
		applicationEventPublisher.publishEvent(new BaseMapperListenerRegisteredEvent(this));
	}
}
