package com.wiki.framework.common.concurrency;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class ThreadPoolExecutor extends ThreadPoolTaskExecutor {
	private static String THREAD_GROUP_NAME = "Framework-ThreadPool";

	private TaskProxyBuilder taskProxyBuilder = new TaskProxyBuilder.DefaultTaskProxyBuilder();

	private ThreadPoolExecutor() {
		super();
	}

	/**
	 * @see ThreadPoolExecutor#newThreadPool(int, int, int, int)
	 */
	public static ThreadPoolExecutor newThreadPool() {
		return newThreadPool(Integer.MAX_VALUE);
	}

	/**
	 * @see ThreadPoolExecutor#newThreadPool(int, int, int, int)
	 */
	public static ThreadPoolExecutor newThreadPool(int coreSize) {
		return newThreadPool(coreSize, coreSize);
	}

	/**
	 * @see ThreadPoolExecutor#newThreadPool(int, int, int, int)
	 */
	public static ThreadPoolExecutor newThreadPool(int coreSize, int maxSize) {
		return newThreadPool(coreSize, maxSize, Integer.MAX_VALUE);
	}

	/**
	 * @see ThreadPoolExecutor#newThreadPool(int, int, int, int)
	 */
	public static ThreadPoolExecutor newThreadPool(int coreSize, int maxSize, int queueCapacity) {
		return newThreadPool(coreSize, maxSize, queueCapacity, Integer.MAX_VALUE);
	}

	/**
	 * 创建新的线程池，如果线程池已经创建，返回已经创建的线程池
	 *
	 * @param coreSize         java.util.concurrent.ThreadPoolExecutor's coreSize
	 * @param maxSize          java.util.concurrent.ThreadPoolExecutor's maxSize
	 * @param queueCapacity    the capacity of the java.util.concurrent.ThreadPoolExecutor's BlockingQueue
	 * @param keepAliveSeconds the java.util.concurrent.ThreadPoolExecutor's keep-alive seconds the default value is 60
	 * @return 线程池
	 */
	public static ThreadPoolExecutor newThreadPool(int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds) {
		return createThreadPool(coreSize, maxSize, queueCapacity, keepAliveSeconds);
	}

	/**
	 * 创建新的线程池，如果线程池已经创建，返回已经创建的线程池
	 *
	 * @param nThreads      java.util.concurrent.ThreadPoolExecutor's coreSize
	 * @param threadFactory the ThreadFactory to use for the ThreadPoolExecutor's thread pool
	 * @return 线程池
	 */
	public static ThreadPoolExecutor createThreadPool(int nThreads, ThreadFactory threadFactory) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();
		threadPoolExecutor.setCorePoolSize(nThreads);
		threadPoolExecutor.setThreadFactory(threadFactory);
		threadPoolExecutor.afterPropertiesSet();
		threadPoolExecutor.setThreadGroupName(THREAD_GROUP_NAME);
		return threadPoolExecutor;
	}

	private static ThreadPoolExecutor createThreadPool(int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();
		threadPoolExecutor.setCorePoolSize(coreSize);
		threadPoolExecutor.setMaxPoolSize(maxSize);
		threadPoolExecutor.setQueueCapacity(queueCapacity);
		threadPoolExecutor.setKeepAliveSeconds(keepAliveSeconds);
		threadPoolExecutor.setThreadGroupName(THREAD_GROUP_NAME);
		threadPoolExecutor.afterPropertiesSet();
		return threadPoolExecutor;
	}

	public static ThreadPoolExecutor createThreadPool(int coreSize, int maxSize, int queueCapacity, int keepAliveSeconds, ThreadFactory threadFactory) {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor();
		threadPoolExecutor.setCorePoolSize(coreSize);
		threadPoolExecutor.setMaxPoolSize(maxSize);
		threadPoolExecutor.setQueueCapacity(queueCapacity);
		threadPoolExecutor.setKeepAliveSeconds(keepAliveSeconds);
		threadPoolExecutor.setThreadGroupName(THREAD_GROUP_NAME);
		threadPoolExecutor.afterPropertiesSet();
		threadPoolExecutor.setThreadFactory(threadFactory);
		return threadPoolExecutor;
	}

	/**
	 * 在执行异步线程之前，将当前线程的线程变量(SystemContext.getContextMap())
	 * 复制到runnable执行的线程中
	 *
	 * @see org.springframework.core.task.AsyncTaskExecutor#submit(java.lang.Runnable)
	 */
	@Override
	public void execute(final Runnable task) {
		super.execute(taskProxyBuilder.runnable(task));
	}

	/**
	 * 在执行异步线程之前，将当前线程的线程变量(SystemContext.getContextMap())
	 * 复制到runnable执行的线程中
	 *
	 * @see org.springframework.core.task.AsyncTaskExecutor#execute(java.lang.Runnable, long)
	 */
	@Override
	public void execute(final Runnable task, long startTimeout) {

		super.execute(taskProxyBuilder.runnable(task), startTimeout);
	}

	/**
	 * 在执行异步线程之前，将当前线程的线程变量(SystemContext.getContextMap())
	 * 复制到runnable执行的线程中
	 *
	 * @see org.springframework.core.task.AsyncTaskExecutor#submit(java.lang.Runnable)
	 */
	@Override
	public Future<?> submit(final Runnable task) {
		return super.submit(taskProxyBuilder.runnable(task));
	}

	/**
	 * 在执行异步线程之前，将当前线程的线程变量(SystemContext.getContextMap())
	 * 复制到runnable执行的线程中
	 *
	 * @see org.springframework.core.task.AsyncTaskExecutor#submit(java.util.concurrent.Callable)
	 */
	@Override
	public <T> Future<T> submit(final Callable<T> task) {
		return super.submit(taskProxyBuilder.callable(task));
	}

	public void setTaskProxyBuilder(TaskProxyBuilder taskProxyBuilder) {
		this.taskProxyBuilder = taskProxyBuilder;
	}
}