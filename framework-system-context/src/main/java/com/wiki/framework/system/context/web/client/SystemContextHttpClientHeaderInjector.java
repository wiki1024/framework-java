package com.wiki.framework.system.context.web.client;

import com.wiki.framework.common.context.ThreadStore;
import com.wiki.framework.system.context.SystemConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SystemContextHttpClientHeaderInjector implements ClientHttpRequestInterceptor {

	private static Logger logger = LoggerFactory.getLogger(SystemContextHttpClientHeaderInjector.class);
	@Override
	public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
		HttpHeaders headers = httpRequest.getHeaders();
		addSystemContextToHeader(headers);
		return execution.execute(httpRequest,bytes);
	}

	/**
	 * 将当前系统的上下文变量放到http请求的header中
	 *
	 * @param headers http 请求header
	 */
	public static void addSystemContextToHeader(HttpHeaders headers) {
		if (logger.isDebugEnabled()) {
			logger.debug("preparing request header...");
		}
		if (headers == null) {
			return;
		}
		Map<String, String> contextMap = ThreadStore.getContextMap();
		if (contextMap == null) {
			return;
		}
		List<Pair<String, String>> pairs = convertSystemContext();
		if (CollectionUtils.isEmpty(pairs)) {
			return;
		}
		pairs.forEach(pair -> {
			headers.add(pair.getKey(), pair.getValue());
		});
	}

	/**
	 * 增加header,可被子类重写
	 *
	 * @param name     header name
	 * @param value    header value
	 * @param encoding need encoding
	 */
	private static Pair<String, String> convertKey(String name, String value, boolean encoding) {
		if (encoding) {
			try {
				return Pair.of(name, URLEncoder.encode(value, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				if (logger.isDebugEnabled()) {
					logger.error("can't convert value:" + value + " to utf-8,will set the raw value");
				}
				return Pair.of(name, value);
			}
		} else {
			return Pair.of(name, value);
		}
	}

	public static List<Pair<String, String>> convertSystemContext() {
		Map<String, String> contextMap = ThreadStore.getContextMap();
		if (contextMap == null) {
			return Collections.emptyList();
		}
		List<Pair<String, String>> pairs = new ArrayList<>();
		for (Map.Entry<String, String> entry : contextMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			if (StringUtils.isBlank(value)) {
				logger.warn("header:" + key + "'s value:" + value + " is empty,will not add to headers");
				continue;
			}
			if (!StringUtils.startsWithIgnoreCase(key, SystemConstant.CONTEXT_HEADER_PREFIX)) {
				continue;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("adding header{" + key + ":" + value + "}");
			}
			if (StringUtils.equalsIgnoreCase(key, SystemConstant.HEADER_ACCOUNT_NAME)
					|| StringUtils.equalsIgnoreCase(key, SystemConstant.HEADER_USER_NAME)) {
				pairs.add(convertKey(key, value, true));
			} else {
				pairs.add(convertKey(key, value, false));
			}
		}
		return pairs;
	}
}
