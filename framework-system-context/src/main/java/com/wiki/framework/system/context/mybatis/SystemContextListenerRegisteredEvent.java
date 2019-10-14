package com.wiki.framework.system.context.mybatis;

import org.springframework.context.ApplicationEvent;

public class SystemContextListenerRegisteredEvent extends ApplicationEvent {

	public SystemContextListenerRegisteredEvent(Object source) {
		super(source);
	}
}
