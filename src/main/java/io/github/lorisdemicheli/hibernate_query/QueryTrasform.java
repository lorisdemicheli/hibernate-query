package io.github.lorisdemicheli.hibernate_query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import io.github.lorisdemicheli.hibernate_query.annotation.Transform;
import io.github.lorisdemicheli.hibernate_query.exception.TransformException;
import jakarta.persistence.Tuple;

public interface QueryTrasform<R> {
	public Class<R> getTrasformClass();

	default R transformSingle(Tuple tuple) {
		R instance = null;
		try {
			instance = ConstructorUtils.invokeConstructor(getTrasformClass());
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
				| InstantiationException e) {
			throw new TransformException(e);
		}
		for(Field field : FieldUtils.getFieldsListWithAnnotation(getTrasformClass(), Transform.class)) {
			Transform transform = field.getAnnotation(Transform.class);
			String name = transform.name() == null ? field.getName() : transform.name();
			try {
				FieldUtils.writeField(field, instance, tuple.get(name), false);
			} catch (IllegalAccessException e) {
				throw new TransformException(e);
			}
		}
		return instance;
	}

	default List<R> transformList(List<Tuple> tuple) {
		return tuple.stream().map(t -> transformSingle(t)).toList();
	}
}
