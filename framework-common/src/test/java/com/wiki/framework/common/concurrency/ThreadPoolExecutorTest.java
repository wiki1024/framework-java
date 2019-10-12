package com.wiki.framework.common.concurrency;

import com.wiki.framework.common.context.ThreadStore;
import com.wiki.framework.common.util.NamedThreadFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class ThreadPoolExecutorTest {

	private Logger logger = LoggerFactory.getLogger(ThreadPoolExecutorTest.class);

	@Test
	public void testOne() throws ExecutionException, InterruptedException {
		ThreadStore.put("kk-id","kk");
		ThreadPoolExecutor tttt = ThreadPoolExecutor.createThreadPool(1, new NamedThreadFactory("tttt"));
		logger.info("from main");

		Future<?> from_async = tttt.submit(() -> {
			logger.info("from async");
		});

		from_async.get();
	}

}