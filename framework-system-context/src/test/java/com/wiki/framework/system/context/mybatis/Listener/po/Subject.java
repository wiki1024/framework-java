package com.wiki.framework.system.context.mybatis.Listener.po;

import com.wiki.framework.system.context.mybatis.po.BasePO;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "t_tenant_subject")
@Data
@ToString
public class Subject extends BasePO {

	@Column(columnDefinition = "varchar(1024) not null comment ''")
	private String name;

	@Column(columnDefinition = "varchar(1024) not null comment ''")
	private int age;
}
