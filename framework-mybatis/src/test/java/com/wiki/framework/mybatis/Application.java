package com.wiki.framework.mybatis;

import com.wiki.framework.mybatis.dbinspector.autoconfig.DbInspect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.wiki.framework.mybatis.mapper")
@DbInspect(basePackages = "com.wiki.framework.mybatis.po")
public class Application {
}
