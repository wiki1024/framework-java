package com.wiki.framework.mybatis.query;


import com.wiki.framework.mybatis.query.v2.Criteria;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/6/7 上午12:06
 */
public class PageRequest {
	/**
	 * 是否自动计算总行数
	 */
	private boolean needCount = true;
	/**
	 * 是否自动分页
	 */
	private boolean needPaging = true;
	/**
	 * 每页记录数
	 */
	private int pageSize = 10;
	/**
	 * 页码号
	 */
	private int pageNo = 1;
	/**
	 * 查询条件
	 */
	private Criteria criteria;

	public boolean isNeedCount() {
		return needCount;
	}

	public void setNeedCount(boolean needCount) {
		this.needCount = needCount;
	}

	public boolean isNeedPaging() {
		return needPaging;
	}

	public void setNeedPaging(boolean needPaging) {
		this.needPaging = needPaging;
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

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
}
