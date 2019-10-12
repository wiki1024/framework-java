package com.wiki.framework.mybatis.mybatis;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wiki.framework.common.dto.PageResponse;
import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.common.util.lambda.SFunction;

import com.wiki.framework.mybatis.mybatis.listener.MybatisListenerContainer;
import com.wiki.framework.mybatis.mybatis.listener.spi.*;
import com.wiki.framework.mybatis.mybatis.util.BaseMapperUtil;
import com.wiki.framework.mybatis.po.CommonPO;
import com.wiki.framework.mybatis.query.PageRequest;
import com.wiki.framework.mybatis.query.v2.Criteria;
import com.wiki.framework.mybatis.query.v2.Operator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.*;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * @author thomason
 * @version 1.0
 * @since 2017/8/2 下午3:45
 */
@SuppressWarnings({"unchecked", "Duplicates"})
//todo cache reflection
public interface BaseMapper<T extends CommonPO> {

	/*******************************插入方法********************************/
	default int insert(T po) {
		//pre insert listener 处理
		List<PreInsertListener> preInsertListeners = MybatisListenerContainer.getPreInsertListeners();
		if (CollectionUtils.isNotEmpty(preInsertListeners)) {
			for (PreInsertListener preInsertListener : preInsertListeners) {
				boolean execute = preInsertListener.preInsert(po);
				if (!execute) {
					return 0;
				}
			}
		}
		int rows = _insert(po);
		//post insert listener 处理
		List<PostInsertListener> postInsertListeners = MybatisListenerContainer.getPostInsertListeners();
		if (CollectionUtils.isNotEmpty(postInsertListeners)) {
			postInsertListeners.forEach(postInsertListener -> postInsertListener.postInsert(po));
		}
		return rows;
	}

	@InsertProvider(type = MybatisSqlBuilder.class, method = "insert")
	int _insert(T po);

	/*******************************批量插入方法********************************/
	@InsertProvider(type = MybatisSqlBuilder.class, method = "batchInsert")
	int _batchInsert(@Param("list") List<T> poList, @Param("map") Map<String, Object> map);

	default int batchInsert(@Param("list") List<T> poList) {
		if (poList == null || poList.size() == 0) {
			return 0;
		}
		List<PreInsertListener> preInsertListeners = MybatisListenerContainer.getPreInsertListeners();
		//pre insert listener 处理
		if (CollectionUtils.isNotEmpty(preInsertListeners)) {
			for (PreInsertListener preInsertListener : preInsertListeners) {
				boolean result = true;
				for (T t : poList) {
					boolean execute = preInsertListener.preInsert(t);
					result = execute && result;
				}
				if (!result) {
					return 0;
				}
			}
		}
		int rows = _batchInsert(poList, new HashMap<>());
		//post insert listener 处理
		List<PostInsertListener> postInsertListeners = MybatisListenerContainer.getPostInsertListeners();
		if (CollectionUtils.isNotEmpty(postInsertListeners)) {
			postInsertListeners.forEach(postInsertListener -> poList.forEach(postInsertListener::postInsert));
		}
		return rows;
	}

	/*******************************全部更新方法********************************/
	@UpdateProvider(type = MybatisSqlBuilder.class, method = "update")
	int _update(@Param("po") T po, @Param("versionable") boolean versionable, @Param("columns") Set<String> columns);

	default int _doUpdate(T po, Set<String> columns) {
		return _update(po, false, columns);
	}

	default int update(T po, String... columns) {
		List<PreUpdateListener> preUpdateListeners = MybatisListenerContainer.getPreUpdateListeners();
		//pre update listener 处理
		if (CollectionUtils.isNotEmpty(preUpdateListeners)) {
			for (PreUpdateListener preInsertListener : preUpdateListeners) {
				boolean execute = preInsertListener.preUpdate(po);
				if (!execute) {
					return 0;
				}
			}
		}
		Set<String> includeColumns = null;
		if (columns != null && columns.length > 0) {
			includeColumns = new HashSet<>(Arrays.asList(columns));
			includeColumns.add("updateTime");
			includeColumns.add("updateBy");
		}
		int rows = _doUpdate(po, includeColumns);

		//post update listener 处理
		List<PostUpdateListener> postUpdateListeners = MybatisListenerContainer.getPostUpdateListeners();
		if (CollectionUtils.isNotEmpty(postUpdateListeners)) {
			postUpdateListeners.forEach(postUpdateListener -> postUpdateListener.postUpdate(po));
		}
		return rows;
	}

	/*******************************选择更新方法********************************/
	@UpdateProvider(type = MybatisSqlBuilder.class, method = "updateSelective")
	int _updateSelective(@Param("po") T po, @Param("versionable") boolean versionable, @Param("columns") Set<String> includeColumns);

	default int _doUpdateSelective(T po, Set<String> columns) {
		return _updateSelective(po, false, columns);
	}

