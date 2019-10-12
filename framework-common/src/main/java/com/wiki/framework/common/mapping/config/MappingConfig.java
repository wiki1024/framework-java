package com.wiki.framework.common.mapping.config;

import com.wiki.framework.common.util.DataType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Thomason
 * Date: 11-4-20
 * Time: 下午5:11
 * @version 1.0
 */
public class MappingConfig {
	private String mappingKey;
	/**
	 * 映射配置的Id
	 */
	private String mappingId;
	/**
	 * 匹配方向
	 * one-way 单向
	 * two-way 双向
	 */
	private String direction;

	/**
	 * 映射源类的类型
	 */
	private String srcClassType;

	/**
	 * 映射的目标类类型
	 */
	private String destClassType;

	/**
	 * 包含的属性列表
	 */
	private List<PropertyConfig> includeList;
	/**
	 * 过滤的属性列表
	 */
	private List<PropertyConfig> excludeList;
	/**
	 * 实际生效的属性映射列表
	 */
	private List<PropertyConfig> actualMappingPropertyList;

	/**
	 * 所有符合条件的映射列表
	 */
	private List<PropertyConfig> allMatchedList;

	/**
	 * 构建映射的主键
	 *
	 * @param src       源对象
	 * @param dest      目标对象
	 * @param mappingId 映射Id
	 * @return 映射主键
	 */
	public static String buildMappingKey(Object src, Object dest, String mappingId) {
		return buildMappingKey(src.getClass().getName(), dest.getClass().getName(), mappingId);
	}

	/**
	 * 构建映射主键
	 *
	 * @param srcClassName  源对象类名
	 * @param destClassName 目标对象类名
	 * @param mappingId     映射Id
	 * @return 映射主键
	 */
	public static String buildMappingKey(String srcClassName, String destClassName, String mappingId) {
		if (StringUtils.isNotEmpty(mappingId)) {
			return srcClassName + "->" + destClassName + ":" + mappingId;
		} else {
			return srcClassName + "->" + destClassName;
		}
	}

	/**
	 * 计算实际生效的属性复制列表
	 */
	public void calculateActualPropertyMappingList() throws CloneNotSupportedException {
		//如果设置了include，就是用include
		if (this.getIncludeList() != null && this.getIncludeList().size() > 0) {
			this.setActualMappingPropertyList(this.getIncludeList());
		}
		//如果没有是用include，就用全部的映射属性减去过滤的映射属性
		else {
			// 取得所有的属性列表
			List<PropertyConfig> allMatchedProperty = getAllMatchedProperty();
			this.setAllMatchedList(allMatchedProperty);
			final List<PropertyConfig> copyedAllMatched = new LinkedList<PropertyConfig>();
			for (PropertyConfig propertyConfig : allMatchedProperty) {
				PropertyConfig clone = propertyConfig.clone();
				copyedAllMatched.add(clone);
			}
			//如果过滤列表不为空，那么判断过滤列表
			final List<PropertyConfig> excludesList = this.getExcludeList();

			if (excludesList != null && excludesList.size() > 0) {
				//使用apache的过滤器
				CollectionUtils.filter(copyedAllMatched, new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						for (PropertyConfig property : excludesList) {
							if (object.equals(property)) {
								return false;
							}
						}
						return true;
					}
				});
			}
			this.setActualMappingPropertyList(copyedAllMatched);
		}
	}

	/**
	 * 取得源对象和目标对象中的属性名称相同的列
	 *
	 * @return 源对象和目标对象中的属性名称相同的列
	 * @throws ClassNotFoundException
	 */
	public List<PropertyConfig> getAllMatchedProperty() {
		final List<PropertyConfig> ret = new ArrayList<PropertyConfig>();
		try {
			ReflectionUtils.doWithFields(Class.forName(srcClassType), new ReflectionUtils.FieldCallback() {
				@Override
				public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
					if (Modifier.isStatic(field.getModifiers())) {
						return;
					}
					if (Modifier.isStatic(field.getDeclaringClass().getModifiers())) {
						return;
					}
					String fieldName = field.getName();
					boolean mateched = true;
					PropertyConfig propertyConfig = new PropertyConfig();
					propertyConfig.setSrcPropertyName(fieldName);
					propertyConfig.setSrcPropertyType(DataType.getDataType(field.getType()));
					//如果源类和目标类是同一个类，那么目标属性就和源属性一样
					if (srcClassType.equals(destClassType)) {
						propertyConfig.setDestPropertyName(propertyConfig.getSrcPropertyName());
						propertyConfig.setDestPropertyType(propertyConfig.getSrcPropertyType());
					}
					//否则，取得目标类的相同属性名的filed
					else {
						try {
							Field destField = ReflectionUtils.findField(Class.forName(destClassType), fieldName);
							if (destField != null) {
								propertyConfig.setDestPropertyName(fieldName);
								propertyConfig.setDestPropertyType(DataType.getDataType(destField.getType()));
							} else {
								mateched = false;
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
					if (mateched) {
						ret.add(propertyConfig);
					}
				}
			});
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public String getMappingId() {
		return mappingId;
	}

	public void setMappingId(String mappingId) {
		this.mappingId = mappingId;
	}


	public String getMappingKey() {
		return mappingKey;
	}

	public void setMappingKey(String mappingKey) {
		this.mappingKey = mappingKey;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getSrcClassType() {
		return srcClassType;
	}

	public void setSrcClassType(String srcClassType) {
		this.srcClassType = srcClassType;
	}

	public String getDestClassType() {
		return destClassType;
	}

	public void setDestClassType(String destClassType) {
		this.destClassType = destClassType;
	}

	public List<PropertyConfig> getIncludeList() {
		return includeList;
	}

	public void setIncludeList(List<PropertyConfig> includeList) {
		this.includeList = includeList;
	}

	public List<PropertyConfig> getExcludeList() {
		return excludeList;
	}

	public void setExcludeList(List<PropertyConfig> excludeList) {
		this.excludeList = excludeList;
	}

	public List<PropertyConfig> getActualMappingPropertyList() {
		return actualMappingPropertyList;
	}

	public void setActualMappingPropertyList(List<PropertyConfig> actualMappingPropertyList) {
		this.actualMappingPropertyList = actualMappingPropertyList;
	}

	public List<PropertyConfig> getAllMatchedList() {
		return allMatchedList;
	}

	public void setAllMatchedList(List<PropertyConfig> allMatchedList) {
		this.allMatchedList = allMatchedList;
	}
}
