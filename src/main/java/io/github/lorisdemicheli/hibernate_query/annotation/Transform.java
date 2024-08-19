package io.github.lorisdemicheli.hibernate_query.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.lorisdemicheli.hibernate_query.Function;
import io.github.lorisdemicheli.hibernate_query.QueryFunctionTransform;

/**
 * 
 * @author Loris Demicheli
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Transform {
	String name();
	Function function() default Function.PATH;
	Class<? extends QueryFunctionTransform> customFunction() default Function.class;
}
