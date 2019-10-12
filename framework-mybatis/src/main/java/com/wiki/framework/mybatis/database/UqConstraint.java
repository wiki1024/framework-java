//package com.wiki.framework.mybatis.database;
//
//import com.wiki.framework.mybatis.po.CommonPO;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.util.Assert;
//
//import java.util.TreeSet;
//import java.util.function.Function;
//import java.util.regex.Pattern;
//
//public class UqConstraint {
//
//	public static final String Post_Table_Fix = "_framework_unique";
//
//	private static Logger logger = LoggerFactory.getLogger(UqConstraint.class);
//
//	private String name;
//
//	private Pattern pattern;
//
//	private TreeSet<String> javaColumnNames = new TreeSet<>();
//
//	private TreeSet<Column> columns = new TreeSet<>();
//
//	private Function<CommonPO, String> lambda; //用于自定义计算唯一约束值
//
//	public static String getUniqueTableName(Table table) {
//		return table.getSqlName() + UqConstraint.Post_Table_Fix;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		Assert.isTrue(StringUtils.isNotBlank(name), "uq name cannot be null");
//		this.name = name;
//		pattern = Pattern.compile(name + "-");
//	}
//
//	public TreeSet<Column> getColumns() {
//		return columns;
//	}
//
//	public Function<CommonPO, String> getLambda() {
//		return lambda;
//	}
//
//	public void setLambda(Function<CommonPO, String> lambda) {
//		this.lambda = lambda;
//	}
//
//	public void addColumn(Column column) {
//		if (columns.contains(column)) {
//			logger.error("repeated column definition for index {}, column name {}", name, column.getSqlName());
//		}
//		columns.add(column);
//		javaColumnNames.add(column.getJavaName());
//	}
//
//	public boolean isCustom() {
//		return lambda != null;
//	}
//
//	public boolean hasJavaColumn(String javaName) {
//		return javaColumnNames.contains(javaName);
//	}
//
//	public Pattern getPattern() {
//		return pattern;
//	}
//}
