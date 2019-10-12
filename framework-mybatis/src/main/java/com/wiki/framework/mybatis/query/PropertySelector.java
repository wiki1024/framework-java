package com.wiki.framework.mybatis.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象的属性选择器，当includes 和 excludes 同时设置时，以excludes为准;当includes和excludes都不设置时，将返回全部属性
 *
 * @author thomason
 * @version 1.0
 * @since 2018/2/5 下午5:14
 */
public class PropertySelector implements Serializable {
	/**
	 * 包含哪些属性
	 */
	private List<String> includes;
	/**
	 * 不包含哪些属性
	 */
	private List<String> excludes;

	public PropertySelector addInclude(String include) {
		if (excludes != null && !excludes.isEmpty()) {
			throw new RuntimeException("you can't both assign excludes and includes");
		}
		if (this.includes == null) {
			this.includes = new ArrayList<>();
		}
		this.includes.add(include);
		return this;
	}

	public PropertySelector include(String include) {
		if (excludes != null && !excludes.isEmpty()) {
			throw new RuntimeException("you can't both assign excludes and includes");
		}
		if (this.includes == null) {
			this.includes = new ArrayList<>();
		}
		this.includes.add(include);
		return this;
	}

	public PropertySelector addExclude(String exclude) {
		if (includes != null && !includes.isEmpty()) {
			throw new RuntimeException("you can't both assign includes and excludes");
		}
		if (this.excludes == null) {
			this.excludes = new ArrayList<>();
		}
		this.excludes.add(exclude);
		return this;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}
}
