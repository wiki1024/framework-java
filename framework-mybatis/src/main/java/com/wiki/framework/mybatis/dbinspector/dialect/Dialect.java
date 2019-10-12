package com.wiki.framework.mybatis.dbinspector.dialect;


import com.wiki.framework.mybatis.database.Column;
import com.wiki.framework.mybatis.database.Index;
import com.wiki.framework.mybatis.database.Table;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/8/13 上午10:20
 */
public interface Dialect {
	String BLANK = " ";

	String buildCreateTableClause(Table table);

	String buildAddColumnClause(Column column);

	String buildUpdateColumnClause(Column column);

	String buildIndexClause(Index index);
}
