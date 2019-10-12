package com.wiki.framework.web.client.rest.template.Proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 微服务 注解
 *
 * @author TIM(JT)
 * @date 2017-10-18 17
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ServiceProxy {
	/**
	 * 服务名称，用于从eureka中获取服务地址，如果写了serviceURL 则不从eureka中获取服务地址，直接连接目标服务
	 *
	 * @return 服务名称
	 */
	String serviceName() default "";

	/**
	 * 请求协议 默认http协议
	 * http,https
	 * @return http
	 */
	String protocol() default "http";

	/**
	 * restTemplateBeanName
	 *
	 * @return restTemplateBeanName
	 */
	String restTemplateBeanName() default "restTemplate";
}
