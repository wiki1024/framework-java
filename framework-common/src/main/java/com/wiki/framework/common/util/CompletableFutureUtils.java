package com.wiki.framework.common.util;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompletableFutureUtils {

	private static final ScheduledExecutorService SCHEDULER =
			Executors.newScheduledThreadPool(1, new NamedThreadFactory("framework-timeout-scheduler"));

	/**
	 * 给一个CompletableFuture 加上超时
	 *
	 * @param future
	 * @param duration
	 * @param <T>
	 * @return
	 */
	public static <T> CompletableFuture<T> within(CompletableFuture<T> future, Duration duration) {
		final CompletableFuture<T> timeout = failAfter(duration);
		return future.applyToEitherAsync(timeout, Function.identity());
	}

	/**
	 * 返回一个CompletableFuture, 在duration后失败
	 *
	 * @param duration
	 * @param <T>
	 * @return
	 */
	public static <T> CompletableFuture<T> failAfter(Duration duration) {
		final CompletableFuture<T> promise = new CompletableFuture<>();
		SCHEDULER.schedule(() -> {
			final TimeoutException ex = new TimeoutException("Timeout after " + duration);
			return promise.completeExceptionally(ex);
		}, duration.toMillis(), TimeUnit.MILLISECONDS);
		return promise;
	}

	/**
	 * 执行一个lambda， 制定超时时间
	 *
	 * @param supplier
	 * @param duration
	 * @param realWorkExecutor
	 * @param waitExecutor
	 * @param <T>
	 * @return
	 */
	public static <T> CompletableFuture<T> supplyAsyncWithTimeout(Supplier<? extends T> supplier, long duration, ThreadPoolTaskExecutor realWorkExecutor, Executor waitExecutor) {
		CompletableFuture<T> result = new CompletableFuture<>();
		CompletableFuture.supplyAsync(() -> {
			try {
				Future<T> future = realWorkExecutor.submit(supplier::get);
				SCHEDULER.schedule(() -> future.cancel(true), duration, TimeUnit.MILLISECONDS);
				T val = future.get();
				result.complete(val);
			} catch (InterruptedException | CancellationException e) {
				final TimeoutException ex = new TimeoutException("Timeout after " + duration);
				result.completeExceptionally(ex);
			} catch (Exception ex) {
				result.completeExceptionally(ex);
			}
			return null;
		}, waitExecutor);
		return result;
	}

	/**
	 * thenApplyAsync的超时版本，
	 *
	 * @param source   源CompletableFuture
	 * @param fn       在源CompletableFuture执行完成后执行的lambda， 如果该lambda没有io操作，则推荐使用jdk原生方法thenApplyAsync，注意指定线程池
	 * @param duration
	 * @param <T>
	 * @param <U>
	 * @return
	 */
	public static <T, U> CompletableFuture<U> applyAsyncWithTimeout(CompletableFuture<T> source, Function<? super T, ? extends U> fn, long duration, ThreadPoolTaskExecutor realWorkExecutor, Executor waitExecutor) {
		CompletableFuture<U> result = new CompletableFuture<>();
		source.thenApplyAsync((t) -> {
			try {
				Future<U> future = realWorkExecutor.submit(() -> fn.apply(t));
				SCHEDULER.schedule(() -> future.cancel(true), duration, TimeUnit.MILLISECONDS);
				U val = future.get();
				result.complete(val);
			} catch (InterruptedException | CancellationException e) {
				final TimeoutException ex = new TimeoutException("Timeout after " + duration);
				result.completeExceptionally(ex);
			} catch (Exception ex) {
				result.completeExceptionally(ex);
			}
			return null;
		}, waitExecutor);

		return result;
	}
}
