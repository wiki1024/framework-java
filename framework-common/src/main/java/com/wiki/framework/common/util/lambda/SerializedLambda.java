package com.wiki.framework.common.util.lambda;

/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 这个类是从 {@link java.lang.invoke.SerializedLambda} 里面 copy 过来的，
 * 字段信息完全一样
 * <p>负责将一个支持序列的 Function 序列化为 SerializedLambda</p>
 *
 * @author HCL
 * @since 2018/05/10
 */
@SuppressWarnings("unused")
public class SerializedLambda implements Serializable {

	private static final long serialVersionUID = 8025925345765570181L;

	private Class<?> capturingClass;
	private String functionalInterfaceClass;
	private String functionalInterfaceMethodName;
	private String functionalInterfaceMethodSignature;
	private String implClass;
	private String implMethodName;
	private String implMethodSignature;
	private int implMethodKind;
	private String instantiatedMethodType;
	private Object[] capturedArgs;

	/**
	 * 通过反序列化转换 lambda 表达式，该方法只能序列化 lambda 表达式，不能序列化接口实现或者正常非 lambda 写法的对象
	 *
	 * @param lambda lambda对象
	 * @return 返回解析后的 SerializedLambda
	 */
	public static SerializedLambda resolve(SFunction<?, ?> lambda) {
		if (!lambda.getClass().isSynthetic()) {
			throw new LambdaParseException("该方法仅能传入 lambda 表达式产生的合成类");
		}
		try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(serialize(lambda))) {
			@Override
			protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
				Class<?> clazz = super.resolveClass(objectStreamClass);
				return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
			}
		}) {
			return (SerializedLambda) objIn.readObject();
		} catch (ClassNotFoundException | IOException e) {
			throw new LambdaParseException("This is impossible to happen", e);
		}
	}

	/**
	 * Serialize the given object to a byte array.
	 *
	 * @param object the object to serialize
	 * @return an array of bytes representing the object in a portable fashion
	 */
	static byte[] serialize(Object object) {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
		} catch (IOException ex) {
			throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
		}
		return baos.toByteArray();
	}

	/**
	 * 获取接口 class
	 *
	 * @return 返回 class 名称
	 */
	public String getFunctionalInterfaceClassName() {
		return normalName(functionalInterfaceClass);
	}

	/**
	 * 获取实现的 class
	 *
	 * @return 实现类
	 */
	public Class<?> getImplClass() {
		return ClassUtils.toClassConfident(getImplClassName());
	}

	/**
	 * 获取 class 的名称
	 *
	 * @return 类名
	 */
	public String getImplClassName() {
		return normalName(implClass);
	}

	/**
	 * 获取实现者的方法名称
	 *
	 * @return 方法名称
	 */
	public String getImplMethodName() {
		return implMethodName;
	}

	/**
	 * 正常化类名称，将类名称中的 / 替换为 .
	 *
	 * @param name 名称
	 * @return 正常的类名
	 */
	private String normalName(String name) {
		return name.replace('/', '.');
	}

	private static final Pattern INSTANTIATED_METHOD_TYPE = Pattern.compile("\\(L(?<instantiatedMethodType>[\\S&&[^;)]]+);\\)L[\\S]+;");

	public Class getInstantiatedMethodType() {
		Matcher matcher = INSTANTIATED_METHOD_TYPE.matcher(instantiatedMethodType);
		if (matcher.find()) {
			return ClassUtils.toClassConfident(normalName(matcher.group("instantiatedMethodType")));
		}
		throw new LambdaParseException("无法从 " + instantiatedMethodType + " 解析调用实例。。。");
	}

	/**
	 * @return 字符串形式
	 */
	@Override
	public String toString() {
		return String.format("%s -> %s::%s", getFunctionalInterfaceClassName(), getImplClass().getSimpleName(),
				implMethodName);
	}

}

