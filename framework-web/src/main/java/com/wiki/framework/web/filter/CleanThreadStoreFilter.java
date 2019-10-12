package com.wiki.framework.web.filter;

import com.wiki.framework.common.context.ThreadStore;

import javax.servlet.*;
import java.io.IOException;

public class CleanThreadStoreFilter implements Filter {

	public static final int Order = Integer.MIN_VALUE;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			ThreadStore.clean();
		}
	}
}
