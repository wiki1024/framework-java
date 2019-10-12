package com.wiki.framework.mybatis.dbinspector.autoconfig;

import com.wiki.framework.mybatis.dbinspector.processor.DdlProcessor;
import com.wiki.framework.mybatis.dbinspector.processor.impl.ConsoleDdlProcessor;
import com.wiki.framework.mybatis.dbinspector.processor.impl.JdbcDdlProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/8/7 下午8:53
 */
@Configuration
public class DdlProcessorConfigure {
	@Bean
	public DdlProcessor jdbcDdlProcessor() {
		return new JdbcDdlProcessor();
	}

	@Bean
	public DdlProcessor consoleDdlProcessor() {
		return new ConsoleDdlProcessor();
	}
}
