package com.wiki.framework.mybatis.mybatis.listener;


import com.wiki.framework.mybatis.mybatis.listener.spi.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/1/2 上午10:42
 */
public class MybatisListenerContainer {
    private static final Comparator<Integer> comparator = Comparator.nullsLast(Comparator.naturalOrder());
	private static List<PreInsertListener> preInsertListeners;
	private static List<PreUpdateListener> preUpdateListeners;
	private static List<PreDeleteListener> preDeleteListeners;
	private static List<PostInsertListener> postInsertListeners;
	private static List<PostUpdateListener> postUpdateListeners;
	private static List<PostDeleteListener> postDeleteListeners;

	public static void registListener(Object object) {
		if (object instanceof PreInsertListener) {
			if (preInsertListeners == null) {
				preInsertListeners = new ArrayList<>();
			}
			preInsertListeners.add((PreInsertListener) object);
            preInsertListeners.sort(Comparator.comparing(PreInsertListener::getOrder,comparator));
        }

		if (object instanceof PreUpdateListener) {
			if (preUpdateListeners == null) {
				preUpdateListeners = new ArrayList<>();
			}
			preUpdateListeners.add((PreUpdateListener) object);
            preUpdateListeners.sort(Comparator.comparing(PreUpdateListener::getOrder,comparator));
        }

		if (object instanceof PreDeleteListener) {
			if (preDeleteListeners == null) {
				preDeleteListeners = new ArrayList<>();
			}
			preDeleteListeners.add((PreDeleteListener) object);
            preDeleteListeners.sort(Comparator.comparing(PreDeleteListener::getOrder,comparator));
        }

		if (object instanceof PostInsertListener) {
			if (postInsertListeners == null) {
				postInsertListeners = new ArrayList<>();
			}
			postInsertListeners.add((PostInsertListener) object);
            postInsertListeners.sort(Comparator.comparing(PostInsertListener::getOrder,comparator));
        }

		if (object instanceof PostUpdateListener) {
			if (postUpdateListeners == null) {
				postUpdateListeners = new ArrayList<>();
			}
			postUpdateListeners.add((PostUpdateListener) object);
            postUpdateListeners.sort(Comparator.comparing(PostUpdateListener::getOrder,comparator));
        }

		if (object instanceof PostDeleteListener) {
			if (postDeleteListeners == null) {
				postDeleteListeners = new ArrayList<>();
			}
			postDeleteListeners.add((PostDeleteListener) object);
            postDeleteListeners.sort(Comparator.comparing(PostDeleteListener::getOrder,comparator));
        }
	}

	public static List<PreInsertListener> getPreInsertListeners() {
		return preInsertListeners;
	}

	public static List<PreUpdateListener> getPreUpdateListeners() {
        return preUpdateListeners;
	}

	public static List<PreDeleteListener> getPreDeleteListeners() {
        return preDeleteListeners;
	}

	public static List<PostInsertListener> getPostInsertListeners() {
        return postInsertListeners;
	}

	public static List<PostUpdateListener> getPostUpdateListeners() {
        return postUpdateListeners;
	}

	public static List<PostDeleteListener> getPostDeleteListeners() {
        return postDeleteListeners;
	}
}
