package com.wiki.framework.common.util.lambda;

/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * ClassUtils
 * </p>
 *
 * @author Caratacus
 * @author HCL
 * @since 2017/07/08
 */
final class ClassUtils {

	private static final char PACKAGE_SEPARATOR = '.';

	/**
	 * 代理 class 的名称
	 */
	private static final List<String> PROXY_CLASS_NAMES = Arrays.asList("net.sf.cglib.proxy.factory"
			// cglib
			, "org.springframework.cglib.proxy.factory"
			, "javassist.util.proxy.ProxyObject"
			// javassist
			, "org.apache.ibatis.javassist.util.proxy.ProxyObject");

	private ClassUtils() {
	}

	/**
	 * <p>
	 * 判断是否为代理对象
	 * </p>
	 *
	 * @param clazz 传入 class 对象
	 * @return 如果对象class是代理 class，返回 true
	 */
	public static boolean isProxy(Class<?> clazz) {
		if (clazz != null) {
			for (Class<?> cls : clazz.getInterfaces()) {
				if (PROXY_CLASS_NAMES.contains(cls.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * <p>
	 * 获取当前对象的 class
	 * </p>
	 *
	 * @param clazz 传入
	 * @return 如果是代理的class，返回父 class，否则返回自身
	 */
	public static Class<?> getUserClass(Class<?> clazz) {
		return isProxy(clazz) ? clazz.getSuperclass() : clazz;
	}

	/**
	 * <p>
	 * 获取当前对象的class
	 * </p>
	 *
	 * @param object 对象
	 * @return 返回对象的 user class
	 */
	public static Class<?> getUserClass(Object object) {
		Assert.notNull(object, "Error: Instance must not be null");
		return getUserClass(object.getClass());
	}


	/**
	 * <p>
	 * 请仅在确定类存在的情况下调用该方法
	 * </p>
	 *
	 * @param name 类名称
	 * @return 返回转换后的 Class
	 */
	public static Class<?> toClassConfident(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new LambdaParseException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法", e);
		}
	}


	/**
	 * Determine the name of the package of the given class,
	 * e.g. "java.lang" for the {@code java.lang.String} class.
	 *
	 * @param clazz the class
	 * @return the package name, or the empty String if the class
	 * is defined in the default package
	 */
	public static String getPackageName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return getPackageName(clazz.getName());
	}

	/**
	 * Determine the name of the package of the given fully-qualified class name,
	 * e.g. "java.lang" for the {@code java.lang.String} class name.
	 *
	 * @param fqClassName the fully-qualified class name
	 * @return the package name, or the empty String if the class
	 * is defined in the default package
	 */
	public static String getPackageName(String fqClassName) {
		Assert.notNull(fqClassName, "Class name must not be null");
		int lastDotIndex = fqClassName.lastIndexOf(PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "");
	}
}
