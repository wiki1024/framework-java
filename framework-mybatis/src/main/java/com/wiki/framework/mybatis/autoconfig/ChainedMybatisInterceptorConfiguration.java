package com.wiki.framework.mybatis.autoconfig;

import com.wiki.framework.mybatis.mybatis.interceptor.ChainedInterceptor;
import com.wiki.framework.mybatis.mybatis.interceptor.DefaultChainedInterceptor;
import com.wiki.framework.mybatis.mybatis.interceptor.SimpleBatchInterceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author Thomason
 * @version 1.0
 * @since 2017/6/19 16:32
 */
@Configuration
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class ChainedMybatisInterceptorConfiguration implements EnvironmentAware {

	private static String propertyPrefix = "mybatis.";


	private Environment environment;

	DefaultChainedInterceptor chainedInterceptor;

	@Bean
	public ChainedInterceptor chainedInterceptor() {
		chainedInterceptor = new DefaultChainedInterceptor();
		chainedInterceptor.addInterceptor(ChainedInterceptor.SIMPLE_BATCH_ORDER, new SimpleBatchInterceptor());
		return chainedInterceptor;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}
