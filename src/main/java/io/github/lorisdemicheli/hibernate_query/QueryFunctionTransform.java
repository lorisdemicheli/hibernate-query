package io.github.lorisdemicheli.hibernate_query;

import io.github.lorisdemicheli.hibernate_query.annotation.Transform;
import io.github.lorisdemicheli.hibernate_query.utils.QueryContext;
import jakarta.persistence.criteria.Selection;

public interface QueryFunctionTransform {

	public Selection<?> transform(QueryContext context, Transform select);
	public boolean validate(QueryContext context, Transform select);
}
