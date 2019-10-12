package com.wiki.framework.ribbon.eureka.sub.env;

import com.netflix.loadbalancer.*;
import com.wiki.framework.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


public class SubEnvPropertiesFactory extends PropertiesFactory {

	static final String NAMESPACE = "ribbon";

	@Autowired
	private Environment environment;

	private Map<Class, String> classToProperty = new HashMap<>();

	public SubEnvPropertiesFactory() {
		classToProperty.put(ILoadBalancer.class, "NFLoadBalancerClassName");
		classToProperty.put(IPing.class, "NFLoadBalancerPingClassName");
		classToProperty.put(IRule.class, "NFLoadBalancerRuleClassName");
		classToProperty.put(ServerList.class, "NIWSServerListClassName");
		classToProperty.put(ServerListFilter.class, "NIWSServerListFilterClassName");
	}

	public boolean isSet(Class clazz, String name) {
		return StringUtils.hasText(getClassName(clazz, name));
	}


	@Override
	public String getClassName(Class clazz, String name) {
		if (this.classToProperty.containsKey(clazz)) {
			String classNameProperty = this.classToProperty.get(clazz);
			String className = environment.getProperty(name + "." + NAMESPACE + "." + classNameProperty);
			if (StringUtil.isBlank(className)) {
				className = environment.getProperty(NAMESPACE + "." + classNameProperty);
			}
			return className;
		}
		return null;
	}
}
