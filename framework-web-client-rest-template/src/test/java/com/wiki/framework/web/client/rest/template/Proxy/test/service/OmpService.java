package com.wiki.framework.web.client.rest.template.Proxy.test.service;

import com.wiki.framework.common.dto.ActionResult;
import com.wiki.framework.web.client.rest.template.Proxy.ServiceProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@ServiceProxy(serviceName = "omp-service")
public interface OmpService {

	@GetMapping("/getApplication")
	ActionResult<List<ApplicationProxyDTO>> getApplication(@RequestParam("appId") List<String> appIds);
}
