package com.wiki.framework.common.util;

import com.sun.jmx.mbeanserver.JmxMBeanServer;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 */
public class PortUtils {
	//默认HTTP连接器协议
	public static String PROTOCOL_TYPE = "HTTP/1.1";

	//默认HTTP连接器协议
	public static String SCHEME_TYPE = "http";

	public static String SCHEME = "scheme";

	public static String PROTOCOL = "protocol";

	public static String PORT = "port";
	//JBOSS容器
	public static String JBOSS = "jboss";

	//容器连接器
	public static String CATALINA_CONNECTOR = "Catalina:type=Connector,*";
	public static String JBOSS_CONNECTOR = "jboss.web:type=Connector,*";

	/**
	 * 根据协议和scheme获取Http服务端口号
	 *
	 * @return
	 */
	public static Integer getHttpPortByMBean() {
		try {
			MBeanServer mBeanServer = null;
			ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
			int serverType = 1;
			if (mBeanServers.size() > 0) {
				for (MBeanServer _mBeanServer : mBeanServers) {
					if (_mBeanServer instanceof JmxMBeanServer) {
						mBeanServer = _mBeanServer;
						break;
					} else if (JBOSS.equalsIgnoreCase(_mBeanServer.getDefaultDomain())) {
						mBeanServer = _mBeanServer;
						serverType = 2;
						break;
					} else {
						serverType = -1;
					}
				}
			}
			Set<ObjectName> objectNames = null;
			if (serverType == 1) {
				objectNames = mBeanServer.queryNames(new ObjectName(CATALINA_CONNECTOR), null);
			} else if (serverType == 2) {
				objectNames = mBeanServer.queryNames(new ObjectName(JBOSS_CONNECTOR), null);
			}
			if (objectNames != null) {
				for (ObjectName objectName : objectNames) {
					Object _protocol = mBeanServer.getAttribute(objectName, PROTOCOL);
					Object _scheme = mBeanServer.getAttribute(objectName, SCHEME);
					if (PROTOCOL_TYPE.equals(_protocol) && SCHEME_TYPE.equals(_scheme)) {
						return Integer.parseInt(mBeanServer.getAttribute(objectName, PORT).toString());
					}
				}
			}
		} catch (Exception ignored) {
			String jettyPort = System.getProperty("jetty.port");
			if (jettyPort != null) {
				return Integer.parseInt(jettyPort);
			}
		}
		return null;
	}
}
