package com.wiki.framework.web.client.rest.template.Proxy;

import com.wiki.framework.common.dto.ActionResult;
import com.wiki.framework.common.dto.ErrorInfo;
import com.wiki.framework.common.error.BusinessException;
import com.wiki.framework.common.error.ErrorCode;
import com.wiki.framework.common.util.ReflectionUtils;
import com.wiki.framework.web.client.rest.template.Factory.ClientHttpRequestFactoryBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author thomason
 * @version 1.0
 * @since 2018/5/21 上午11:51
 */
public class ServiceProxyFactoryBean<T> implements FactoryBean<T>, MethodInterceptor {
	private static Logger logger = LoggerFactory.getLogger(ServiceProxyFactoryBean.class);

	private String serviceName;

	private String protocol;

	private RestTemplate restTemplate;

	private Class<T> serviceInterface;

	public ServiceProxyFactoryBean(Class<T> interfaze) {
		this.serviceInterface = interfaze;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();
		String methodName = method.getName();
		if ("toString".equals(methodName) && method.getParameterTypes().length == 0) {
			return this.toString();
		} else if ("hashCode".equals(methodName) && method.getParameterTypes().length == 0) {
			return this.hashCode();
		} else if ("equals".equals(methodName) && method.getParameterTypes().length == 1) {
			return false;
		}
		ServiceProfile serviceProfile = populateServiceProfile(method);

		Type type = method.getGenericReturnType();
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
		Annotation[][] arrays = method.getParameterAnnotations();
		Object httpBody = null;
		NotNullMap<String, Object> uriParams = new NotNullMap<>();
		int argIndex = 0;
		for (Annotation[] annotations : arrays) {
			for (Annotation annotation : annotations) {
				Object argument = arguments[argIndex];
				if (annotation instanceof RequestBody) {
					httpBody = argument;
				} else if (annotation instanceof PathVariable) {
					PathVariable param = (PathVariable) annotation;
					uriParams.put(param.value(), argument);
				} else if (annotation instanceof RequestParam) {
					RequestParam param = (RequestParam) annotation;
					String name = param.name();
					if (StringUtils.isBlank(name)) {
						name = param.value();
					}
					if (StringUtils.isBlank(name)) {
						name = method.getParameters()[argIndex].getName();
					}
					String defaultValue = param.defaultValue();
					boolean required = param.required();
					if (argument == null) {
						if (required) {
							uriParams.put(name, defaultValue);
						}
					} else {
						uriParams.put(name, argument);
					}
				} else if (annotation instanceof RequestHeader) {
					String headerName = ((RequestHeader) annotation).value();
					String defaultValue = ((RequestHeader) annotation).defaultValue();
					if (StringUtils.isNotBlank(headerName)) {
						if (argument != null) {
							httpHeaders.add(headerName, String.valueOf(argument));
						} else {
							httpHeaders.add(headerName, defaultValue);
						}
					}
				} else if (annotation instanceof ModelAttribute) {
					Map<String, Object> map = object2Map(argument);
					uriParams.putAll(map);
				} else if (annotation instanceof RequestPart) {
					String name = ((RequestPart) annotation).name();
					String value = ((RequestPart) annotation).value();
					httpBody = new HashMap<>();
					String paraName = name;
					if (StringUtils.isBlank(name)) {
						paraName = value;
					}
					if (argument instanceof File) {
						FileInputStream fileInputStream = new FileInputStream((File) argument);
						InputStreamResource inputStreamSource = new InputStreamResource(fileInputStream);
						((HashMap) httpBody).put(paraName, inputStreamSource);
					} else if (argument instanceof InputStream) {

					}
				}
			}
			argIndex++;
		}
		Pair<String, NotNullMap<String, Object>> pair = appendParams(serviceProfile.getRequestUrl(), uriParams);
		String requestUrl = pair.getLeft();
		HttpEntity<Object> httpEntity = new HttpEntity<>(httpBody, httpHeaders);
		long start = System.currentTimeMillis();
		Object object;
		try {
			ParameterizedTypeReference<Object> typeReference = ParameterizedTypeReference.forType(type);
			ResponseEntity<Object> exchange = serviceProfile.getRestTemplate()
					.exchange(requestUrl, serviceProfile.getHttpMethod(), httpEntity, typeReference, pair.getRight());
			object = exchange.getBody();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(new ErrorInfo(ErrorCode.ServerError.getErrorCode(), "fail:" + e.getMessage()));
		}
		if (object instanceof ActionResult) {
			ActionResult actionResult = (ActionResult) object;
			if (!actionResult.isSuccess()) {
				throw new BusinessException(actionResult.getErrors());
			}
		}
		return object;
	}

