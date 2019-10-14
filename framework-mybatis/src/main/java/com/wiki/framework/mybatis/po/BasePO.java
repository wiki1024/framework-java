//package com.wiki.framework.mybatis.po;
//
//import javax.persistence.Column;
//import java.io.Serializable;
//
///**
// * 所有的实体类的父类，集成了一些公用属性
// *
// * @author Thomason
// */
//@SuppressWarnings("serial")
//public abstract class BasePO extends CommonPO implements Serializable, Cloneable {
//	/**
//	 * 租户Id
//	 */
//	@Column(columnDefinition = "varchar(50) not null comment '租户Id'")
//	protected String tenantId;
//
//	public BasePO() {
//	}
//
//	@Override
//	public String toString() {
//		return "BasePO{" +
//				"tenantId='" + tenantId + '\'' +
//				", id='" + id + '\'' +
//				", createBy='" + createBy + '\'' +
//				", createTime=" + createTime +
//				", updateBy='" + updateBy + '\'' +
//				", updateTime=" + updateTime +
//				", isDeleted=" + isDeleted +
//				'}';
//	}
//
//	public String getTenantId() {
//		return tenantId;
//	}
//
//	public void setTenantId(String tenantId) {
//		this.tenantId = tenantId;
//	}
//}
