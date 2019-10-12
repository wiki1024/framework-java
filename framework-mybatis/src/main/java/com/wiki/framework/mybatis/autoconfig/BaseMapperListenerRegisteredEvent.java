package com.wiki.framework.mybatis.autoconfig;

import org.springframework.context.ApplicationEvent;

public class BaseMapperListenerRegisteredEvent extends ApplicationEvent {

	public BaseMapperListenerRegisteredEvent(Object source) {
		super(source);
	}
}