	/**
	 * exclude null field
	 *
	 * @param po
	 * @param columns
	 * @return
	 */
	default int updateSelective(T po, String... columns) {
		List<PreUpdateListener> preUpdateListeners = MybatisListenerContainer.getPreUpdateListeners();
		//pre update listener 处理
		if (CollectionUtils.isNotEmpty(preUpdateListeners)) {
			for (PreUpdateListener preInsertListener : preUpdateListeners) {
				boolean execute = preInsertListener.preUpdate(po);
				if (!execute) {
					return 0;
				}
			}
		}
		Set<String> includeColumns = null;
		if (columns != null && columns.length > 0) {
			includeColumns = new HashSet<>(Arrays.asList(columns));
		}
		int rows = _doUpdateSelective(po,  includeColumns);

		//post update listener 处理
		List<PostUpdateListener> postUpdateListeners = MybatisListenerContainer.getPostUpdateListeners();
		if (CollectionUtils.isNotEmpty(postUpdateListeners)) {
			postUpdateListeners.forEach(postUpdateListener -> postUpdateListener.postUpdate(po));
		}
		return rows;
	}

	/*******************************批量选择更新方法********************************/
	@UpdateProvider(type = MybatisSqlBuilder.class, method = "batchUpdateSelective")
	int _batchUpdateSelective(@Param("clazz") Class<T> clazz, @Param("list") List<T> poList, @Param("includeColumns") Set<String> includeColumns);

	default int batchUpdateSelective(List<T> poList, String... includeColumns) {
		if (poList == null || poList.size() == 0) {
			return 0;
		}
		List<PreUpdateListener> preUpdateListeners = MybatisListenerContainer.getPreUpdateListeners();
		//pre update listener 处理
		if (CollectionUtils.isNotEmpty(preUpdateListeners)) {
			for (PreUpdateListener preInsertListener : preUpdateListeners) {
				boolean result = true;
				for (T t : poList) {
					boolean execute = preInsertListener.preUpdate(t);
					result = result && execute;
				}
				if (!result) {
					return 0;
				}
			}
		}

		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
		Set<String> includeColumnSet = new HashSet<>();
		if (includeColumns != null) {
			includeColumnSet.addAll(Arrays.asList(includeColumns));
		}
		int rows = _batchUpdateSelective(entityClass, poList, includeColumnSet);

		//post insert listener 处理
		List<PostUpdateListener> postUpdateListeners = MybatisListenerContainer.getPostUpdateListeners();
		if (CollectionUtils.isNotEmpty(postUpdateListeners)) {
			postUpdateListeners.forEach(postUpdateListener -> poList.forEach(postUpdateListener::postUpdate));
		}
		return rows;
	}

	@UpdateProvider(type = MybatisSqlBuilder.class, method = "batchUpdate")
	int _batchUpdate(@Param("clazz") Class<T> clazz, @Param("list") List<T> poList, @Param("includeColumns") Set<String> includeColumns);

	default int batchUpdate(List<T> poList, String... columns) {
		if (poList == null || poList.size() == 0) {
			return 0;
		}
		List<PreUpdateListener> preUpdateListeners = MybatisListenerContainer.getPreUpdateListeners();
		//pre update listener 处理
		if (CollectionUtils.isNotEmpty(preUpdateListeners)) {
			for (PreUpdateListener preInsertListener : preUpdateListeners) {
				boolean result = true;
				for (T t : poList) {
					boolean execute = preInsertListener.preUpdate(t);
					result = result && execute;
				}
				if (!result) {
					return 0;
				}
			}
		}

		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
		Set<String> includeColumns = new HashSet<>();
		if (columns != null && columns.length > 0) {
			includeColumns = new HashSet<>(Arrays.asList(columns));
			includeColumns.add("updateTime");
			includeColumns.add("updateBy");
		}
		int rows = _batchUpdate(entityClass, poList, includeColumns);

		//post insert listener 处理
		List<PostUpdateListener> postUpdateListeners = MybatisListenerContainer.getPostUpdateListeners();
		if (CollectionUtils.isNotEmpty(postUpdateListeners)) {
			postUpdateListeners.forEach(postUpdateListener -> poList.forEach(postUpdateListener::postUpdate));
		}
		return rows;
	}

	/*******************************删除方法********************************/
	@DeleteProvider(type = MybatisSqlBuilder.class, method = "delete")
	int _delete(@Param("map") Map<String, Object> map, @Param("clazz") Class<T> clazz);

	default int delete(String id) {
		T po = null;
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
		//pre postDelete listener 处理
		List<PreDeleteListener> preDeleteListeners = MybatisListenerContainer.getPreDeleteListeners();
		if (CollectionUtils.isNotEmpty(preDeleteListeners)) {
			po = get(id);
			for (PreDeleteListener preInsertListener : preDeleteListeners) {
				boolean execute = preInsertListener.preDelete(po);
				if (!execute) {
					return 0;
				}
			}
		}
		Criteria idCriteria = BaseMapperUtil.createIdCriteria(id);
		int rows = deleteByCondition(idCriteria);

		//post postDelete listener 处理
		List<PostDeleteListener> postDeleteListeners = MybatisListenerContainer.getPostDeleteListeners();
		if (CollectionUtils.isNotEmpty(postDeleteListeners)) {
			if (po == null) {
				po = get(id);
			}
			for (PostDeleteListener postDeleteListener : postDeleteListeners) {
				postDeleteListener.postDelete(po);
			}
		}
		return rows;
	}


