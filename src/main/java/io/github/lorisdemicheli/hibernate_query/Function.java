package io.github.lorisdemicheli.hibernate_query;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.reflect.ConstructorUtils;

import io.github.lorisdemicheli.hibernate_query.annotation.Transform;
import io.github.lorisdemicheli.hibernate_query.exception.FunctionException;
import io.github.lorisdemicheli.hibernate_query.utils.QueryContext;
import io.github.lorisdemicheli.hibernate_query.utils.QueryUtils;
import jakarta.persistence.criteria.Selection;

public enum Function implements QueryFunctionTransform {
	
	PATH {
		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
			return QueryUtils.aliasPath(context, select.path()[0].value());
		}

		@Override
		public boolean validate(QueryContext context, Transform select) {
			return select.path().length == 1 && select.attribute().length == 0;
		}
	},
	COUNT {
		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
			return context.criteriaBuilder().count(QueryUtils.aliasPath(context, select.path()[0].value()));
		}

		@Override
		public boolean validate(QueryContext context, Transform select) {
			return select.path().length == 1 && select.attribute().length == 0;
		}
	},
	MAX {
		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
			return context.criteriaBuilder().max(QueryUtils.aliasPath(context, select.path()[0].value(),Number.class));
		}

		@Override
		public boolean validate(QueryContext context, Transform select) {
			return select.path().length == 1 && select.attribute().length == 0;
		}
	},
	MIN {
		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
			return context.criteriaBuilder().min(QueryUtils.aliasPath(context, select.path()[0].value(),Number.class));
		}

		@Override
		public boolean validate(QueryContext context, Transform select) {
			return select.path().length == 1 && select.attribute().length == 0;
		}
	},
	AVG {
		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
			return context.criteriaBuilder().avg(QueryUtils.aliasPath(context, select.path()[0].value(),Number.class));
		}

		@Override
		public boolean validate(QueryContext context, Transform select) {
			return select.path().length == 1 && select.attribute().length == 0;
		}
	},
	CONCAT {
		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
			List<jakarta.persistence.criteria.Expression<String>> pathString = Stream.of(select.path())
					.map(p->p.value())
					.map(v->(jakarta.persistence.criteria.Expression<String>) QueryUtils.aliasPath(context, v, String.class))
					.toList();
			return context.criteriaBuilder().concat(pathString);
		}

		@Override
		public boolean validate(QueryContext context, Transform select) {
			return select.path().length >= 2 && select.attribute().length == 0;
		}
	},
	IS_NULL {
		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
			return context.criteriaBuilder().isNull(QueryUtils.aliasPath(context, select.path()[0].value(),Number.class));
		}

		@Override
		public boolean validate(QueryContext context, Transform select) {
			return select.path().length == 1 && select.attribute().length == 0;
		}
	},
	CUSTOM {
		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
				return customTransform(select).transform(context, select);
		}

		@Override
		public boolean validate(QueryContext context, Transform select) {
			if(select.customFunction().equals(Function.class)) {
				throw new FunctionException(String.format("Specify custom function in %s", select.name()));
			}
			return customTransform(select).validate(context, select);
		}
		
		private QueryFunctionTransform customTransform(Transform select) {
			try {
				return ConstructorUtils.invokeConstructor(select.customFunction());
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException 
					| InstantiationException e) {
				throw new FunctionException(
						String.format("Unable to instance %s class", select.customFunction().getCanonicalName()));
			}
		}
	};
}
