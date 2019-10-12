package com.wiki.framework.mybatis.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Version;
import java.util.Date;

@Data
public abstract class CommonPO {

	/**
	 * 主键 为了兼容 老数据,将主键长度该长
	 */
	@Column(columnDefinition = "varchar(50) not null comment '主键'", nullable = false, updatable = false)
	@Id
	protected String id;
	/**
	 * 数据版本号
	 */
	@Column(nullable = false, updatable = false, columnDefinition = "bigint not null default 0 comment '版本号'")
	@Version
	protected Long version;
	/**
	 * 创建者
	 */
	@Column(updatable = false, columnDefinition = "varchar(50) comment '创建者Id'")
	protected String createBy;
	/**
	 * 创建时间
	 */
	@Column(updatable = false, columnDefinition = "datetime(6) comment '创建时间'")
	protected Date createTime;
	/**
	 * 最后修改者
	 */
	@Column(columnDefinition = "varchar(50) comment '最后修改人Id'")
	protected String updateBy;
	/**
	 * 更新时间
	 */
	@Column(columnDefinition = "datetime(6) comment '最后修改时间'")
	protected Date updateTime;
	/**
	 * isDeleted 标记
	 */
	@Column(nullable = false, columnDefinition = "tinyint not null default 0 comment '删除标记 0 未删除 1 删除'")
	protected Integer isDeleted;
}
