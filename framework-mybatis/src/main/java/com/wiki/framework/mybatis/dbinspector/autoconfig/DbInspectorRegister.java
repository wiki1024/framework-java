package com.wiki.framework.mybatis.dbinspector.autoconfig;

import com.wiki.framework.mybatis.dbinspector.DbInspector;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/5/26 下午11:38
 */
public class DbInspectorRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

	private DbInspectorProperties dbInspectorProperties;

	@Override
	public void setEnvironment(Environment environment) {
		Binder binder = Binder.get(environment);
		dbInspectorProperties = new DbInspectorProperties();
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(DbInspect.class.getName()));
		List<String> basePackages = new ArrayList<String>();
		for (String pkg : annoAttrs.getStringArray("value")) {
			if (StringUtils.isNotBlank(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (String pkg : annoAttrs.getStringArray("basePackages")) {
			if (StringUtils.isNotBlank(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
			basePackages.add(ClassUtils.getPackageName(clazz));
		}
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
		beanDefinition.setNonPublicAccessAllowed(true);
		beanDefinition.setBeanClass(DbInspector.class);

		beanDefinition.getPropertyValues().add("packagesToScan", basePackages);

		beanDefinition.getPropertyValues().addPropertyValue("enabled", dbInspectorProperties.isEnabled());
		beanDefinition.getPropertyValues().addPropertyValue("async", dbInspectorProperties.isAsync());
		String ddlProcessorBeanName = dbInspectorProperties.getDdlProcessorBeanName();
		if (StringUtils.isNoneBlank(ddlProcessorBeanName)) {
			beanDefinition.getPropertyValues().addPropertyValue("ddlProcessor", new RuntimeBeanReference(ddlProcessorBeanName));
		}

		registry.registerBeanDefinition("dbInspector", beanDefinition);
	}
}
