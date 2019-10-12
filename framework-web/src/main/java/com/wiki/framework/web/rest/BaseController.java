package com.wiki.framework.web.rest;

import com.wiki.framework.common.dto.ActionResult;
import com.wiki.framework.common.dto.PageResponse;
import com.wiki.framework.common.mapping.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseController {

	@Autowired
	private Mapper mapper;

	protected <T> ActionResult<T> ok(T date) {
		ActionResult<T> result = new ActionResult<>();
		result.setData(date);
		result.setSuccess(true);
		return result;
	}

	protected ActionResult<String> ok() {
		return ok("ok");
	}

	protected <T> PageResponse<T> mapPage(PageResponse from, Class<T> toClazz) {

		PageResponse<T> to = new PageResponse<>();
		to.setTotal(from.getTotal());
		if (CollectionUtils.isEmpty(from.getRows())) {
			return to;
		}
		List<Object> rows = (List<Object>) from.getRows();
		Stream<T> tStream = rows.stream().map(r -> mapTo(r, toClazz));
		to.setRows(tStream.collect(Collectors.toList()));
		return to;
	}

	protected <T> T mapTo(Object from, Class<T> toClazz) {
		if(from == null) return null;
		try {
			T t = toClazz.newInstance();
			mapper.map(from, t);
			return t;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
