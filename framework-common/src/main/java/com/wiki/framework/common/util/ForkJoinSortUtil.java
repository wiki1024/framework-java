package com.wiki.framework.common.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinSortUtil {
	private final static int THREADS = 12;

	public static void sort(String[] sortWords) {
		ForkJoinSortTask wordsSortTask = new ForkJoinSortTask(sortWords);
		ForkJoinPool forkJoinPool = new ForkJoinPool(THREADS);
		forkJoinPool.invoke(wordsSortTask);
	}

	public static void sort(List<String> sortWords) {
		String[] stringArray = sortWords.toArray(new String[0]);
		sort(stringArray);
		sortWords.clear();
		sortWords.addAll(Arrays.asList(stringArray));
	}

/*    public static void main(String[] args) {
        String[] tests = {"18", "a15","()", "2", "--","b2", "a2", "b11", "ba","ade","caa","波音747","波音80","博客"};
        ForkJoinSortUtil.sort(tests);
        for (String result : tests) {
            System.out.print(result + " ");
        }
    }*/

}
