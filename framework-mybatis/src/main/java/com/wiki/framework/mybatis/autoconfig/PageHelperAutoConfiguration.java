package com.wiki.framework.mybatis.autoconfig;

import com.github.pagehelper.PageInterceptor;
import com.wiki.framework.mybatis.mybatis.interceptor.ChainedInterceptor;
import com.wiki.framework.mybatis.mybatis.interceptor.NamedWrapperInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;

/**
 * 自定注入分页插件
 *
 * @author liuzh
 */
@Configuration
@ConditionalOnBean(SqlSessionFactory.class)
@EnableConfigurationProperties(PageHelperProperties.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class PageHelperAutoConfiguration implements EnvironmentAware {

	@Autowired
	private List<SqlSessionFactory> sqlSessionFactoryList;

	@Autowired
	private PageHelperProperties pageHelperProperties;

	@Resource
	private ChainedInterceptor chainedInterceptor;

	@Override
	public void setEnvironment(Environment environment) {
	}

	@PostConstruct
	public void addPageInterceptor() {
		PageInterceptor interceptor = new PageInterceptor();
		Properties properties = pageHelperProperties.getProperties();
		interceptor.setProperties(properties);
		chainedInterceptor.addInterceptor(ChainedInterceptor.PAGE_HELPER_ORDER, new NamedWrapperInterceptor("page-helper", interceptor));
	}

}

