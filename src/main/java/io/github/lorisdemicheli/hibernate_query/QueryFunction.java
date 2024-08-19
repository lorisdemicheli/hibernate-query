package io.github.lorisdemicheli.hibernate_query;

@FunctionalInterface
public interface QueryFunction<R, C, S> {
    R apply(C c, S s);
}
