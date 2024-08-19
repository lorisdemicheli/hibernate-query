package io.github.lorisdemicheli.hibernate_query.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.lorisdemicheli.hibernate_query.Expression;

/**
 * 
 * @author Loris Demicheli
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Filter {
	String name() default "";
	String path() default "";
	Expression expression() default Expression.EQUAL;
	boolean negate() default false;
	boolean emptyOrNullExclude() default false;
	String disjunction() default "";
}