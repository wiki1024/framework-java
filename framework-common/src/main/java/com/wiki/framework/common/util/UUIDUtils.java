package com.wiki.framework.common.util;

import java.net.InetAddress;

/**
 * @author Thomason
 * @version 1.0
 * @since 2016/9/28 14:01
 */

public class UUIDUtils {
	private static final String sep = "";
	private static final int IP;
	//    private static AtomicLong counter = new AtomicLong(0);
	private static final int JVM = (int) (System.currentTimeMillis() >>> 8);
	private static short counter = (short) 0;

	static {
		int ipadd;
		try {
			ipadd = toInt(InetAddress.getLocalHost().getAddress());
		} catch (Exception e) {
			ipadd = 0;
		}
		IP = ipadd;
	}

	/**
	 * 优化
	 *
	 * @return
	 */
	public static String getUUID() {
		String part1 = format(getIP());
		String part2 = format(getJVM());
		String part3 = format(getHiTime());
		String part4 = format(getLoTime());
		String part5 = format(getCount());
		char[] dist = new char[32];
		part1.getChars(0, 8, dist, 0);
		part2.getChars(0, 8, dist, 8);
		part3.getChars(0, 4, dist, 16);
		part4.getChars(0, 8, dist, 20);
		part5.getChars(0, 4, dist, 28);
		return new String(dist, 0, 32);
	}

	protected static String format(int intval) {
		String formatted = Integer.toHexString(intval);
//        StringBuilder buf = new StringBuilder("00000000");
//        buf.replace( 8-formatted.length(), 8, formatted );
		char[] init = "00000000".toCharArray();
//        if(formatted.length()>4) System.out.println(shortval);
		int length = formatted.length();
		formatted.getChars(0, length, init, 8 - length);
		//   buf.replace( 4-formatted.length(), 4, formatted );
		return new String(init, 0, 8);
//        return buf.toString();
	}

	protected static String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		// StringBuilder buf = new StringBuilder("0000");
		char[] init = "0000".toCharArray();
//        if(formatted.length()>4) System.out.println(shortval);
		int length = formatted.length();
		formatted.getChars(0, length, init, 4 - length);
		//   buf.replace( 4-formatted.length(), 4, formatted );
		return new String(init, 0, 4);
	}

	/**
	 * Unique across JVMs on this machine (unless they load this class
	 * in the same quater second - very unlikely)
	 */
	protected static int getJVM() {
		return JVM;
	}

	/**
	 * Unique in a millisecond for this JVM instance (unless there
	 * are > Short.MAX_VALUE instances created in a millisecond)
	 */
	protected static short getCount() {
		synchronized (UUIDUtils.class) {
			if (counter < 0) {
				counter = 0;
			}
			return counter++;
		}
//        return (short)counter.incrementAndGet();
	}

	/**
	 * Unique in a local network
	 */
	protected static int getIP() {
		return IP;
	}

	/**
	 * Unique down to millisecond
	 */
	protected static short getHiTime() {
		return (short) (System.currentTimeMillis() >>> 32);
	}

	protected static int getLoTime() {
		return (int) System.currentTimeMillis();
	}

	protected static int toInt(byte[] bytes) {
		int result = 0;
		for (int i = 0; i < 4; i++) {
			result = (result << 8) - Byte.MIN_VALUE + (int) bytes[i];
		}
		return result;
	}
}
