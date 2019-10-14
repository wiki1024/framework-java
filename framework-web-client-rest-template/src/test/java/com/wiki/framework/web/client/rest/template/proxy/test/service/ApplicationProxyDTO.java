package com.wiki.framework.web.client.rest.template.proxy.test.service;

import lombok.ToString;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/8/24 上午9:54
 */
@ToString
public class ApplicationProxyDTO {
	/**
	 * 应用Id
	 */
	private String appId;
	/**
	 * 应用名称
	 */
	private String appName;
	/**
	 * 应用图标
	 */
	private String icon;
	/**
	 * 当前版本号
	 */
	private String versionName;
	/**
	 * 软件商品名称
	 */
	private String productName;
	/**
	 * 软件型号
	 */
	private String modelName;
	/**
	 * 微服务名称
	 */
	private String serviceName;
	/**
	 * 应用分类
	 */
	private String category;
	/**
	 * 首页地址
	 */
	private String indexUrl;
	/**
	 * 数据初始化地址
	 */
	private String initialUrl;
	/**
	 * 项目数据初始化地址
	 */
	private String projectInitialUrl;
	/**
	 * 是否基础应用
	 */
	private Integer isBasic;
	/**
	 * 应用是否有初始化数据
	 */
	private Integer needInit;
	/**
	 * 应用描述
	 */
	private String description;
	/**
	 * 是否显示
	 */
	private String isDisplay;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIndexUrl() {
		return indexUrl;
	}

	public void setIndexUrl(String indexUrl) {
		this.indexUrl = indexUrl;
	}

	public String getInitialUrl() {
		return initialUrl;
	}

	public void setInitialUrl(String initialUrl) {
		this.initialUrl = initialUrl;
	}

	public Integer getIsBasic() {
		return isBasic;
	}

	public void setIsBasic(Integer isBasic) {
		this.isBasic = isBasic;
	}

	public Integer getNeedInit() {
		return needInit;
	}

	public void setNeedInit(Integer needInit) {
		this.needInit = needInit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProjectInitialUrl() {
		return projectInitialUrl;
	}

	public void setProjectInitialUrl(String projectInitialUrl) {
		this.projectInitialUrl = projectInitialUrl;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(String isDisplay) {
		this.isDisplay = isDisplay;
	}
}
