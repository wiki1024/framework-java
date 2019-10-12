package com.wiki.framework.mybatis.dbinspector.processor.impl;

import com.wiki.framework.mybatis.dbinspector.processor.DdlProcessor;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/8/7 下午8:27
 */
public class ConsoleDdlProcessor implements DdlProcessor {
	@Override
	public void execute(Connection connection, List<String> ddlList) throws Exception {
		System.out.println(" _______  .______           __  .__   __.      _______..______    _______   ______ .___________.  ______   .______      \n" +
				"|       \\ |   _  \\         |  | |  \\ |  |     /       ||   _  \\  |   ____| /      ||           | /  __  \\  |   _  \\     \n" +
				"|  .--.  ||  |_)  |  ______|  | |   \\|  |    |   (----`|  |_)  | |  |__   |  ,----'`---|  |----`|  |  |  | |  |_)  |    \n" +
				"|  |  |  ||   _  <  |______|  | |  . `  |     \\   \\    |   ___/  |   __|  |  |         |  |     |  |  |  | |      /     \n" +
				"|  '--'  ||  |_)  |        |  | |  |\\   | .----)   |   |  |      |  |____ |  `----.    |  |     |  `--'  | |  |\\  \\----.\n" +
				"|_______/ |______/         |__| |__| \\__| |_______/    | _|      |_______| \\______|    |__|      \\______/  | _| `._____|\n" +
//				"                                                                                                                        \n");
				"");
		System.out.println("DB-INFO:" + connection.getMetaData().getUserName());
		for (String ddl : ddlList) {
			if (StringUtils.isBlank(ddl)) {
				continue;
			}
			System.out.println(ddl + ";");
		}
		System.out.println("==========================================END OF DB-INSPECTOR==========================================");
	}
}
