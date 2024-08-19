package io.github.lorisdemicheli.hibernate_query.utils;

import java.util.Map;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;

public record QueryContext(CriteriaBuilder criteriaBuilder, Map<String, From<?, ?>> alias) {
}