	/*******************************按照查询条件删除********************************/
	@DeleteProvider(type = MybatisSqlBuilder.class, method = "deleteByCriteria")
	int _deleteByCondition(@Param("clazz") Class<T> clazz, @Param("criteria") Criteria criteria, @Param("map") Map<String, Object> map);

	default int deleteByCondition(Criteria criteria) {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
		criteria.and("isDeleted", Operator.equal, "0");
		Map<String, Object> map = new HashMap<>();
		map.put("updateTime", new Date());
		int rows = _deleteByCondition(entityClass, criteria, map);
		return rows;
	}

	/*******************************按照主键查询********************************/
	@SelectProvider(type = MybatisSqlBuilder.class, method = "get")
	T _get(@Param("id") String id, @Param("clazz") Class<T> clazz);

	default T get(String id) {
		Criteria criteria = BaseMapperUtil.createIdCriteria(id);
		List<T> ts = findByCriteria(criteria);
		if (ts != null && ts.size() > 0) {
			Assert.isTrue(ts.size() == 1, "按id查出多个记录");
			return ts.get(0);
		}
		return null;
	}


	@SelectProvider(type = MybatisSqlBuilder.class, method = "findByCriteria")
	List<T> _findByCriteria(@Param("clazz") Class<T> clazz, @Param("criteria") Criteria criteria, @Param("map") Map<String, Object> map);

	default List<T> findByCriteria(Criteria criteria) {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
//		if (ReflectionUtils.isSubClass(entityClass, BasePO.class)) {
//			criteria.and("tenantId", Operator.equal, SystemContext.getTenantId());
//		}
//		if (ReflectionUtils.isSubClass(entityClass, BaseProjectPO.class)) {
//			criteria.and("projectId", Operator.equal, SystemContext.getProjectId());
//		}
		criteria.and("isDeleted", Operator.equal, "0");
		return _findByCriteria(entityClass, criteria, new HashMap<>());
	}

	/**
	 * 根据查询条件查询单个记录
	 *
	 * @param criteria 查询条件封装类
	 * @return 记录
	 */
	default T findOne(Criteria criteria) {
		List<T> list = findByCriteria(criteria);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	/**
	 * 分页查询对象
	 *
	 * @param request 查询条件
	 * @return 分页查询结果
	 */
	default PageResponse<T> findPage(PageRequest request) {
		if (request.isNeedPaging()) {
			PageHelper.startPage(request.getPageNo(), request.getPageSize(), request.isNeedCount());
		}
		List<T> list = this.findByCriteria(request.getCriteria());
		PageInfo<T> pageInfo = new PageInfo<>(list);
		PageResponse<T> response = new PageResponse<>();
		response.setTotal(pageInfo.getTotal());
		response.setRows(pageInfo.getList());
		return response;
	}

	default List<T> findByProperty(SFunction<T, ?> lambda, Object propValue) {
		Criteria and = Criteria.create().and(lambda, Operator.equal, String.valueOf(propValue));
		return findByCriteria(and);
	}

	default List<T> findByProperty(String propName, Object propValue) {
		Criteria criteria = new Criteria();
		criteria.and(propName, Operator.equal, String.valueOf(propValue));
		return findByCriteria(criteria);
	}

	default T findOne(String propName, Object propValue) {
		List<T> list = findByProperty(propName, propValue);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	@SelectProvider(type = MybatisSqlBuilder.class, method = "countByCondition")
	Long _countByCriteria(@Param("clazz") Class<T> clazz, @Param("criteria") Criteria criteria, @Param("map") Map<String, Object> map);

	default Long countByCriteria(Criteria criteria) {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
//		if (ReflectionUtils.isSubClass(entityClass, BasePO.class)) {
//			criteria.and("tenantId", Operator.equal, SystemContext.getTenantId());
//		}
//		if (ReflectionUtils.isSubClass(entityClass, BaseProjectPO.class)) {
//			criteria.and("projectId", Operator.equal, SystemContext.getProjectId());
//		}
		criteria.and("isDeleted", Operator.equal, "0");
		return _countByCriteria(entityClass, criteria, new HashMap<>());
	}

	default boolean checkRepeat(String id, String propName, Object propValue) {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
		Criteria criteria = new Criteria();
		if (StringUtil.isNotBlank(id)) {
			criteria.and("id", Operator.notEqual, id);
		}
		criteria.and(propName, Operator.equal, String.valueOf(propValue));
		Long aLong = countByCriteria(criteria);
		return aLong > 0;
	}

}
