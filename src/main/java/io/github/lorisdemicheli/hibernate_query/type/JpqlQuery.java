package io.github.lorisdemicheli.hibernate_query.type;

import java.lang.reflect.Field;

import io.github.lorisdemicheli.hibernate_query.QueryType;
import io.github.lorisdemicheli.hibernate_query.annotation.CountQuery;
import io.github.lorisdemicheli.hibernate_query.annotation.Filter;
import io.github.lorisdemicheli.hibernate_query.annotation.HasResultQuery;
import io.github.lorisdemicheli.hibernate_query.annotation.Query;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class JpqlQuery<T> extends AbstractQuery<T,
	TypedQuery<T>,
	TypedQuery<Long>,
	TypedQuery<Boolean>> {

	public JpqlQuery(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	public TypedQuery<T> buildSelect(QueryType<T> queryFilter) {
		Query stringQuery = queryFilter.getClass().getAnnotation(Query.class);
		return entityManager.createQuery(stringQuery.value(), queryFilter.getType());
	}

	@Override
	public TypedQuery<Long> buildCount(QueryType<T> queryFilter) {
		CountQuery stringCountQuery = queryFilter.getClass().getAnnotation(CountQuery.class);
		return entityManager.createQuery(stringCountQuery.value(), Long.class);
	}

	@Override
	public TypedQuery<Boolean> buildHasResult(QueryType<T> queryFilter) {
		HasResultQuery hasResultQuery = queryFilter.getClass().getAnnotation(HasResultQuery.class);
		return entityManager.createQuery(hasResultQuery.value(), Boolean.class);
	}
	
	@Override
	public boolean filterValidation(Field field) {
		return field.isAnnotationPresent(Filter.class);
	}

	@Override
	public boolean canFetch() {
		return true;
	}
}
