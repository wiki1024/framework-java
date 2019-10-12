package com.wiki.framework.common.util;

import java.util.Comparator;
import java.util.concurrent.RecursiveAction;

/**
 * @author Devin
 * @version 1.0
 * @since 2017/8/28 11:10
 */
public class ForkJoinSortTask extends RecursiveAction {
	public static final Comparator<String> naturalComparator = new NaturalOrderComparator<>(false);
	private static final long serialVersionUID = -1738015707066879398L;
	public final String[] words;
	final int lo;
	final int hi;

	public ForkJoinSortTask(String[] array) {
		this.words = array;
		this.lo = 0;
		this.hi = array.length - 1;
	}

	public ForkJoinSortTask(String[] array, int lo, int hi) {
		this.words = array;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	protected void compute() {
		if (hi - lo > 0) {
			int pivot = partition(words, lo, hi);
			ForkJoinSortTask left = new ForkJoinSortTask(words, lo, pivot - 1);
			ForkJoinSortTask right = new ForkJoinSortTask(words, pivot + 1, hi);
			invokeAll(left, right);
		}
	}

	/**
	 * 对数组进行分区操作，并返回中间元素位置
	 */
	private int partition(String[] array, int lo, int hi) {
		String x = array[hi];
		int i = lo - 1;
		for (int j = lo; j < hi; j++) {
			if (naturalComparator.compare(array[j], x) <= 0) {
				i++;
				swap(array, i, j);
			}
		}
		swap(array, i + 1, hi);
		return i + 1;
	}

	private void swap(String[] array, int i, int j) {
		if (i != j) {
			String temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}
	}

}
