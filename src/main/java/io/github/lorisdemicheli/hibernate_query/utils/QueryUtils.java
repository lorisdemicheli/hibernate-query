package io.github.lorisdemicheli.hibernate_query.utils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import io.github.lorisdemicheli.hibernate_query.Expression;
import io.github.lorisdemicheli.hibernate_query.QueryType;
import io.github.lorisdemicheli.hibernate_query.annotation.Alias;
import io.github.lorisdemicheli.hibernate_query.annotation.Filter;
import io.github.lorisdemicheli.hibernate_query.exception.ParameterException;
import io.github.lorisdemicheli.hibernate_query.type.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaBuilder.In;

public class QueryUtils {
	
	public static final String DEFAULT_ROOT_ALIAS = "root";
	
	public static String fieldName(Field field) {
		String name;
		Filter filter = field.getAnnotation(Filter.class);
		if(StringUtils.isEmpty(filter.name())) {
			name = field.getName();
		} else {
			name = filter.name();
		}
		return name;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Field> getParameterFields(AbstractQuery<?,?,?,?,?> abstractQuery,
			Class<? extends QueryType> classQuery) {
		return FieldUtils.getAllFieldsList(classQuery).stream().filter(f->abstractQuery.filterValidation(f)).toList();
	}
	
	public static boolean isParameterActive(QueryType<?> query, Field field) {
		Filter filter = field.getAnnotation(Filter.class);
		try {
			Object value = FieldUtils.readField(field, query, true);
			boolean isCollectionValid = true;
			if(value != null && value instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) value;
				isCollectionValid = !collection.isEmpty();
			}
			return (value != null && isCollectionValid) || !filter.emptyOrNullExclude();
		} catch (IllegalAccessException e) {
			throw new ParameterException(String.format("Unable to read value of %s",field.getName()),e);
		}
	}

	public static Predicate convertField(QueryContext ctx, Field field) {
		ParameterExpression<?> parameter = ctx.criteriaBuilder().parameter(field.getType(), fieldName(field));
		Filter filter = field.getAnnotation(Filter.class);
		return convertExpression(ctx, filter, parameter, aliasPath(ctx, filter.path()));
	}

	public static Path<?> aliasPath(QueryContext ctx, String complexAttribute) {
		String aliasName = complexAttribute.split("\\.")[0];
		String other = complexAttribute.substring(complexAttribute.indexOf(".") + 1);
		Path<?> ret = ctx.alias().get(aliasName);
		while (!aliasName.equals(other)) {
			aliasName = other.split("\\.")[0];
			other = other.substring(other.indexOf("\\.") + 1);
			ret = ret.get(aliasName);
		}
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	public static String getRootAlias(Class<? extends QueryType> classQuery) {
		String rootAlias;
		Alias rootAliasAnnotation = classQuery.getAnnotation(Alias.class);
		if (rootAliasAnnotation != null) {
			rootAlias = rootAliasAnnotation.value();
		} else {
			rootAlias = DEFAULT_ROOT_ALIAS;
		}
		return rootAlias;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> Predicate convertExpression(QueryContext ctx, Filter filter,
			ParameterExpression<? extends T> parameter,
			jakarta.persistence.criteria.Expression<? extends T> fieldValue) {
		if (isComparable(filter.expression())) {
			return comparableExpression(ctx.criteriaBuilder(), filter.expression(), filter.negate(), fieldValue,
					(jakarta.persistence.criteria.Expression<? extends Comparable>) parameter);
		} else {
			return normalExpression(ctx.criteriaBuilder(), filter.expression(), filter.negate(), fieldValue, parameter);
		}
	}

	private static <Y extends Comparable<? super Y>> Predicate comparableExpression(CriteriaBuilder cb,
			Expression expression, boolean negate, jakarta.persistence.criteria.Expression<?> path,
			jakarta.persistence.criteria.Expression<Y> value) {
		@SuppressWarnings("unchecked")
		jakarta.persistence.criteria.Expression<? extends Y> cast = (jakarta.persistence.criteria.Expression<? extends Y>) path;
		return comparableValueExpression(cb, expression, negate, cast, value);
	}

	private static <Y> Predicate normalExpression(CriteriaBuilder cb, Expression expression, boolean negate,
			jakarta.persistence.criteria.Expression<?> path, jakarta.persistence.criteria.Expression<Y> value) {
		@SuppressWarnings("unchecked")
		jakarta.persistence.criteria.Expression<? extends Y> cast = (jakarta.persistence.criteria.Expression<? extends Y>) path;
		return normalValueExpression(cb, expression, negate, cast, value);
	}

	private static boolean isComparable(Expression expression) {
		return expression.equals(Expression.LESS_THAN) //
				|| expression.equals(Expression.GREATER_THAN) //
				|| expression.equals(Expression.LESS_EQUAL_THAN) //
				|| expression.equals(Expression.GREATER_EQUAL_THAN);
	}

	private static <Y extends Comparable<? super Y>> Predicate comparableValueExpression(CriteriaBuilder cb,
			Expression expression, boolean negate, jakarta.persistence.criteria.Expression<? extends Y> x,
			jakarta.persistence.criteria.Expression<Y> y) {

		switch (expression) {
		case LESS_THAN:
			if (negate) {
				return cb.greaterThanOrEqualTo(x, y);
			} else {
				return cb.lessThan(x, y);
			}
		case GREATER_THAN:
			if (negate) {
				return cb.lessThanOrEqualTo(x, y);
			} else {
				return cb.greaterThan(x, y);
			}
		case LESS_EQUAL_THAN:
			if (negate) {
				return cb.greaterThan(x, y);
			} else {
				return cb.lessThanOrEqualTo(x, y);
			}
		case GREATER_EQUAL_THAN:
			if (negate) {
				return cb.lessThan(x, y);
			} else {
				return cb.greaterThanOrEqualTo(x, y);
			}
		default:
			throw new RuntimeException("Unlock type");
		}
	}

	private static <Y> Predicate normalValueExpression(CriteriaBuilder cb, Expression expression, boolean negate,
			jakarta.persistence.criteria.Expression<? extends Y> x, jakarta.persistence.criteria.Expression<Y> y) {

		switch (expression) {
		case EQUAL:
			if (negate) {
				return cb.notEqual(x, y);
			} else {
				return cb.equal(x, y);
			}
		case LIKE:
			if (negate) {
				return cb.notLike(x.as(String.class), cb.concat(cb.concat("%", y.as(String.class)), "%"));
			} else {
				return cb.like(x.as(String.class), cb.concat(cb.concat("%", y.as(String.class)), "%"));
			}
		case START_WITH:
			if (negate) {
				return cb.notLike(x.as(String.class), cb.concat(y.as(String.class), "%"));
			} else {
				return cb.like(x.as(String.class), cb.concat(y.as(String.class), "%"));
			}
		case END_WITH:
			if (negate) {
				return cb.notLike(x.as(String.class), cb.concat("%", y.as(String.class)));
			} else {
				return cb.like(x.as(String.class), cb.concat("%", y.as(String.class)));
			}
		case IS_NULL:
			if (negate) {
				return cb.isNotNull(x);
			} else {
				return cb.isNull(x);
			}
		case IN:
			// DA VERIFICARE FORSE SERVE CAST CON EXPRESSION
			In<Object> in = cb.in(x);
			Collection<?> coll = (Collection<?>) y;
			for (Object val : coll) {
				in.value(val);
			}
			if (negate) {
				return in.not();
			} else {
				return in;
			}
		default:
			throw new RuntimeException("Unlock type");
		}
	}
}
