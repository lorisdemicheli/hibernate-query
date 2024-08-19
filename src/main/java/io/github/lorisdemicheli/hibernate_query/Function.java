package io.github.lorisdemicheli.hibernate_query;


import java.lang.reflect.InvocationTargetException;

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
			return QueryUtils.aliasPath(context, ""); //select.path()[0].path()
		}
	},
	CUSTOM {
		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
			if(select.customFunction().equals(Function.class)) {
				throw new FunctionException(String.format("Specify custom function in %s", select.name()));
			} else {
				try {
					QueryFunctionTransform customTransform = ConstructorUtils.invokeConstructor(select.customFunction());
					return customTransform.transform(context, select);
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
					throw new FunctionException(String.format("Unable to instance %s class", select.customFunction().getCanonicalName()));
				}
			}
		}
	};
}
