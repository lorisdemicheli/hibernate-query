package io.github.lorisdemicheli.hibernate_query.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author Loris Demicheli
 */
@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(Fetch.List.class)
public @interface Fetch {
	
	String path();

	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface List {
		Fetch[] value();
	}
}
