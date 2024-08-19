package io.github.lorisdemicheli.hibernate_query.type;

import java.lang.reflect.Field;

import io.github.lorisdemicheli.hibernate_query.QueryType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;

public abstract class AbstractQuery<R,
	Q extends TypedQuery<R>,
	CQ extends TypedQuery<Long>, 
	HRQ extends TypedQuery<Boolean>,
	TQ extends TypedQuery<Tuple>> {

	protected EntityManager entityManager;

	public AbstractQuery(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public abstract Q buildSelect(QueryType<R> queryFilter);

	public abstract CQ buildCount(QueryType<R> queryFilter);
	
	public abstract HRQ buildHasResult(QueryType<R> queryFilter);
	
	public abstract <TR> TQ buildTransformSelect(QueryType<R> queryFilter, Class<TR> trasformClass);
	
	public abstract boolean filterValidation(Field field);
	
	public abstract boolean canFetch();
}
