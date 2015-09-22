package org.portletbeans.liferay.ddm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.portletbeans.liferay.ddm.DynamicElement.Type;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles the conversion of fields that have a string constructor into preference values.
 *
 * @author Tobias Liefke
 * @param <T>
 *            The type of the field
 */
@Slf4j
public final class ToStringFieldHandler<T> implements StructureFieldHandler<T> {

	private static final Map<Class<?>, Type> TYPES = new HashMap<>();

	static {
		TYPES.put(Byte.class, Type.DDM_INTEGER);
		TYPES.put(Short.class, Type.DDM_INTEGER);
		TYPES.put(Integer.class, Type.DDM_INTEGER);
		TYPES.put(Long.class, Type.DDM_INTEGER);
		TYPES.put(Float.class, Type.DDM_NUMBER);
		TYPES.put(Double.class, Type.DDM_NUMBER);
		TYPES.put(Number.class, Type.DDM_NUMBER);
		TYPES.put(Boolean.class, Type.BOOLEAN);
	}

	private final Constructor<T> constructor;

	@Getter
	private final Type type;

	/**
	 * Creates a new instance of this handler.
	 *
	 * @param type
	 *            the class of the associated field
	 * @throws NoSuchMethodException
	 *             if the string constructor for the given type was not found
	 */
	public ToStringFieldHandler(final Class<T> type) throws NoSuchMethodException {
		Class<T> wrapper = ClassUtils.primitiveToWrapper(type);
		this.constructor = wrapper.getConstructor(String.class);
		Type elementType = null;
		while (wrapper != null && elementType == null) {
			elementType = TYPES.get(wrapper);
			wrapper = (Class<T>) wrapper.getSuperclass();
		}
		this.type = elementType == null ? Type.TEXT : elementType;
	}

	@Override
	public T read(final DynamicElement element, final Locale locale, final String defaultValue) {
		final String value = element.getValue(locale, defaultValue);
		if (StringUtils.isEmpty(value) || "null".equals(value)) {
			return null;
		}
		try {
			return this.constructor.newInstance(value);
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error("Could not convert the field value to a " + this.constructor.getDeclaringClass() + ": " + value,
					e);
			return null;
		}
	}

	@Override
	public void write(final DynamicElement element, final Locale locale, final T value,
			final Map<String, byte[]> images) {
		element.setValue(locale, value == null ? "" : value.toString());
	}

}