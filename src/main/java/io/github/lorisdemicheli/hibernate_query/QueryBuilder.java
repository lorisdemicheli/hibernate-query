package io.github.lorisdemicheli.hibernate_query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import io.github.lorisdemicheli.hibernate_query.annotation.CountQuery;
import io.github.lorisdemicheli.hibernate_query.annotation.Fetch;
import io.github.lorisdemicheli.hibernate_query.annotation.HasResultQuery;
import io.github.lorisdemicheli.hibernate_query.exception.ParameterException;
import io.github.lorisdemicheli.hibernate_query.type.AbstractQuery;
import io.github.lorisdemicheli.hibernate_query.type.CriteriaQuery;
import io.github.lorisdemicheli.hibernate_query.type.JpqlQuery;
import io.github.lorisdemicheli.hibernate_query.type.NativeQuery;
import io.github.lorisdemicheli.hibernate_query.utils.QueryUtils;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Graph;
import jakarta.persistence.TypedQuery;

public class QueryBuilder {
	
	private EntityManager em;
	
	public QueryBuilder(EntityManager em) {
		this.em = em;
	}

	public <T> TypedQuery<T> buildSelect(QueryType<T> queryFilter, boolean fetch) {
		AbstractQuery<T,? extends TypedQuery<T>,? extends TypedQuery<Long>,? extends TypedQuery<Boolean>> abstractQuery;
		io.github.lorisdemicheli.hibernate_query.annotation.Query stringQuery = 
				queryFilter.getClass().getAnnotation(io.github.lorisdemicheli.hibernate_query.annotation.Query.class);
		if (stringQuery != null) {
			if (stringQuery.nativeSql()) {
				abstractQuery = new NativeQuery<T>(em);
			} else {
				abstractQuery = new JpqlQuery<T>(em);
			}
		} else {
			abstractQuery = new CriteriaQuery<T>(em);
		}
		TypedQuery<T> query = abstractQuery.buildSelect(queryFilter);
		setParameters(query, abstractQuery, queryFilter);
		if(abstractQuery.canFetch() && fetch) {
			setFetch(query, queryFilter); //org.hibernate.graph.internal.RootGraphImpl Not yet implemented after this uncomment
		}
		return query;
	}
	
	public <T> TypedQuery<Long> buildCount(QueryType<T> queryFilter) {
		AbstractQuery<T,? extends TypedQuery<T>,? extends TypedQuery<Long>,? extends TypedQuery<Boolean>> abstractQuery;
		CountQuery stringCountQuery = queryFilter.getClass().getAnnotation(CountQuery.class);
		if (stringCountQuery != null) {
			if (stringCountQuery.nativeSql()) {
				abstractQuery = new NativeQuery<T>(em);
			} else {
				abstractQuery = new JpqlQuery<T>(em);
			}
		} else {
			abstractQuery = new CriteriaQuery<T>(em);
		}
		TypedQuery<Long> query = abstractQuery.buildCount(queryFilter);
		setParameters(query, abstractQuery, queryFilter);
		return query;
	}
	
	public <T> TypedQuery<Boolean> buildHasResult(QueryType<T> queryFilter) {
		AbstractQuery<T,? extends TypedQuery<T>,? extends TypedQuery<Long>,? extends TypedQuery<Boolean>> abstractQuery;
		HasResultQuery hasResultQuery = queryFilter.getClass().getAnnotation(HasResultQuery.class);
		if (hasResultQuery != null) {
			if (hasResultQuery.nativeSql()) {
				abstractQuery = new NativeQuery<T>(em);
			} else {
				abstractQuery = new JpqlQuery<T>(em);
			}
		} else {
			abstractQuery = new CriteriaQuery<T>(em);
		}
		TypedQuery<Boolean> query = abstractQuery.buildHasResult(queryFilter);
		setParameters(query, abstractQuery, queryFilter);
		return query;
	}
	
	private <T> void setFetch(TypedQuery<?> query, QueryType<T> queryFilter) {
		EntityGraph<T> entityGraph = em.createEntityGraph(queryFilter.getType());
		for(Fetch fetch : getFetchs(queryFilter.getClass())) {
			String aliasName = fetch.path().split("\\.")[0];
			String other = fetch.path().substring(fetch.path().indexOf(".") + 1);
			Graph<?> graph = entityGraph;
			while (!aliasName.equals(other)) {
				aliasName = other.split("\\.")[0];
				other = other.substring(other.indexOf("\\.") + 1);
				if(aliasName.equals(other)) {
					graph.addAttributeNode(other);
				} else {
					graph = graph.addSubgraph(aliasName);
				}
			}
		}
		query.setHint("jakarta.persistence.fetchgraph", entityGraph);
	}
	
	@SuppressWarnings("rawtypes")
	private List<Fetch> getFetchs(Class<? extends QueryType> classQuery) {
		Fetch.List fetchList = classQuery.getAnnotation(Fetch.List.class);
		Fetch fetchSingle = classQuery.getAnnotation(Fetch.class);
		List<Fetch> fetchs = new ArrayList<>();
		if (fetchList != null) {
			fetchs.addAll(Arrays.asList(fetchList.fetchs()));
		} else if (fetchSingle != null) {
			fetchs.add(fetchSingle);
		}
		return fetchs;
	}
	
	private void setParameters(TypedQuery<?> query, 
			AbstractQuery<?,?,?,?> abstractQuery, QueryType<?> queryFilter) {
		for(Field field : QueryUtils.getParameterFields(abstractQuery, queryFilter.getClass())) {
			try {
				if(QueryUtils.isParameterActive(queryFilter, field)) {
					String name = QueryUtils.fieldName(field);
					query.setParameter(name,FieldUtils.readField(field, queryFilter, true));
				}
			} catch (IllegalAccessException e) {
				throw new ParameterException(String.format("Unable to read value of %s",field.getName()),e);
			}
		}
	}
}
