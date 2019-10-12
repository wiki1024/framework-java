package com.wiki.framework.mybatis.po;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "t_subject")
@Data
@ToString
public class Subject extends CommonPO {

	@Column(columnDefinition = "varchar(1024) not null comment ''")
	private String name;

	@Column(columnDefinition = "varchar(1024) not null comment ''")
	private int age;
}
