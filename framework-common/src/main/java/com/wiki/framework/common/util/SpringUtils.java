package com.wiki.framework.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/7/18 下午1:26
 */
public class SpringUtils {

	public static ApplicationContext applicationContext;

	public static void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtils.applicationContext = applicationContext;
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(String beanName) {
		return (T) applicationContext.getBean(beanName);
	}
}
