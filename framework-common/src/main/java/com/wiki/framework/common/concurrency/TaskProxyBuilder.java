package com.wiki.framework.common.concurrency;

import com.wiki.framework.common.context.ThreadStore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public interface TaskProxyBuilder {

	Runnable runnable(Runnable runnable);

	<T> Callable<T> callable(Callable<T> callable);

	class DefaultTaskProxyBuilder implements TaskProxyBuilder{

		@Override
		public Runnable runnable(Runnable runnable) {
			return new RunProxy(ThreadStore.getContextMap(),ThreadStore.getIdMap(),runnable);
		}

		@Override
		public <T> Callable<T> callable(Callable<T> callable) {
			return new CallProxy<>(ThreadStore.getContextMap(),ThreadStore.getIdMap(),callable);
		}
	}

	class RunProxy implements Runnable {
		private Map<String, String> contextMap= new HashMap<>();
		private Map<String, String> idMap = new HashMap<>();
		private Runnable runner;

		public RunProxy(Map<String, String> contextMap, Map<String, String> idMap, Runnable runner) {
			if (contextMap != null) {
				this.contextMap.putAll(contextMap);
			}
			if (idMap != null) {
				this.idMap.putAll(idMap);
			}
			this.runner = runner;
		}

		@Override
		public void run() {
			try {
				if (contextMap != null) {
					ThreadStore.setContextMap(contextMap);
				}
				if (idMap != null) {
					ThreadStore.setIdMap(idMap);
				}
				runner.run();
			} finally {
				ThreadStore.clean();
			}
		}
	}

	class CallProxy<T> implements Callable<T> {
		private Map<String, String> contextMap = new HashMap<>();
		private Map<String, String> idMap = new HashMap<>();
		private Callable<T> caller;

		public CallProxy(Map<String, String> contextMap, Map<String, String> idMap, Callable<T> caller) {
			if (contextMap != null) {
				this.contextMap.putAll(contextMap);
			}
			if (idMap != null) {
				this.idMap.putAll(idMap);
			}
			this.caller = caller;
		}

		@Override
		public T call() throws Exception {
			try {
				if (contextMap != null) {
					ThreadStore.setContextMap(contextMap);
				}
				if (idMap != null) {
					ThreadStore.setIdMap(idMap);
				}
				return caller.call();
			} finally {
				ThreadStore.clean();
			}
		}
	}
}
