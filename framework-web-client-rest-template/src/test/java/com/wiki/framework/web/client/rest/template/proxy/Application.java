package com.wiki.framework.web.client.rest.template.proxy;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@ServiceProxyScan("com.wiki.framework.web.client.rest.template.proxy.test.service")
public class Application {


}
