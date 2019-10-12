package com.wiki.framework.common.util;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Thomason
 * @version 1.0
 * @since 2015/4/28 15:47
 */

public class RandomUtils {
	private static Random random = null;

	public static int getRandom(int max) {
		return org.apache.commons.lang3.RandomUtils.nextInt(0, max);
	}

	public static String getRandomAlphabet() {
		int random = getRandom(26);
		switch (random) {
			case 0:
				return "a";
			case 1:
				return "b";
			case 2:
				return "c";
			case 3:
				return "d";
			case 4:
				return "e";
			case 5:
				return "f";
			case 6:
				return "g";
			case 7:
				return "h";
			case 8:
				return "i";
			case 9:
				return "i";
			case 10:
				return "k";
			case 11:
				return "l";
			case 12:
				return "m";
			case 13:
				return "n";
			case 14:
				return "o";
			case 15:
				return "p";
			case 16:
				return "q";
			case 17:
				return "r";
			case 18:
				return "s";
			case 19:
				return "t";
			case 20:
				return "u";
			case 21:
				return "v";
			case 22:
				return "w";
			case 23:
				return "x";
			case 24:
				return "y";
			case 25:
				return "z";
			default:
				return null;
		}
	}

	public static int[] getRandom(int max, int number) {
		if (number > max) {
			throw new IllegalArgumentException();
		}
		if (number == max) {
			int[] result = new int[max];
			for (int i = 0; i < max; i++) {
				result[i] = i;
			}
			return result;
		}
		Set<Integer> containsSet = new HashSet<Integer>();
		while (containsSet.size() < number) {
			int i = org.apache.commons.lang3.RandomUtils.nextInt(0, max);
//			Random rdm = new Random(System.currentTimeMillis());
//			int i = Math.abs(rdm.nextInt()) % max + 1;
			if (!containsSet.contains(i)) {
				containsSet.add(i);
			}
		}
		int[] result = new int[containsSet.size()];
		int count = 0;
		for (Integer integer : containsSet) {
			result[count] = integer;
			count++;
		}
		return result;
	}


	private static Random getRandomInstance() {
		if (random == null) {
			random = new Random(System.currentTimeMillis());
		}
		return random;
	}

	public static String getChinese() {
		String str = null;
		int highPos, lowPos;
		Random random = getRandomInstance();
		highPos = (176 + Math.abs(random.nextInt(39)));
		lowPos = 161 + Math.abs(random.nextInt(93));
		byte[] b = new byte[2];
		b[0] = (new Integer(highPos)).byteValue();
		b[1] = (new Integer(lowPos)).byteValue();
		try {
			str = new String(b, "GB2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String getFixedLengthChinese(int length) {
		String str = "";
		for (int i = length; i > 0; i--) {
			str = str + getChinese();
		}
		return str;
	}

	public static String getRandomLengthChinese(int start, int end) {
		String str = "";
		int length = new Random().nextInt(end + 1);
		if (length < start) {
			str = getRandomLengthChinese(start, end);
		} else {
			for (int i = 0; i < length; i++) {
				str = str + getChinese();
			}
		}
		return str;
	}
}
