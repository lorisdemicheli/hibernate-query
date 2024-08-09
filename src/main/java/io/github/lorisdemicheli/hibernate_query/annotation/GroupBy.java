package io.github.lorisdemicheli.hibernate_query.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author Loris Demicheli
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface GroupBy {
	String value();
	
	@Target(TYPE)
	@Retention(RUNTIME)
	@interface List {
		GroupBy[] groupsBy();
	}
}