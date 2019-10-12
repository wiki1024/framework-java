package com.wiki.framework.mybatis.dbinspector;

import com.wiki.framework.mybatis.ORMapping;
import com.wiki.framework.mybatis.dbinspector.dialect.Dialect;
import com.wiki.framework.mybatis.dbinspector.dialect.DialectFactory;
import com.wiki.framework.mybatis.dbinspector.processor.DdlProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/8/12 下午1:00
 */
public class DbInspector {
	private static final String RESOURCE_PATTERN = "/**/*.class";
	private static final String PACKAGE_INFO_SUFFIX = ".package-info";

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	private final Dialect dialect;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private DdlProcessor ddlProcessor;
	@Autowired
	private DataSource dataSource;

	@Autowired(required = false)
	private List<DbInspectListener> listeners;
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	private boolean enabled = false;
	private boolean async = false;
	private List<String> packagesToScan;

	public DbInspector() {
		this.dialect = DialectFactory.getDialect("mysql");
	}

	@PostConstruct
	public void inspect() {
		try {
			if (!enabled) {
				return;
			}
			if (CollectionUtils.isEmpty(packagesToScan)) {
				return;
			}
			List<String> packages = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(packagesToScan)) {
				packages.addAll(packagesToScan);
			}
			doInspect(packages);
		} finally {
			applicationEventPublisher.publishEvent(new DbInspectorDoneInspectEvent(this));
		}
	}

	public void doInspect(List<String> packages) {
		List<com.wiki.framework.mybatis.database.Table> tables = scanJPATables(packages);
		//没有要同步的表，直接返回
		if (CollectionUtils.isEmpty(tables)) {
			return;
		}

		try {
			executeOnOneDataSource(tables, dataSource);
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	private List<com.wiki.framework.mybatis.database.Table> scanJPATables(List<String> packages) {
		if (CollectionUtils.isNotEmpty(listeners)) {
			for (DbInspectListener listener : listeners) {
				listener.beforeScan(packages);
			}
		}
		Set<String> classNames = scanPackages(packages);
		List<com.wiki.framework.mybatis.database.Table> jpaTables = new ArrayList<>();
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className);
				com.wiki.framework.mybatis.database.Table jpaTable = ORMapping.get(clazz);
				if (jpaTable == null) {
					continue;
				}
				jpaTables.add(jpaTable);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		if (CollectionUtils.isNotEmpty(listeners)) {
			for (DbInspectListener listener : listeners) {
				listener.afterScan(jpaTables);
			}
		}
		return jpaTables;
	}

	/**
	 * 对某个单库进行处理
	 *
	 * @param jpaTables  JPA对象
	 * @param dataSource 数据源
	 */
	private void executeOnOneDataSource(List<com.wiki.framework.mybatis.database.Table> jpaTables, DataSource dataSource) throws Exception {
		if (CollectionUtils.isNotEmpty(listeners)) {
			for (DbInspectListener listener : listeners) {
				listener.beforeInspectOnDatasource(dataSource, jpaTables);
			}
		}
		int size = jpaTables.size();
		CountDownLatch latch = new CountDownLatch(size);
		List<String> ddlList = new CopyOnWriteArrayList<>();
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		//按照数据库的表的个数，创建对应个数的线程池
		for (com.wiki.framework.mybatis.database.Table table : jpaTables) {
			executorService.execute(() -> {
				String javaName = table.getJavaName();
				try (Connection connection = dataSource.getConnection()) {
					connection.setAutoCommit(false);
					processOneTable(connection, table, ddlList);
				} catch (Exception e) {
					throw new RuntimeException("error process table:" + table.toString(), e);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		//销毁此线程池
		executorService.shutdown();

		//找到处理类
		if (CollectionUtils.isNotEmpty(ddlList) && ddlProcessor != null) {
			try (Connection connection = dataSource.getConnection()) {
				ddlProcessor.execute(connection, ddlList);
			}
		}
		if (CollectionUtils.isNotEmpty(listeners)) {
			for (DbInspectListener listener : listeners) {
				listener.afterInspectOnDatasource(dataSource, jpaTables);
			}
		}
	}

	private void processOneTable(Connection connection, com.wiki.framework.mybatis.database.Table table, List<String> ddlList) throws Exception {
		DatabaseResolver resolver = DatabaseResolver.getInstance();
		String tableName = table.getSqlName();
		Table dbTable = resolver.getTable(connection, tableName);
		List<com.wiki.framework.mybatis.database.Column> tableColumns = table.getColumns() == null ? Collections.EMPTY_LIST : table.getColumns();
		List<com.wiki.framework.mybatis.database.Index> indexList = table.getIndices() == null ? Collections.EMPTY_LIST : table.getIndices();
		if (dbTable == null) {
			String ddl = dialect.buildCreateTableClause(table);
			ddlList.add(ddl);
			if (CollectionUtils.isNotEmpty(indexList)) {
				indexList.forEach(index -> {
					String indexClause = dialect.buildIndexClause(index);
					ddlList.add(indexClause);
				});
			}
		} else {
			List<Column> dbColumns = dbTable.getColumns() == null ? Collections.EMPTY_LIST : dbTable.getColumns();
			tableColumns.forEach(poColumn -> {
				Column dbColumn = dbColumns.stream().filter(r -> r.getSqlName().equals(poColumn.getSqlName())).findAny().orElse(null);
				if (dbColumn == null) {
					String addColumnClause = dialect.buildAddColumnClause(poColumn);
					ddlList.add(addColumnClause);
				} else {
					//todo check is upgradable, compare and upgrade
//					int dbType = dbColumn.getSqlType();
//					int poType = poColumn.getSqlType();
//					System.out.println("tableName:" + tableName);
//					System.out.println("dbColumn:" + dbColumn.getSqlName() + " poColumn:" + poColumn.getSqlName());
//					System.out.println("dbType:" + dbType + " poType:" + poType);
				}
			});
			List<Index> dbIndices = dbTable.getIndices() == null ? Collections.EMPTY_LIST : dbTable.getIndices();
			indexList.forEach(index -> {
				boolean hasIndex = dbIndices.stream().anyMatch(r -> r.getName().equals(index.getName()));
				if (!hasIndex) {
					ddlList.add(dialect.buildIndexClause(index));
				}
			});
		}
	}

	protected Set<String> scanPackages(List<String> packages) {
		Set<String> classNames = new TreeSet<>();
		if (packages != null) {
			try {
				for (String pkg : packages) {
					String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
							ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
					Resource[] resources = this.resourcePatternResolver.getResources(pattern);
					MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
					for (Resource resource : resources) {
						if (resource.isReadable()) {
							MetadataReader reader = readerFactory.getMetadataReader(resource);
							String className = reader.getClassMetadata().getClassName();
							if (className.endsWith(PACKAGE_INFO_SUFFIX)) {
								continue;
							}
							AnnotationMetadata annotationMetadata = reader.getAnnotationMetadata();
							boolean hasAnnotation = annotationMetadata.hasAnnotation(javax.persistence.Table.class.getName());
							if (!hasAnnotation) {
								continue;
							}
							classNames.add(reader.getClassMetadata().getClassName());
						}
					}
				}
			} catch (IOException ex) {
				throw new RuntimeException("Failed to scan classpath for unlisted classes", ex);
			}
		}
		return classNames;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setPackagesToScan(List<String> packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	public void setDdlProcessor(DdlProcessor ddlProcessor) {
		this.ddlProcessor = ddlProcessor;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}
}
