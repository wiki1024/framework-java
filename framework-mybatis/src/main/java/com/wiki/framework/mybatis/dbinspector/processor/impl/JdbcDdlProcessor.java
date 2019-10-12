package com.wiki.framework.mybatis.dbinspector.processor.impl;

import com.wiki.framework.mybatis.dbinspector.processor.DdlProcessor;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;


/**
 * @author thomason
 * @version 1.0
 * @since 2018/8/7 下午8:21
 */
public class JdbcDdlProcessor implements DdlProcessor {
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
		System.out.println("DB-INFO:" + connection.getMetaData().getURL());
		Statement statement = connection.createStatement();
		for (String ddl : ddlList) {
			statement.addBatch(ddl);
			System.out.println(ddl + ";");
		}
		statement.executeBatch();
		System.out.println("==========================================END OF DB-INSPECTOR==========================================");
	}
}
