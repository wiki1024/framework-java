package com.wiki.framework.mybatis.query;



import com.wiki.framework.mybatis.query.v2.Criteria;

import java.io.Serializable;
import java.util.List;

/**
 * 与具体ORM实现无关的分页参数及查询结果封装. 注意所有序号从1开始.
 * 注:
 * 不需要json序列化的属性用@JsonIgnore标记
 *
 * @author Thomason
 */
public class Pagination<T> implements Serializable {
	/**
	 * 排序字符串格式：列:asc(或者desc),列:asc(或者desc)
	 */
	private String sort;
	/**
	 * 是否自动计算总行数
	 */
	private boolean counting = true;
	/**
	 * 是否自动分页
	 */
	private boolean paging = true;
	/**
	 * 每页记录数
	 */
	private int pageSize = 10;
	/**
	 * 页码号
	 */
	private int pageNo = 1;
	/**
	 * 起始记录数 默认-1
	 */
	private int start = 0;
	/**
	 * 最大记录数 默认-1
	 */
	private long count = 0;
	/**
	 * 结果记录
	 */
	private List<T> rows;
	/**
	 * 查询条件
	 */
	private Criteria criteria;

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public boolean isCounting() {
		return counting;
	}

	public void setCounting(boolean counting) {
		this.counting = counting;
	}

	public boolean isPaging() {
		return paging;
	}

	public void setPaging(boolean paging) {
		this.paging = paging;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
}
