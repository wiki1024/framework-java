package com.wiki.framework.mybatis.mybatis;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MybatisUtils {

	private static Logger logger = LoggerFactory.getLogger(MybatisUtils.class);
	public static MappedStatement copyFromMappedStatement(MappedStatement ms, String suffix, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId() + suffix, newSqlSource, ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuilder keyProperties = new StringBuilder();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}

		//setStatementTimeout()
		builder.timeout(ms.getTimeout());

		//setStatementResultMap()
		builder.parameterMap(ms.getParameterMap());

		//setStatementResultMap()
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());

		//setStatementCache()
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	public static MappedStatement copyFromNewSql(MappedStatement ms, BoundSql boundSql,
	                                             String sql) {
		BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
		SqlSource sqlSource = new BoundSqlSqlSource(newBoundSql);
//				new StaticSqlSource(ms.getConfiguration(), sql, boundSql.getParameterMappings());
		return copyFromMappedStatement(ms, "", sqlSource);
	}

	public static Statement parseSql(String origSql) {
		try {
			return CCJSqlParserUtil.parse(origSql);
		} catch (JSQLParserException e) {
			logger.error("unable to parse sql:" + origSql + " reason is:\n" + ExceptionUtils.getStackTrace(e));
			throw new RuntimeException(e);
		}
	}

	public static BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql,
	                                        String sql, List<ParameterMapping> parameterMappings, Object parameter) {
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMappings, parameter);
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
			}
		}
		return newBoundSql;
	}

	public static class BoundSqlSqlSource implements SqlSource {
		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}
}
