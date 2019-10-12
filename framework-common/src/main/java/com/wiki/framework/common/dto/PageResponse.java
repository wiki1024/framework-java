package com.wiki.framework.common.dto;

import java.util.List;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/6/7 上午12:06
 */
public class PageResponse<T> {
	/**
	 * 结果记录
	 */
	private List<T> rows;
	/**
	 * 总记录数
	 */
	private long total;

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}
}
