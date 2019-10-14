package com.wiki.framework.mybatis.mybatis.listener;


import com.wiki.framework.mybatis.mybatis.listener.spi.*;
import org.apache.commons.lang3.NotImplementedException;

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
	private static List<POUpdateListener> preInsertListeners = new ArrayList<>();
	private static List<POUpdateListener> prePOUpdateListeners = new ArrayList<>();

	private static List<CriteriaUpdateListener> queryListeners = new ArrayList<>();
	private static List<CriteriaUpdateListener> deleteListeners = new ArrayList<>();


	@SuppressWarnings("Duplicates")
	public static void registListener(Object listener) {
		if (listener instanceof POUpdateListener) {
			POUpdateListener l = (POUpdateListener) listener;
			if (l.type() == 1) {
				//update
				update(l, prePOUpdateListeners);
			} else if (l.type() == 2) {
				//insert
				update(l, preInsertListeners);
			} else {
				throw new RuntimeException("POUpdateListener type invalid: " + l.type());
			}
		} else if (listener instanceof CriteriaUpdateListener) {
			CriteriaUpdateListener l = (CriteriaUpdateListener) listener;
			if (l.type() == 1) {
				//query
				update(l, queryListeners);
			} else if (l.type() == 2) {
				//insert
				update(l, deleteListeners);
			} else {
				throw new RuntimeException("POUpdateListener type invalid: " + l.type());
			}
		} else {
			throw new RuntimeException("listener type invalid: " + listener.getClass());
		}
	}

	private static void update(POUpdateListener l, List<POUpdateListener> listenerList) {
		if (l.direction() > 0) {
			listenerList.add(l);
			listenerList.sort(Comparator.comparing(OrderedListener::getOrder, comparator));
		} else if (l.direction() < 0) {
			throw new NotImplementedException("post update not");
		} else {
			throw new NotImplementedException("all update not");
		}
	}

	private static void update(CriteriaUpdateListener l, List<CriteriaUpdateListener> listenerList) {

		listenerList.add(l);
		listenerList.sort(Comparator.comparing(OrderedListener::getOrder, comparator));

	}

	public static List<POUpdateListener> getPreInsertListeners() {
		return preInsertListeners;
	}

	public static List<POUpdateListener> getPrePOUpdateListeners() {
		return prePOUpdateListeners;
	}

	public static List<CriteriaUpdateListener> getQueryListeners() {
		return queryListeners;
	}

	public static List<CriteriaUpdateListener> getDeleteListeners() {
		return deleteListeners;
	}
}