	private ServiceProfile populateServiceProfile(Method method) {
		ServiceProfile serviceProfile = new ServiceProfile();
		serviceProfile.setServiceName(serviceName);
		serviceProfile.setProtocol(protocol);
		serviceProfile.setRestTemplate(restTemplate);
		GetMapping getMapping = method.getAnnotation(GetMapping.class);
		if (getMapping != null) {
			serviceProfile.setHttpMethod(HttpMethod.GET);
			serviceProfile.setUrl(getMapping.value()[0]);
		}
		PostMapping postMapping = method.getAnnotation(PostMapping.class);
		if (postMapping != null) {
			serviceProfile.setHttpMethod(HttpMethod.POST);
			serviceProfile.setUrl(postMapping.value()[0]);
		}
		PutMapping putMapping = method.getAnnotation(PutMapping.class);
		if (putMapping != null) {
			serviceProfile.setHttpMethod(HttpMethod.PUT);
			serviceProfile.setUrl(putMapping.value()[0]);
		}
		DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
		if (deleteMapping != null) {
			serviceProfile.setHttpMethod(HttpMethod.DELETE);
			serviceProfile.setUrl(deleteMapping.value()[0]);
		}
		RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
		if (requestMapping != null) {
			serviceProfile.setUrl(requestMapping.value()[0]);
			RequestMethod requestMethod = requestMapping.method()[0];
			switch (requestMethod) {
				case GET:
					serviceProfile.setHttpMethod(HttpMethod.GET);
					break;
				case POST:
					serviceProfile.setHttpMethod(HttpMethod.POST);
					break;
				case PUT:
					serviceProfile.setHttpMethod(HttpMethod.PUT);
					break;
				case DELETE:
					serviceProfile.setHttpMethod(HttpMethod.DELETE);
					break;
				case HEAD:
					serviceProfile.setHttpMethod(HttpMethod.HEAD);
					break;
				default:
					serviceProfile.setHttpMethod(HttpMethod.POST);
					break;
			}
		}
		//check service profile
		serviceProfile.check();
		return serviceProfile;
	}

	private Pair<String, NotNullMap<String, Object>> appendParams(String url, Map<String, Object> requestParams) {
		StringBuilder sb = new StringBuilder(url);
		NotNullMap<String, Object> uriParams = new NotNullMap<>();
		if (requestParams != null && requestParams.size() > 0) {
			if (!url.contains("?")) {
				sb.append("?");
			}
			for (String key : requestParams.keySet()) {
				Object value = requestParams.get(key);
				if (value.getClass().isArray()) {
					//处理简单类型
					String simpleName = value.getClass().getSimpleName();
					if ("int[]".equals(simpleName)) {
						int[] values = (int[]) value;
						for (int i = 0; i < values.length; i++) {
							int o = values[i];
							sb.append(key).append("={").append(key).append("_" + i + "}&");
							uriParams.put(key + "_" + i, o);
						}
					} else if ("long[]".equals(simpleName)) {
						long[] values = (long[]) value;
						for (int i = 0; i < values.length; i++) {
							long o = values[i];
							sb.append(key).append("={").append(key).append("_").append(i).append("}&");
							uriParams.put(key + "_" + i, o);
						}
					} else if ("char[]".equals(simpleName)) {
						char[] values = (char[]) value;
						for (int i = 0; i < values.length; i++) {
							char o = values[i];
							sb.append(key).append("={").append(key).append("_").append(i).append("}&");
							uriParams.put(key + "_" + i, o);
						}
					} else if ("double[]".equals(simpleName)) {
						double[] values = (double[]) value;
						for (int i = 0; i < values.length; i++) {
							double o = values[i];
							sb.append(key).append("={").append(key).append("_").append(i).append("}&");
							uriParams.put(key + "_" + i, o);
						}
					} else if ("byte[]".equals(simpleName)) {
						byte[] values = (byte[]) value;
						for (int i = 0; i < values.length; i++) {
							byte o = values[i];
							sb.append(key).append("={").append(key).append("_").append(i).append("}&");
							uriParams.put(key + "_" + i, o);
						}
					} else if ("short[]".equals(simpleName)) {
						short[] values = (short[]) value;
						for (int i = 0; i < values.length; i++) {
							short o = values[i];
							sb.append(key).append("={").append(key).append("_").append(i).append("}&");
							uriParams.put(key + "_" + i, o);
						}
					} else if ("boolean[]".equals(simpleName)) {
						boolean[] values = (boolean[]) value;
						for (int i = 0; i < values.length; i++) {
							boolean o = values[i];
							sb.append(key).append("={").append(key).append("_").append(i).append("}&");
							uriParams.put(key + "_" + i, o);
						}
					} else if ("float[]".equals(simpleName)) {
						float[] values = (float[]) value;
						for (int i = 0; i < values.length; i++) {
							float o = values[i];
							sb.append(key).append("={").append(key).append("_").append(i).append("}&");
							uriParams.put(key + "_" + i, o);
						}
					} else {
						Object[] values = (Object[]) value;
						for (int i = 0; i < values.length; i++) {
							Object o = values[i];
							sb.append(key).append("={").append(key).append("_" + i + "}&");
							uriParams.put(key + "_" + i, o);
						}
					}
				} else if (value instanceof Iterable) {
					Iterable values = (Iterable) value;
					int i = 0;
					for (Object o : values) {
						sb.append(key).append("={").append(key).append("_" + i + "}&");
						uriParams.put(key + "_" + i, o);
						i++;
					}
				} else {
					sb.append(key).append("={").append(key).append("}&");
					uriParams.put(key, value);
				}
			}
			sb.deleteCharAt(sb.length() - 1);
		}
		return Pair.of(sb.toString(), uriParams);
	}

	private Map<String, Object> object2Map(Object object) {
		if (object == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<>(30);
		List<Field> fields = ReflectionUtils.getDeclaredFields(object);
		if (CollectionUtils.isNotEmpty(fields)) {
			for (Field field : fields) {
				map.put(field.getName(), ReflectionUtils.getFieldValue(object, field));
			}
		}
		return map;
	}

	@Override
	public T getObject() throws Exception {
		return (T) new ProxyFactory(serviceInterface, this).getProxy(ClassUtils.getDefaultClassLoader());
	}

	@Override
	public Class<?> getObjectType() {
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
