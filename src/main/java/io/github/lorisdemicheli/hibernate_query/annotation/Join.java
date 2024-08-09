package io.github.lorisdemicheli.hibernate_query.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.persistence.criteria.JoinType;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Join {
	Alias alias();

	String path();

	JoinType type() default JoinType.LEFT;

	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface List {
		Join[] joins();
	}
}
