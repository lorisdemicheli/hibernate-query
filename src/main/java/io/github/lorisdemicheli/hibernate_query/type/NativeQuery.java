package io.github.lorisdemicheli.hibernate_query.type;

import java.lang.reflect.Field;

import org.hibernate.Session;

import io.github.lorisdemicheli.hibernate_query.QueryType;
import io.github.lorisdemicheli.hibernate_query.annotation.CountQuery;
import io.github.lorisdemicheli.hibernate_query.annotation.Filter;
import io.github.lorisdemicheli.hibernate_query.annotation.HasResultQuery;
import jakarta.persistence.EntityManager;

public class NativeQuery<T> extends AbstractQuery<T,
	org.hibernate.query.NativeQuery<T>,
	org.hibernate.query.NativeQuery<Long>,
	org.hibernate.query.NativeQuery<Boolean>> {

	public NativeQuery(EntityManager entityManager) {
		super(entityManager);
	}

	@Override
	public org.hibernate.query.NativeQuery<T> buildSelect(QueryType<T> queryFilter) {
		io.github.lorisdemicheli.hibernate_query.annotation.Query stringQuery = 
				queryFilter.getClass().getAnnotation(io.github.lorisdemicheli.hibernate_query.annotation.Query.class);
		Session session = entityManager.unwrap(Session.class);
		return session.createNativeQuery(stringQuery.value(), queryFilter.getType());
	}

	@Override
	public org.hibernate.query.NativeQuery<Long> buildCount(QueryType<T> queryFilter) {
		CountQuery stringCountQuery = queryFilter.getClass().getAnnotation(CountQuery.class);
		Session session = entityManager.unwrap(Session.class);
		return session.createNativeQuery(stringCountQuery.value(), Long.class);
	}

	@Override
	public org.hibernate.query.NativeQuery<Boolean> buildHasResult(QueryType<T> queryFilter) {
		HasResultQuery hasResultQuery = queryFilter.getClass().getAnnotation(HasResultQuery.class);
		Session session = entityManager.unwrap(Session.class);
		return session.createNativeQuery(hasResultQuery.value(), Boolean.class);
	}

	@Override
	public boolean filterValidation(Field field) {
		return field.isAnnotationPresent(Filter.class);
	}

	@Override
	public boolean canFetch() {
		return false;
	}
}