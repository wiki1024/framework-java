package com.wiki.framework.common.mapping.impl;


import com.wiki.framework.common.mapping.Mapper;
import com.wiki.framework.common.mapping.config.MappingConfig;
import com.wiki.framework.common.mapping.config.PropertyConfig;
import com.wiki.framework.common.util.DataType;
import com.wiki.framework.common.util.ReflectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author Thomason
 * Date: 11-4-20
 * Time: 下午5:30
 * @version 1.0
 */
@SuppressWarnings("Duplicates")
public class DefaultMapper implements Mapper {

	protected Logger logger = LoggerFactory.getLogger(DefaultMapper.class);
	private Map<String, MappingConfig> configMap;

	/**
	 * 从源对象复制属性到目标对象
	 *
	 * @param origin      源对象
	 * @param destination 目标对象
	 */
	@Override
	public void map(Object origin, Object destination) {
		if (origin == null || destination == null) {
			return;
		}
		map(origin, destination, (String) null);
	}

	@Override
	public void mapWithout(Object origin, Object destination, String[] excludeProps) {
		String mappingKey = MappingConfig.buildMappingKey(origin, destination, null);
		MappingConfig mappingConfig = configMap.get(mappingKey);
		if (mappingConfig == null) {
			//如果未配置，调用默认的属性复制器
			/*if (logger.isDebugEnabled()) {
				logger.debug("srcClass[" + origin.getClass() + "]and destClass[" + destination.getClass() + "] doesn't has a mappingConfig with mappingId:" + mappingKey + ",will use org.apache.commons.beanutils.BeanUtils to copy value");
			}*/
			copyWithout(origin, destination, excludeProps);
		} else {
			copyWithout(origin, destination, mappingConfig, excludeProps);
		}
	}

	@Override
	public void mapWith(Object origin, Object destination, String[] includeProps) {
		/*String mappingKey = MappingConfig.buildMappingKey(origin, destination, null);
		MappingConfig mappingConfig = configMap.get(mappingKey);*/
		copyWith(origin, destination, includeProps);
	}

	/**
	 * fixme 修改枚举型变量的赋值问题
	 * 从源对象复制属性到目标对象
	 *
	 * @param origin      源对象
	 * @param destination 目标对象
	 * @param mappingId   映射文件Id
	 */
	@Override
	public void map(Object origin, Object destination, String mappingId) {
		String mappingKey = MappingConfig.buildMappingKey(origin, destination, mappingId);
		MappingConfig mappingConfig = configMap.get(mappingKey);
		if (mappingConfig == null) {
			//如果未配置，调用默认的属性复制器
			/*if (logger.isDebugEnabled()) {
				logger.debug("srcClass[" + origin.getClass() + "]and destClass[" + destination.getClass() + "] doesn't has a mappingConfig with mappingId:" + mappingId + ",will use org.apache.commons.beanutils.BeanUtils to copy value");
			}*/
			copyWithout(origin, destination, null);
		} else {
			copyWithout(origin, destination, mappingConfig, null);
		}
	}

	/**
	 * 复制源对象的所有属性到目标对象中
	 *
	 * @param origin      源对象
	 * @param destination 目标对象
	 */
	@Override
	public void clone(Object origin, Object destination) {
		copyWithout(origin, destination, null);
	}

