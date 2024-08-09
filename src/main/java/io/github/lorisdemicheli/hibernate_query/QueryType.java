package io.github.lorisdemicheli.hibernate_query;

public interface QueryType<T> {
	public Class<T> getType();
}