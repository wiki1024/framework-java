package com.wiki.framework.system.context.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.system.context.SystemConstant;
import com.wiki.framework.system.context.SystemContext;
import com.wiki.framework.web.filter.CleanThreadStoreFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;


public class SystemContextFilter implements javax.servlet.Filter {

	private Logger logger = LoggerFactory.getLogger(SystemContextFilter.class);

	public static final int Order = CleanThreadStoreFilter.Order + 1;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		if (logger.isDebugEnabled()) {
			logger.debug("system context init");
		}
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String curHeader = headerNames.nextElement();
			if (StringUtils.startsWithIgnoreCase(curHeader, SystemConstant.CONTEXT_HEADER_PREFIX)) {
				String headerVal = request.getHeader(curHeader);
				String requestURI = request.getRequestURI();
				if (StringUtil.equalsIgnoreCase(curHeader, SystemConstant.HEADER_USER_ID)) {
					SystemContext.put(SystemConstant.HEADER_USER_ID, headerVal);
					if (logger.isDebugEnabled()) {
						logger.debug("request {} has  header: {}, with value {}", requestURI, SystemConstant.HEADER_USER_ID, headerVal);
					}
				} else if (StringUtil.equalsIgnoreCase(curHeader, SystemConstant.HEADER_ACCOUNT_ID)) {
					SystemContext.put(SystemConstant.HEADER_ACCOUNT_ID, headerVal);
					if (logger.isDebugEnabled()) {
						logger.debug("request {} has  header: {}, with value {}", requestURI, SystemConstant.HEADER_ACCOUNT_ID, headerVal);
					}
				} else if (StringUtil.equalsIgnoreCase(curHeader, SystemConstant.HEADER_ACCOUNT_NAME)) {
					headerVal = URLDecoder.decode(headerVal, "utf-8");
					SystemContext.put(SystemConstant.HEADER_ACCOUNT_NAME, headerVal);
					if (logger.isDebugEnabled()) {
						logger.debug("request {} has  header: {}, with value {}", requestURI, SystemConstant.HEADER_ACCOUNT_NAME, headerVal);
					}
				} else if (StringUtil.equalsIgnoreCase(curHeader, SystemConstant.HEADER_USER_NAME)) {
					headerVal = URLDecoder.decode(headerVal, "utf-8");
					SystemContext.put(SystemConstant.HEADER_USER_NAME, headerVal);
					if (logger.isDebugEnabled()) {
						logger.debug("request {} has  header: {}, with value {}", requestURI, SystemConstant.HEADER_USER_NAME, headerVal);
					}
				} else if (StringUtil.equalsIgnoreCase(curHeader, SystemConstant.HEADER_USER_IP)) {
					SystemContext.put(SystemConstant.HEADER_USER_IP, headerVal);
					if (logger.isDebugEnabled()) {
						logger.debug("request {} has  header: {}, with value {}", requestURI, SystemConstant.HEADER_USER_IP, headerVal);
					}
				} else if (StringUtil.equalsIgnoreCase(curHeader, SystemConstant.HEADER_USER_AGENT)) {
					SystemContext.put(SystemConstant.HEADER_USER_AGENT, headerVal);
					if (logger.isDebugEnabled()) {
						logger.debug("request {} has  header: {}, with value {}", requestURI, SystemConstant.HEADER_USER_AGENT, headerVal);
					}
				} else if (StringUtil.equalsIgnoreCase(curHeader, SystemConstant.HEADER_TENANT_ID)) {
					SystemContext.put(SystemConstant.HEADER_TENANT_ID, headerVal);
					if (logger.isDebugEnabled()) {
						logger.debug("request {} has  header: {}, with value {}", requestURI, SystemConstant.HEADER_TENANT_ID, headerVal);
					}
				} else if (StringUtil.equalsIgnoreCase(curHeader, SystemConstant.HEADER_LOCALE)) {
					SystemContext.put(SystemConstant.HEADER_LOCALE, headerVal);
					if (logger.isDebugEnabled()) {
						logger.debug("request {} has  header: {}, with value {}", requestURI, SystemConstant.HEADER_LOCALE, headerVal);
					}
				} else {
					SystemContext.put(curHeader, headerVal);
				}
			}
		}
	}
}
