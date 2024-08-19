package io.github.lorisdemicheli.hibernate_query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import io.github.lorisdemicheli.hibernate_query.annotation.Transform;
import io.github.lorisdemicheli.hibernate_query.exception.TransformException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;

public class QueryService {

	private EntityManager entityManager;

	public QueryService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public <T> List<T> getResultList(QueryType<T> queryFilter) {
		return new QueryBuilder(entityManager).buildSelect(queryFilter, true).getResultList();
	}

	public <T> T getFirstResult(QueryType<T> queryFilter) {
		return new QueryBuilder(entityManager).buildSelect(queryFilter, false).setMaxResults(1).getSingleResult();
	}

	public <T> Stream<T> getStreamResult(QueryType<T> queryFilter) {
		return new QueryBuilder(entityManager).buildSelect(queryFilter, true).getResultStream();
	}

	public <T> T getSingleResult(QueryType<T> queryFilter) {
		return new QueryBuilder(entityManager).buildSelect(queryFilter, true).getSingleResult();
	}

	public <T> Long count(QueryType<T> queryFilter) {
		return new QueryBuilder(entityManager).buildCount(queryFilter).getSingleResult();
	}

	public <T> Boolean hasResult(QueryType<T> queryFilter) {
		return new QueryBuilder(entityManager).buildHasResult(queryFilter).getSingleResult();
	}

	public <T> Page<T> getPagedResultList(QueryType<T> queryFilter, int pageNumber, int pageSize) {
		TypedQuery<T> query = new QueryBuilder(entityManager).buildSelect(queryFilter, false);
		query.setMaxResults(pageSize);
		query.setFirstResult(pageNumber * pageSize);
		Long totalElement = count(queryFilter);
		int tempSize = (int) (totalElement / pageSize);
		if (totalElement % pageSize != 0) {
			tempSize++;
		}
		return new Page<T>(query.getResultList(), tempSize, pageNumber, totalElement);
	}

	public <T> T getFirstResultIfAny(QueryType<T> queryFilter) {
		try {
			return getFirstResult(queryFilter);
		} catch (NoResultException e) {
			return null;
		}
	}

	public <T> T getSingleResultIfAny(QueryType<T> queryFilter) {
		try {
			return getSingleResult(queryFilter);
		} catch (NoResultException e) {
			return null;
		}
	}

	public <R, T> List<R> getTrasformResultList(QueryType<T> queryFilter, Class<R> trasformClass) {
		return transformList(new QueryBuilder(entityManager).buildTrasformSelect(queryFilter,trasformClass).getResultList(),trasformClass);
	}

	public <R, T> R getTrasformSingleResult(QueryType<T> queryFilter, Class<R> trasformClass) {
		return transformSingle(new QueryBuilder(entityManager).buildTrasformSelect(queryFilter,trasformClass).getSingleResult(), trasformClass);
	}
	
	private <R> R transformSingle(Tuple tuple, Class<R> trasformClass) {
		R instance = null;
		try {
			instance = ConstructorUtils.invokeConstructor(trasformClass);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
				| InstantiationException e) {
			throw new TransformException(e);
		}
		for(Field field : FieldUtils.getFieldsListWithAnnotation(trasformClass, Transform.class)) {
			Transform transform = field.getAnnotation(Transform.class);
			String name = transform.name() == null ? field.getName() : transform.name();
			try {
				FieldUtils.writeField(field, instance, tuple.get(name), false);
			} catch (IllegalAccessException e) {
				throw new TransformException(e);
			}
		}
		return instance;
	}

	private <R> List<R> transformList(List<Tuple> tuple, Class<R> trasformClass) {
		return tuple.stream().map(t -> transformSingle(t,trasformClass)).toList();
	}
}
