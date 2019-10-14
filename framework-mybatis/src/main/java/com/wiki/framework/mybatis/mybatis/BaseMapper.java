package com.wiki.framework.mybatis.mybatis;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wiki.framework.common.dto.PageResponse;
import com.wiki.framework.common.util.Assertion;
import com.wiki.framework.common.util.StringUtil;
import com.wiki.framework.common.util.lambda.SFunction;

import com.wiki.framework.mybatis.mybatis.listener.MybatisListenerContainer;
import com.wiki.framework.mybatis.mybatis.listener.spi.CriteriaUpdateListener;
import com.wiki.framework.mybatis.mybatis.listener.spi.POUpdateListener;
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
		List<POUpdateListener> preInsertListeners = MybatisListenerContainer.getPreInsertListeners();
		if (CollectionUtils.isNotEmpty(preInsertListeners)) {
			for (POUpdateListener preInsertListener : preInsertListeners) {
				preInsertListener.apply(po);
			}
		}
		int rows = _insert(po);
		//todo post insert listener 处理
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
		List<POUpdateListener> preInsertListeners = MybatisListenerContainer.getPreInsertListeners();
		//pre insert listener 处理
		if (CollectionUtils.isNotEmpty(preInsertListeners)) {
			for (POUpdateListener preInsertListener : preInsertListeners) {
				for (T t : poList) {
					preInsertListener.apply(t);
				}
			}
		}
		int rows = _batchInsert(poList, new HashMap<>());
		//todo post insert listener 处理
		return rows;
	}

	/*******************************全部更新方法********************************/
	@UpdateProvider(type = MybatisSqlBuilder.class, method = "update")
	int _update(@Param("po") T po, @Param("versionable") boolean versionable, @Param("columns") Set<String> columns);

	default int _doUpdate(T po, Set<String> columns) {
		return _update(po, false, columns);
	}

	default int update(T po, String... columns) {
		List<POUpdateListener> prePOUpdateListeners = MybatisListenerContainer.getPrePOUpdateListeners();
		//pre update listener 处理
		if (CollectionUtils.isNotEmpty(prePOUpdateListeners)) {
			for (POUpdateListener preInsertListener : prePOUpdateListeners) {
				preInsertListener.apply(po);
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
		List<POUpdateListener> prePOUpdateListeners = MybatisListenerContainer.getPrePOUpdateListeners();
		//pre update listener 处理
		if (CollectionUtils.isNotEmpty(prePOUpdateListeners)) {
			for (POUpdateListener preInsertListener : prePOUpdateListeners) {
				preInsertListener.apply(po);
			}
		}
		Set<String> includeColumns = null;
		if (columns != null && columns.length > 0) {
			includeColumns = new HashSet<>(Arrays.asList(columns));
		}
		int rows = _doUpdateSelective(po, includeColumns);

		//post update listener 处理
		return rows;
	}

	/*******************************批量选择更新方法********************************/
	@UpdateProvider(type = MybatisSqlBuilder.class, method = "batchUpdateSelective")
	int _batchUpdateSelective(@Param("clazz") Class<T> clazz, @Param("list") List<T> poList, @Param("includeColumns") Set<String> includeColumns);

	default int batchUpdateSelective(List<T> poList, String... includeColumns) {
		if (poList == null || poList.size() == 0) {
			return 0;
		}
		List<POUpdateListener> prePOUpdateListeners = MybatisListenerContainer.getPrePOUpdateListeners();
		//pre update listener 处理
		if (CollectionUtils.isNotEmpty(prePOUpdateListeners)) {
			for (POUpdateListener preInsertListener : prePOUpdateListeners) {
				boolean result = true;
				for (T t : poList) {
					preInsertListener.apply(t);
				}
			}
		}

		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
		Set<String> includeColumnSet = new HashSet<>();
		if (includeColumns != null) {
			includeColumnSet.addAll(Arrays.asList(includeColumns));
		}
		int rows = _batchUpdateSelective(entityClass, poList, includeColumnSet);
		return rows;
	}

	@UpdateProvider(type = MybatisSqlBuilder.class, method = "batchUpdate")
	int _batchUpdate(@Param("clazz") Class<T> clazz, @Param("list") List<T> poList, @Param("includeColumns") Set<String> includeColumns);

	default int batchUpdate(List<T> poList, String... columns) {
		if (poList == null || poList.size() == 0) {
			return 0;
		}
		List<POUpdateListener> prePOUpdateListeners = MybatisListenerContainer.getPrePOUpdateListeners();
		//pre update listener 处理
		if (CollectionUtils.isNotEmpty(prePOUpdateListeners)) {
			for (POUpdateListener preInsertListener : prePOUpdateListeners) {
				boolean result = true;
				for (T t : poList) {
					preInsertListener.apply(t);
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
		return rows;
	}

	/*******************************删除方法********************************/
	@DeleteProvider(type = MybatisSqlBuilder.class, method = "delete")
	int _delete(@Param("map") Map<String, Object> map, @Param("clazz") Class<T> clazz);

	default int delete(String id) {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];

		Criteria idCriteria = BaseMapperUtil.createIdCriteria(id);
		int rows = deleteByCriteria(idCriteria);

		return rows;
	}


	/*******************************按照查询条件删除********************************/
	@DeleteProvider(type = MybatisSqlBuilder.class, method = "deleteByCriteria")
	int _deleteByCriteria(@Param("clazz") Class<T> clazz, @Param("criteria") Criteria criteria, @Param("map") Map<String, Object> map);

	default int deleteByCriteria(Criteria criteria) {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
		Map<String, Object> map = new HashMap<>();
		List<CriteriaUpdateListener> queryListeners = MybatisListenerContainer.getDeleteListeners();
		for (CriteriaUpdateListener queryListener : queryListeners) {
			queryListener.apply(entityClass, criteria, map);
		}
		int rows = _deleteByCriteria(entityClass, criteria, map);
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
		Assertion.isTrue(criteria != null, "criteria cannot be null");
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
		List<CriteriaUpdateListener> queryListeners = MybatisListenerContainer.getQueryListeners();
		HashMap<String, Object> _map = new HashMap<>();
		for (CriteriaUpdateListener queryListener : queryListeners) {
			queryListener.apply(entityClass, criteria, _map);
		}
		return _findByCriteria(entityClass, criteria, _map);
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


	default T findOne(SFunction<T, ?> lambda, Object propValue) {
		List<T> list = findByProperty(lambda, propValue);
		return CollectionUtils.isEmpty(list) ? null : list.get(0);
	}

	@SelectProvider(type = MybatisSqlBuilder.class, method = "countByCondition")
	Long _countByCriteria(@Param("clazz") Class<T> clazz, @Param("criteria") Criteria criteria, @Param("map") Map<String, Object> map);

	default Long countByCriteria(Criteria criteria) {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getInterfaces()[0].getGenericInterfaces()[0]).getActualTypeArguments()[0];
		List<CriteriaUpdateListener> queryListeners = MybatisListenerContainer.getQueryListeners();
		HashMap<String, Object> map = new HashMap<>();
		for (CriteriaUpdateListener queryListener : queryListeners) {
			queryListener.apply(entityClass, criteria, map);
		}
		return _countByCriteria(entityClass, criteria, map);
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
