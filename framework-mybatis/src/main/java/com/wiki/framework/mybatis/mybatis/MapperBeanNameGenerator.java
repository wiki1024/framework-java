package com.wiki.framework.mybatis.mybatis;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/5/23 下午2:32
 */
public class MapperBeanNameGenerator extends AnnotationBeanNameGenerator {
	private Logger logger = LoggerFactory.getLogger(MapperBeanNameGenerator.class);

	@Override
	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		String beanName = super.generateBeanName(definition, registry);
		try {
			String beanClassName = definition.getBeanClassName();
			List<Method> methods = getMethods(Class.forName(beanClassName));
			if (CollectionUtils.isNotEmpty(methods)) {
				methods.stream().forEach(m -> {
					String mybatisStmtId = beanClassName + "." + m.getName();
					MybatisStmtIdContext.addIgnoreTenantStmtId(mybatisStmtId);
				});
			}
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return beanName;
	}

	private List<Method> getMethods(Class<?> clazz) {
		List<Method> methodList = new ArrayList<>();
		//递归获取接口的所有方法
		getInterfaceMethod(clazz, methodList);
		return methodList;
	}

	private void getInterfaceMethod(Class<?> clazz, List<Method> methodList) {
		Method[] methods = clazz.getDeclaredMethods();
		if (methods != null && methods.length > 0) {
			for (Method m : methods) {
				boolean contains = false;
				for (Method method : methodList) {
					if (method.getName().equals(m.getName())) {
						contains = true;
						break;
					}
				}
				if (!contains) {
					methodList.add(m);
				}
			}
		}
		Class<?>[] interfaces = clazz.getInterfaces();
		if (interfaces != null && interfaces.length > 0) {
			for (Class<?> interfaceClass : interfaces) {
				getInterfaceMethod(interfaceClass, methodList);
			}
		}
	}
}
