package com.wiki.framework.common.mapping;


import com.wiki.framework.common.mapping.config.MappingConfig;
import com.wiki.framework.common.mapping.config.PropertyConfig;
import com.wiki.framework.common.mapping.impl.DefaultMapper;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Thomason
 * Date: 11-4-20
 * Time: 下午9:41
 * @version 1.0
 */
@SuppressWarnings({"unchecked"})
public class MapperFactoryBean implements FactoryBean<Mapper>, InitializingBean {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private Resource[] configFiles;
	private DefaultMapper mapper;

	@Override
	public Mapper getObject() throws Exception {
		return mapper;
	}

	@Override
	public Class<Mapper> getObjectType() {
		return Mapper.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		mapper = new DefaultMapper();
		Map<String, MappingConfig> configMap = new HashMap<String, MappingConfig>();
		mapper.setConfigMap(configMap);
		if (logger.isDebugEnabled()) {
			logger.debug("映射文件全部加载完成");
		}
	}

}
