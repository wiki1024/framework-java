package com.wiki.framework.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * @author Thomason
 * @version 1.0
 * @since 13-11-29 下午5:21
 */

@SuppressWarnings("Duplicates")
public class OSUtils {
	/**
	 * 获取操作系统名称
	 *
	 * @return
	 */
	public static String getOSName() {
		return System.getProperty("os.name").toLowerCase();
	}

	public static String getMacAddress() {
		String windowscmd = "cmd.exe /c echo %NUMBER_OF_PROCESSORS%";//windows的特殊
		String[] solariscmd = {"/bin/sh", "-c", "/usr/sbin/psrinfo | wc -l"};
		String[] aixcmd = {"/bin/sh", "-c", "/usr/sbin/lsdev -Cc processor | wc -l"};
		String[] hpuxcmd = {"/bin/sh", "-c", "echo \"map\" | /usr/sbin/cstm | grep CPU | wc -l "};
		String[] linuxcmd = {"/bin/sh", "-c", "cat /proc/cpuinfo | grep ^process | wc -l"};
		return null;
	}

	public static String runWindowsCmd(String cmds) throws IOException {
		Process p = Runtime.getRuntime().exec(cmds);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		String result = "";
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		bufferedReader.close();
		return result;
	}

	public static String runLinuxCmd(String[] cmds) throws IOException {
		try {
			Process p = Runtime.getRuntime().exec(cmds);
			p.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			String result = "";
			while ((line = bufferedReader.readLine()) != null) {
				result += line;
			}
			bufferedReader.close();
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String removeTitle(String result, String title) {
		StringBuilder builder = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(result, " ", false);
		while (tokenizer.hasMoreTokens()) {
			String s = tokenizer.nextToken();
			if (title.equals(s)) {
				continue;
			}
			builder.append(",");
			builder.append(s);
		}
		if (builder.length() > 0) {
			builder.deleteCharAt(0);
		}
		return builder.toString();
	}
}
