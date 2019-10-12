package com.wiki.framework.mybatis.dbinspector;

import org.springframework.context.ApplicationEvent;

public class DbInspectorDoneInspectEvent extends ApplicationEvent {
	public DbInspectorDoneInspectEvent(Object source) {
		super(source);
	}
}