	/**
	 * 根据映射配置复制对象属性
	 *
	 * @param orig          原始对象
	 * @param dest          目标对象
	 * @param mappingConfig 映射配置
	 */
	private void copyWithout(Object orig, Object dest, MappingConfig mappingConfig, String[] excludeProps) {
		Set<String> ignorePropertySet = createIgnoreSet(dest);
		if (excludeProps != null) {
			Collections.addAll(ignorePropertySet, excludeProps);
		}
		for (PropertyConfig propertyCfg : mappingConfig.getActualMappingPropertyList()) {
			try {
				String destPropertyName = propertyCfg.getDestPropertyName();
				//忽略不需要复制的属性
				if (ignorePropertySet.contains(destPropertyName)) {
					continue;
				}
				Field origFiled = ReflectionUtils.getDeclaredField(orig, propertyCfg.getSrcPropertyName());
				Field destFiled = ReflectionUtils.getDeclaredField(orig, propertyCfg.getDestPropertyName());
				copyFieldValue(orig, dest, origFiled, destFiled, (s, d) -> copyWithout(s, d, mappingConfig, excludeProps));
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug("convert object:" + orig.getClass() + " field:[" + propertyCfg.getSrcPropertyName() + "] to object:" + dest + " field[" + propertyCfg.getDestPropertyName() + "] with exception:" + e.getCause());
				}
			}
		}
	}

	/**
	 * 创建被复制对象的忽略属性列表
	 *
	 * @param dest 被复制的对象
	 * @return 自动过滤的忽略列表
	 */
	private Set<String> createIgnoreSet(Object dest) {
		Set<String> ignorePropertySet = new HashSet<String>();
		/*if (dest instanceof BasePO) {
			ignorePropertySet.add("id");
			ignorePropertySet.add("version");
			ignorePropertySet.add("tenantId");
			ignorePropertySet.add("createBy");
			ignorePropertySet.add("createTime");
			ignorePropertySet.add("updateBy");
			ignorePropertySet.add("updateTime");
			ignorePropertySet.add("isDeleted");
		}*/
		return ignorePropertySet;
	}

	/**
	 * 复制简单类型的值
	 *
	 * @param orig           源对象
	 * @param destination    目标对象
	 * @param propertyConfig 属性配置对象
	 */
	private void copyBasicTypeValue(Object orig, Object destination, PropertyConfig propertyConfig) {
		String srcPropertyName = propertyConfig.getSrcPropertyName();
		int srcPropertyType = propertyConfig.getSrcPropertyType();
		if (srcPropertyType == DataType.DT_Unknown) {
			srcPropertyType = DataType.DT_String;
		}
		String destPropertyName = propertyConfig.getDestPropertyName();
		int destPropertyType = propertyConfig.getDestPropertyType();
		if (destPropertyType == DataType.DT_Unknown) {
			destPropertyType = DataType.DT_String;
		}
		Object propVal = null;
		if (orig instanceof Map) {
			propVal = ((Map) orig).get(srcPropertyName);
		} else {
			propVal = ReflectionUtils.getFieldValue(orig, srcPropertyName);
		}
		if (destination instanceof Map) {
			((Map) destination).put(destPropertyName, propVal);
		} else {
			//如果是相同类型，直接赋值
			if (isSameType(srcPropertyType, destPropertyType)) {
				ReflectionUtils.setFieldValue(destination, destPropertyName, propVal);
			}
			//如果不是相同类型，进行类型装换
			else {
				Object transformedValue = DataType.toType(propVal, srcPropertyType, destPropertyType);
				ReflectionUtils.setFieldValue(destination, destPropertyName, transformedValue);
			}
		}
	}

	/**
	 * @param origin       源对象
	 * @param destination  目标对象
	 * @param includeProps 忽略属性列表
	 */
	protected void copyWith(Object origin, Object destination, String[] includeProps) {
		//如果是自定义范围内的类，去掉自动的主键和version复制
		for (String includeProp : includeProps) {
			try {
				Field srcField = ReflectionUtils.getDeclaredField(origin.getClass(), includeProp);
				if (srcField == null) {
//					logger.warn("the property:[" + includeProp + "] doesn't appear in srcObject:" + origin.getClass());
					continue;
				}
				int modifiers = srcField.getModifiers();
				//静态属性和final属性不copy
				if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
//					logger.warn("the property:[" + includeProp + "] is static or final can't copy");
					continue;
				}

				Field destField = ReflectionUtils.getDeclaredField(destination.getClass(), includeProp);
				if (destField == null) {
//					logger.warn("the property:[" + includeProp + "] doesn't appear in destObject:" + destination.getClass());
					continue;
				}
				//复制属性值
				copyFieldValue(origin, destination, srcField, destField, (s, d) -> copyWith(s, d, includeProps));
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug("convert object:" + origin + " to object:" + destination + " on field:" + includeProp + "with exception:" + e.getCause());
				}
			}
		}
	}

	/**
	 * @param origin       源对象
	 * @param destination  目标对象
	 * @param excludeProps 忽略属性列表
	 */
	protected void copyWithout(Object origin, Object destination, String[] excludeProps) {
		//如果是自定义范围内的类，去掉自动的主键和version复制
		Set<String> ignorePropertySet = createIgnoreSet(destination);
		if (excludeProps != null) {
			Collections.addAll(ignorePropertySet, excludeProps);
		}

		List<Field> srcFiledList = ReflectionUtils.getDeclaredFields(origin);
		for (Field srcField : srcFiledList) {
			try {
				int modifiers = srcField.getModifiers();
				//静态属性和final属性不copy
				if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
					continue;
				}
				//如果包含在过滤列表中，跳过
				if (ignorePropertySet.contains(srcField.getName())) {
					continue;
				}
				Field destField = ReflectionUtils.getDeclaredField(destination.getClass(), srcField.getName());
				//复制属性值
				copyFieldValue(origin, destination, srcField, destField, (s, d) -> copyWithout(s, d, excludeProps));
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug("convert object:" + origin + " to object:" + destination + " on field:" + srcField.getName() + "with exception:" + e.getCause());
				}
			}
		}
	}

	/**
	 * 复制对象属性
	 *
	 * @param origin      源对象
	 * @param destination 目标对象
	 * @param srcField    源对象的属性
	 */
	private void copyFieldValue(Object origin, Object destination, Field srcField, Field destField, BiConsumer<Object, Object> consumer) throws IllegalAccessException {
		//目标类如果没有相同属性，跳过
		if (destField == null || srcField == null) {
			return;
		}
		int modifiers = destField.getModifiers();
		//static或final属性不copy
		if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
			return;
		}
		Class<?> srcFieldType = srcField.getType();
		//枚举值
		Class<?> destFieldType = destField.getType();
		Object srcFieldValue = getFieldValue(origin, srcField);
		if (srcFieldValue == null) {
			return;
		}
		int srcDataType = DataType.getDataType(srcFieldType);
		if (DataType.isSimpleType(srcDataType)) {
			int destDataType = DataType.getDataType(destFieldType);
			if (isSameType(srcDataType, destDataType)) {
				setFieldValue(destination, destField, srcFieldValue);
			} else if (destDataType == DataType.DT_ENUM) {
				Enum enumValue = Enum.valueOf((Class<Enum>) destFieldType, String.valueOf(srcFieldValue));
				setFieldValue(destination, destField, enumValue);
			} else {
				//类型转换
				Object transformedValue = DataType.toType(srcFieldValue, srcDataType, destDataType);
				setFieldValue(destination, destField, transformedValue);
			}
		}
		if (srcFieldType.isEnum()) {
			if (!Modifier.isPublic(modifiers)
					|| !Modifier.isPublic(destField.getDeclaringClass().getModifiers())) {
				destField.setAccessible(true);
			}
			if (destFieldType.isEnum()) {
				destField.set(destination, srcFieldValue);
			} else {
				int destDataType = DataType.getDataType(destFieldType);
				if (DataType.DT_String == destDataType) {
					destField.set(destination, String.valueOf(srcFieldValue));
				}
			}
			return;
		}
		//TODO 集合暂不处理
		else if (srcDataType == DataType.DT_List) {
			List srcList = (List) srcFieldValue;
			List<Object> destList = new ArrayList<>();
			String typeParameter = getTypeParameter(destField);
			if (typeParameter == null) {
				logger.warn("can't copy none for property:" + destField.getName());
				return;
			}
			srcList.forEach(c -> {
				try {
					Object dest = ClassUtils.getClass(typeParameter).newInstance();
					consumer.accept(c, dest);
//					map(c, dest, ignoreProps);
					destList.add(dest);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			setFieldValue(destination, destField, destList);
		} else if (srcDataType == DataType.DT_Set) {
			Set srcSet = (Set) srcFieldValue;
			Set<Object> destSet = new HashSet<>();
			String typeParameter = getTypeParameter(destField);
			if (typeParameter == null) {
				logger.warn("can't copy none for property:" + destField.getName());
				return;
			}
			srcSet.forEach(s -> {
				try {
					Object dest = ClassUtils.getClass(typeParameter).newInstance();
					consumer.accept(s, dest);
//					map(s, dest, ignoreProps);
					destSet.add(dest);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			setFieldValue(destination, destField, destSet);
		} else if (srcDataType == DataType.DT_Array) {
			Object[] srcArray = (Object[]) srcFieldValue;
			Object[] destArray = new Object[srcArray.length];
			String arrayType = getArrayType(destField);
			if (arrayType == null) {
				logger.warn("can't copy none for property:" + destField.getName());
				return;
			}
			for (int i = 0; i < srcArray.length; i++) {
				try {
					Object o = srcArray[i];
					Object dest = ClassUtils.getClass(arrayType).newInstance();
					consumer.accept(o, dest);
//					map(o, dest, ignoreProps);
					destArray[i] = dest;
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
			setFieldValue(destination, destField, destArray);
		} else if (DataType.isMapType(srcDataType)) {
			//map类型直接copy
			setFieldValue(destination, destField, srcFieldValue);
		}
		//TODO 自定义对象的状况
		else if (srcDataType == DataType.DT_UserDefine) {
			int destDataType = DataType.getDataType(destFieldType);
			//两者都是自定义属性时才会copy
			if (destDataType == DataType.DT_UserDefine) {
				Object destFieldValue =getFieldValue(destination,destField);
				if (destFieldValue == null) {
					try {
						destFieldValue = destFieldType.newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
					setFieldValue(destination, destField,destFieldValue);
				}
				consumer.accept(srcFieldValue, destFieldValue);
//				map(srcFieldValue, destFieldValue);
			}
		}
	}

	private void setFieldValue(Object destination, Field destField, Object transformedValue) throws IllegalAccessException {
		if (!Modifier.isPublic(destField.getModifiers())
				|| !Modifier.isPublic(destField.getDeclaringClass().getModifiers())) {
			destField.setAccessible(true);
		}
		destField.set(destination, transformedValue);
	}

	private Object getFieldValue(Object origin, Field srcField) throws IllegalAccessException {
		if (!Modifier.isPublic(srcField.getModifiers())
				|| !Modifier.isPublic(srcField.getDeclaringClass().getModifiers())) {
			srcField.setAccessible(true);
		}
		return srcField.get(origin);
	}

	/**
	 * 判断两个类型是否相同
	 *
	 * @param srcType  源类型
	 * @param destType 目标类型
	 * @return 是否是相同类型
	 */
	protected boolean isSameType(Object srcType, Object destType) {
		return srcType == destType;
	}

	public Map<String, MappingConfig> getConfigMap() {
		return configMap;
	}

	public void setConfigMap(Map<String, MappingConfig> configMap) {
		this.configMap = configMap;
	}

	public String getTypeParameter(Field field) {
		ParameterizedType pt = (ParameterizedType) field.getGenericType();
		if (pt != null && pt.getActualTypeArguments() != null && pt.getActualTypeArguments().length > 0) {
			Type type = pt.getActualTypeArguments()[0];
			return type.getTypeName();
		}
		return null;
	}

	public String getArrayType(Field field) {
		String typeName = field.getGenericType().getTypeName();
		if (typeName.endsWith("[]")) {
			return typeName.substring(0, typeName.length() - 2);
		}
		return null;
	}
}
