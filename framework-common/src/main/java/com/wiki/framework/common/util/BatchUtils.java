package com.wiki.framework.common.util;

import java.util.ArrayList;
import java.util.List;

public final class BatchUtils {

	private BatchUtils() {
	}

	public static int[] makeSlice(int size, int batchSize) {
		if (size <= batchSize) {
			return new int[]{0, size};
		}
		int numOfBatch = (int) Math.ceil(size * 1.0 / batchSize);
		int[] result = new int[numOfBatch + 1];
		for (int i = 1; i < result.length; i++) {
			if (size <= i * batchSize) {
				result[i] = size;
				break;
			} else {
				result[i] = i * batchSize;
			}
		}
		return result;
	}

	public static List<List> sliceBatch(List poList, int batchSize) {
		int size = poList.size();
		int numOfBatch = size / batchSize + 1;
		List<List> batches = new ArrayList<>(numOfBatch);
		int currentEndIndex = 0;
		for (int i = 0; i < numOfBatch; i++) {
			currentEndIndex = currentEndIndex + batchSize;
			if (size <= currentEndIndex) {
				batches.add(poList.subList(i * batchSize, size));
				break;
			} else {
				batches.add(poList.subList(i * batchSize, currentEndIndex));
			}
		}
		return batches;
	}
}
