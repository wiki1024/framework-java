package com.wiki.framework.web.client.rest.template.proxy;

import com.wiki.framework.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/8/25 下午9:03
 */
public class ServiceProfile {

	private String serviceName;

	private String protocol;

	private RestTemplate restTemplate;

	private HttpMethod httpMethod;

	private String url;

	public ServiceProfile() {
	}

	public void check() {
		if (StringUtils.isBlank(serviceName)) {
			throw new RuntimeException("serviceName must be set at least");
		}
		Assert.notNull(protocol, "protocol can't be null");
		Assert.notNull(httpMethod, "httpMethod is null");
		Assert.hasText(url, "url is empty");
		Assert.notNull(restTemplate, "restTemplate is null");
	}

	public String getRequestUrl() {
		return protocol + "://" + StringUtil.formatPath(StringUtils.removeStart(serviceName, "/") + "/" + url);
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
