package com.wiki.framework.web.client.rest.template.proxy;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/5/21 上午10:49
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import(ServiceProxyRegister.class)
public @interface ServiceProxyScan {
	/**
	 * Alias for the {@link #basePackages()} attribute.
	 */
	String[] value() default {};

	/**
	 * Base packages to scan for Micro interface.
	 */
	String[] basePackages() default {};

	/**
	 * alternative to {@link #basePackages()} for specifying the packages
	 * to scan for annotated components. The package of each class specified will be scanned.
	 */
	Class<?>[] basePackageClasses() default {};
}
