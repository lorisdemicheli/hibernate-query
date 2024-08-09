package io.github.lorisdemicheli.hibernate_query.utils;

import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;

public class QueryContext {
	private CriteriaBuilder criteriaBuilder;
	private Map<String, From<?, ?>> alias;

	public QueryContext(CriteriaBuilder criteriaBuilder, Map<String, From<?, ?>> alias) {
		this.criteriaBuilder = criteriaBuilder;
		this.alias = alias;
	}

	public CriteriaBuilder criteriaBuilder() {
		return criteriaBuilder;
	}

	public Map<String, From<?, ?>> alias() {
		return alias;
	}
}
