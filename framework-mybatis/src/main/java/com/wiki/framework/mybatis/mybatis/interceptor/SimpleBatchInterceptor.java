package com.wiki.framework.mybatis.mybatis.interceptor;


import com.wiki.framework.common.util.BatchUtils;
import com.wiki.framework.common.util.ReflectionUtils;
import com.wiki.framework.mybatis.database.Column;
import com.wiki.framework.mybatis.database.Table;
import com.wiki.framework.mybatis.mybatis.BatchOperationException;
import com.wiki.framework.mybatis.mybatis.BatchType;
import com.wiki.framework.mybatis.po.CommonPO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/***
 * @author wiki
 * todo add metrics
 * todo make type conversion as complete as jdbc
 * @version 1.0.0
 */
@SuppressWarnings("Duplicates")
@Intercepts(
		{
				@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
		}
)
public class SimpleBatchInterceptor extends BaseBatchInterceptor {

	public SimpleBatchInterceptor() {
	}

	public SimpleBatchInterceptor(int batchSize) {
		super(batchSize);
	}

	@Override
	protected int doBatchInsert(Executor executor, List poList, Table table) throws SQLException {
		return doOneTableBatchInsert(executor, poList, table, table.getSqlName());
	}

	private int doOneTableBatchInsert(Executor executor, List poList, Table table, String taleName) throws SQLException {
		StringBuilder builder = new StringBuilder(table.getColumns().size() * 20 + 3);
		String sql = buildInsertSql(taleName, table.getColumns(), builder);

		List<List> batches = BatchUtils.sliceBatch(poList, batchSize);
		int rows = 0;
		long start = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("batch insert log: {} start ", sql);
		}
		Connection conn = executor.getTransaction().getConnection();
		for (List batch : batches) {
			rows = rows + doOneBatchInsert(conn, batch, table.getColumns(), sql);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("batch insert done log, time :{}, rows {}, sql {}, ", System.currentTimeMillis() - start, rows, sql);
		}
		return rows;
	}

	@Override
	protected int doBatchUpdateSelective(Executor executor, List poList, Table table, Set<String> includeColumns) throws SQLException {
		sortPoList(poList);
		return doOneTableUpdateSelective(executor, poList, table, table.getSqlName(), includeColumns);
	}

	private void sortPoList(List poList) {
		if (poList != null) {
			poList.sort((o1, o2) -> {
				if (o1 instanceof CommonPO && o2 instanceof CommonPO) {
					return ((CommonPO) o1).getId().compareTo(((CommonPO) o2).getId());
				} else {
					return o1.hashCode() - o2.hashCode();
				}
			});
		}
	}

	@Override
	protected int doBatchUpdate(Executor executor, List poList, Table table, Set<String> columns) throws SQLException {
		sortPoList(poList);

		return doOneTableUpdate(executor, poList, table, table.getSqlName(), columns);

	}

	private int doOneTableUpdateSelective(Executor executor, List poList, Table table, String tableName, Set<String> includeColumns) throws SQLException {
		StringBuilder builder = new StringBuilder((table.getColumns().size() * 20 + 3) * batchSize * 3); //random guess, better than nothing
		List<List> batches = BatchUtils.sliceBatch(poList, batchSize);
		Connection conn = executor.getTransaction().getConnection();
		int rows = 0;
		for (List batch : batches) {
			try (Statement statement = conn.createStatement()) {
				for (Object o : batch) {
					CommonPO po = (CommonPO) o;
					if (po.getId() == null) {
						throw new BatchOperationException("id is null when update");
					}
					String sql = buildUpdateSelective(table.getColumns(), builder, tableName, po, table, includeColumns);
					if (logger.isDebugEnabled()) {
						logger.debug("simple batch update selective {} ", sql);
					}
					builder.setLength(0);
					statement.addBatch(sql);
				}
				int[] ints = statement.executeBatch();
				rows = rows + Arrays.stream(ints).sum();
			}
		}
		return rows;
	}


	private int doOneTableUpdate(Executor executor, List poList, Table table, String tableName, Set<String> includeColumns) throws SQLException {
		StringBuilder builder = new StringBuilder((table.getColumns().size() * 20 + 3) * batchSize * 3); //random guess, better than nothing
		List<List> batches = BatchUtils.sliceBatch(poList, batchSize);
		Connection conn = executor.getTransaction().getConnection();
		int rows = 0;
		for (List batch : batches) {
			try (Statement statement = conn.createStatement()) {
				for (Object o : batch) {
					CommonPO po = (CommonPO) o;
					if (po.getId() == null) {
						throw new BatchOperationException("id is null when update");
					}
					String sql = buildUpdate(table.getColumns(), builder, tableName, po, table, includeColumns);
					if (logger.isDebugEnabled()) {
						logger.debug("simple batch update selective {} ", sql);
					}
					builder.setLength(0);
					statement.addBatch(sql);
				}
				int[] ints = statement.executeBatch();
				rows = rows + Arrays.stream(ints).sum();
			}
		}
		return rows;
	}

	@Override
	protected boolean shouldApply(Invocation invocation, Triple<BatchType, List, Table> context) {
		BatchType batchType = context.getLeft();
		Table right = context.getRight();
		switch (batchType) {
			case BatchInsert:
				return true;
			case BatchUpdateSelective:
				return true;
			case BatchUpdate:
				return true;
			case None:
			default:
				return false;
		}
	}

	@Override
	public String getName() {
		return "SimpleBatchInterceptor";
	}

	@Override
	public void setProperties(Properties properties) {

	}

}
