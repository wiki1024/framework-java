package com.wiki.framework.system.context.mybatis.po;

import com.wiki.framework.mybatis.po.CommonPO;

import javax.persistence.Column;
import java.io.Serializable;

public abstract class BasePO extends CommonPO implements TenantPO {
	/**
	 * 租户Id
	 */
	@Column(columnDefinition = "varchar(50) not null comment '租户Id'")
	protected String tenantId;

	public BasePO() {
	}

	@Override
	public String toString() {
		return "BasePO{" +
				"tenantId='" + tenantId + '\'' +
				", id='" + id + '\'' +
				", createBy='" + createBy + '\'' +
				", createTime=" + createTime +
				", updateBy='" + updateBy + '\'' +
				", updateTime=" + updateTime +
				", isDeleted=" + isDeleted +
				'}';
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}