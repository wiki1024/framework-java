package com.wiki.framework.web.client.rest.template.Factory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

public class ClientHttpRequestFactoryBuilder {

	public ClientHttpRequestFactory create() {
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		Integer poolSize = Runtime.getRuntime().availableProcessors() * 2 + 3;
		poolSize = poolSize < 4 ? 4 : poolSize;
		httpClientBuilder.setMaxConnTotal(poolSize * 10);
		httpClientBuilder.setMaxConnPerRoute(poolSize);
		httpClientBuilder.disableCookieManagement();
		httpClientBuilder.disableAuthCaching();
		httpClientBuilder.setConnectionTimeToLive(60, TimeUnit.SECONDS);

		RequestConfig.Builder builder = RequestConfig.custom();
		httpClientBuilder.setDefaultRequestConfig(builder.build());
		httpClientBuilder.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE);
		HttpClient httpClient = httpClientBuilder.build();
		ClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		return httpRequestFactory;
	}
}
