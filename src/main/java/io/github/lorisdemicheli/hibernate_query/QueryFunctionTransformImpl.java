package io.github.lorisdemicheli.hibernate_query;

import io.github.lorisdemicheli.hibernate_query.annotation.Transform;
import io.github.lorisdemicheli.hibernate_query.utils.QueryContext;
import jakarta.persistence.criteria.Selection;

public class QueryFunctionTransformImpl {

	public static final Class<? extends QueryFunctionTransform> NONE = QueryFunctionNone.class;
	
	public class QueryFunctionNone implements QueryFunctionTransform {

		@Override
		public Selection<?> transform(QueryContext context, Transform select) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public enum Test implements QueryFunctionTransform {
		T1 {

			@Override
			public Selection<?> transform(QueryContext context, Transform select) {
				// TODO Auto-generated method stub
				return null;
			}
			
		}
	}
}
