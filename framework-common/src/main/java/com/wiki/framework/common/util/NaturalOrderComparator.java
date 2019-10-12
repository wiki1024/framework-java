package com.wiki.framework.common.util;

import java.util.Comparator;

public final class NaturalOrderComparator<T> implements Comparator<T> {

	private final boolean caseInsensitive;

	public NaturalOrderComparator(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

	int compareRight(String a, String b) {
		int bias = 0;
		int ia = 0;
		int ib = 0;

		for (; ; ia++, ib++) {
			char ca = charAt(a, ia);
			char cb = charAt(b, ib);

			if (!Character.isDigit(ca) && !Character.isDigit(cb)) {
				return bias;
			} else if (!Character.isDigit(ca)) {
				return -1;
			} else if (!Character.isDigit(cb)) {
				return +1;
			} else if (ca < cb) {
				if (bias == 0) {
					bias = -1;
				}
			} else if (ca > cb) {
				if (bias == 0) {
					bias = +1;
				}
			} else if (ca == 0 && cb == 0) {
				return bias;
			}
		}
	}

	@Override
	public int compare(T o1, T o2) {
		String a = o1.toString();
		String b = o2.toString();

		int ia = 0, ib = 0;
		int nza = 0, nzb = 0;
		char ca, cb;
		int result;

		while (true) {
			nza = nzb = 0;

			ca = charAt(a, ia);
			cb = charAt(b, ib);

			while (ca == '0') {
				if (ca == '0') {
					nza++;
				} else {
					nza = 0;
				}

				if (!Character.isDigit(charAt(a, ia + 1))) {
					break;
				}

				ca = charAt(a, ++ia);
			}

			while (cb == '0') {
				if (cb == '0') {
					nzb++;
				} else {
					nzb = 0;
				}

				if (!Character.isDigit(charAt(b, ib + 1))) {
					break;
				}

				cb = charAt(b, ++ib);
			}

			if (Character.isDigit(ca) && Character.isDigit(cb)) {
				if ((result = compareRight(a.substring(ia), b.substring(ib))) != 0) {
					return result;
				}
			}

			if (ca == 0 && cb == 0) {
				return nza - nzb;
			}

			if (ca < cb) {
				return -1;
			} else if (ca > cb) {
				return +1;
			}

			++ia;
			++ib;
		}
	}

	private char charAt(String s, int i) {
		if (i >= s.length()) {
			return 0;
		} else {
			return caseInsensitive ? Character.toUpperCase(s.charAt(i)) : s.charAt(i);
		}
	}
}
