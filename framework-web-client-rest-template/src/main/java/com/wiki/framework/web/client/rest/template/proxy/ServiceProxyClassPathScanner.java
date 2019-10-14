package com.wiki.framework.web.client.rest.template.proxy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/5/21 上午11:51
 */
public class ServiceProxyClassPathScanner extends ClassPathBeanDefinitionScanner {
	public ServiceProxyClassPathScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}


	public ServiceProxyClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
		super(registry, useDefaultFilters);
	}

	public ServiceProxyClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment) {
		super(registry, useDefaultFilters, environment);
	}

	public ServiceProxyClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment, ResourceLoader resourceLoader) {
		super(registry, useDefaultFilters, environment, resourceLoader);
	}

	/**
	 * Configures parent scanner to search for the right interfaces. It can search
	 * for all interfaces or just for those that extends a markerInterface or/and
	 * those annotated with the annotationClass
	 */
	public void registerFilters() {

		addIncludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
				return metadataReader.getClassMetadata().isInterface();
			}
		});

		addIncludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
				return metadataReader.getAnnotationMetadata().hasAnnotation(ServiceProxy.class.getName());
			}
		});

		// exclude package-info.java
		addExcludeFilter(new TypeFilter() {
			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
				String className = metadataReader.getClassMetadata().getClassName();
				return className.endsWith("package-info");
			}
		});
	}

	/**
	 * Calls the parent search that will search and register all the candidates.
	 * Then the registered objects are post processed to set them as
	 * MapperFactoryBeans
	 */
	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			logger.warn("No soa service was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
		} else {
			processBeanDefinitions(beanDefinitions);
		}

		return beanDefinitions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}

	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		GenericBeanDefinition definition;
		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (GenericBeanDefinition) holder.getBeanDefinition();
			if (logger.isDebugEnabled()) {
				logger.debug("Creating MapperFactoryBean with name '" + holder.getBeanName()
						+ "' and '" + definition.getBeanClassName() + "' mapperInterface");
			}
			try {
				String beanClassName = holder.getBeanDefinition().getBeanClassName();
				ServiceProxy serviceProxy = Class.forName(beanClassName).getAnnotation(ServiceProxy.class);
				// the mapper interface is the original class of the bean
				// but, the actual class of the bean is MapperFactoryBean
				definition.setLazyInit(true);
				definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName());
				definition.setBeanClass(ServiceProxyFactoryBean.class);
				definition.getPropertyValues().add("serviceName", serviceProxy.serviceName());
				definition.getPropertyValues().add("protocol", serviceProxy.protocol());
				if (StringUtils.isNotBlank(serviceProxy.restTemplateBeanName())) {
					definition.getPropertyValues().add("restTemplate", new RuntimeBeanReference(serviceProxy.restTemplateBeanName()));
				}else {
					throw new RuntimeException("no rest template to populate");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
