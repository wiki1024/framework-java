package com.wiki.framework.system.context.mybatis.Listener;

import com.wiki.framework.mybatis.dbinspector.autoconfig.DbInspect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.wiki.framework.system.context.mybatis.Listener.mapper")
@DbInspect(basePackages = "com.wiki.framework.system.context.mybatis.Listener.po")
public class Application {
}
