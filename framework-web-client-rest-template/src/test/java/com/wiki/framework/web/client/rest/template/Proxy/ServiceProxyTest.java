package com.wiki.framework.web.client.rest.template.Proxy;

import com.wiki.framework.common.dto.ActionResult;
import com.wiki.framework.web.client.rest.template.Proxy.test.service.ApplicationProxyDTO;
import com.wiki.framework.web.client.rest.template.Proxy.test.service.OmpService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
public class ServiceProxyTest {

	private Logger logger = LoggerFactory.getLogger(ServiceProxyTest.class);

	@Autowired
	private OmpService ompService;


	@Test
	public void test1() {
		List<String> strings = new ArrayList<>();
		strings.add("edc");
		strings.add("pv");
		strings.add("iwrs");
		ActionResult<List<ApplicationProxyDTO>> result = ompService.getApplication(strings);
		List<ApplicationProxyDTO> data = result.getData();
		for (ApplicationProxyDTO datum : data) {
			logger.info("{}", datum);
		}
	}

}