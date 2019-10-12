package com.wiki.framework.mybatis.dbinspector.autoconfig;

import com.wiki.framework.mybatis.dbinspector.dialect.Dialect;
import com.wiki.framework.mybatis.dbinspector.dialect.impl.MysqlDialect;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.lang.annotation.*;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/5/26 下午11:37
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({DbInspectorRegister.class, DdlProcessorConfigure.class})
@AutoConfigureAfter(DataSource.class)
public @interface DbInspect {
	/**
	 * 数据库方言，默认mysql
	 */
	Class<? extends Dialect> dialect() default MysqlDialect.class;

	/**
	 * 是否自动启用，默认启用
	 */
	@Deprecated
	boolean enabled() default true;

	/**
	 * 是否同步检查表结构
	 */
	@Deprecated
	boolean async() default false;

	/**
	 * Alias for the {@link #basePackages()} attribute. Allows for more concise
	 * annotation declarations e.g.:
	 * {@code @EnableMyBatisMapperScanner("org.my.pkg")} instead of {@code
	 *
	 * @EnableMyBatisMapperScanner(basePackages= "org.my.pkg"})}.
	 */
	String[] value() default {};

	/**
	 * Base packages to scan for MyBatis interfaces. Note that only interfaces
	 * with at least one method will be registered; concrete classes will be
	 * ignored.
	 */
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #basePackages()} for specifying the packages
	 * to scan for annotated components. The package of each class specified will be scanned.
	 * <p>Consider creating a special no-op marker class or interface in each package
	 * that serves no purpose other than being referenced by this attribute.
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * 处理ddl语句的实现类，默认是consoleDdlProcessor 直接将ddl打印在控制台
	 * 如果想自动执行ddl语句，可以将此值设置为jdbcDdlProcessor
	 */
	@Deprecated
	String ddlProcessorBeanName() default "jdbcDdlProcessor";
}
