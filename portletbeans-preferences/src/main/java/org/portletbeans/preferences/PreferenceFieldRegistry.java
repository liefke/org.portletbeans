package org.portletbeans.preferences;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.portletbeans.util.ClassUtil;
import org.reflections.Reflections;

import lombok.Getter;

/**
 * Manages classes with {@link PreferenceField} annotations.
 *
 * @author Tobias Liefke
 */
public final class PreferenceFieldRegistry {

	/**
	 * Saves the cached metadata for a field annotated with {@link PreferenceField}.
	 *
	 * @param <T>
	 *            the type of the field
	 */
	@Getter
	private static final class PreferenceFieldDescription<T> {

		private static final PreferenceField DEFAULT_PREFERENCE_FIELD = new PreferenceField() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return PreferenceField.class;
			}

			@Override
			public String defaultValue() {
				return null;
			}

			@Override
			@SuppressWarnings("rawtypes")
			public Class<PreferenceFieldHandler> handler() {
				return PreferenceFieldHandler.class;
			}

			@Override
			public String value() {
				return "";
			}
		};

		private final Field field;

		private final String key;

		private final String defaultValue;

		private final PreferenceFieldHandler<T> handler;

		private final boolean isFinal;

		PreferenceFieldDescription(final Field field) {
			this.field = field;
			field.setAccessible(true);
			PreferenceField preferenceField = field.getAnnotation(PreferenceField.class);
			if (preferenceField == null) {
				preferenceField = DEFAULT_PREFERENCE_FIELD;
			}

			this.key = StringUtils.isBlank(preferenceField.value()) ? field.getName() : preferenceField.value();
			this.defaultValue = StringUtils.isBlank(preferenceField.defaultValue()) ? null
					: preferenceField.defaultValue();
			this.handler = findHandler(field, (Class<? extends PreferenceFieldHandler<T>>) preferenceField.handler());
			this.isFinal = Modifier.isFinal(field.getModifiers());
			if (this.isFinal && !(this.handler instanceof FinalFieldHandler)) {
				throw new IllegalArgumentException("Can't handle final field " + field);
			}
		}

		void load(final PortletPreferences preferences, final String prefix, final Object instance) {
			try {
				final String preferenceKey = prefix == null ? this.key : prefix + this.key;
				if (this.isFinal) {
					final T value = (T) this.field.get(instance);
					if (value != null) {
						((FinalFieldHandler<T>) this.handler).load(value, preferences, preferenceKey,
								this.defaultValue);
					}
				} else {
					final T value = this.handler.load(preferences, preferenceKey, this.defaultValue);
					// Dont' set null values for primitives (the default after initialization is usually enough)
					if (value != null || !this.field.getType().isPrimitive()) {
						this.field.set(instance, value);
					}
				}
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}

		void store(final PortletPreferences preferences, final String prefix, final Object instance) {
			try {
				final T value = (T) this.field.get(instance);
				this.handler.store(preferences, prefix == null ? this.key : prefix + this.key, value);
			} catch (final IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			} catch (final ReadOnlyException e) {
				throw new IllegalArgumentException("Could not read store preference for field " + this.field, e);
			}
		}
	}

	private static final Map<Class<?>, List<PreferenceFieldDescription<?>>> PREFERENCE_FIELDS = Collections
			.synchronizedMap(new HashMap<Class<?>, List<PreferenceFieldDescription<?>>>());

	private static final Map<Class<? extends PreferenceFieldHandler<?>>, PreferenceFieldHandler<?>> HANDLERS = Collections
			.synchronizedMap(new HashMap<Class<? extends PreferenceFieldHandler<?>>, PreferenceFieldHandler<?>>());

	private static final Map<Class<?>, Class<? extends PreferenceFieldHandler<?>>> DEFAULT_HANDLERS = Collections
			.unmodifiableMap(createDefaultHandlers());

	private static Map<Class<?>, Class<? extends PreferenceFieldHandler<?>>> createDefaultHandlers() {
		final Map<Class<?>, Class<? extends PreferenceFieldHandler<?>>> result = new HashMap<>();
		final Set<Class<? extends PreferenceFieldHandler<?>>> handlerTypes = new Reflections("")
				.getSubTypesOf((Class<PreferenceFieldHandler<?>>) (Class<?>) PreferenceFieldHandler.class);
		for (final Class<? extends PreferenceFieldHandler<?>> handlerType : handlerTypes) {
			if (!Modifier.isAbstract(handlerType.getModifiers())) {
				final Class<Object> type = ClassUtil.getActualTypeBinding(handlerType, PreferenceFieldHandler.class, 0);
				if (type != null && type != Object.class) {
					result.put(type, handlerType);
				}
			}
		}
		return result;
	}

	/**
	 * Resolves the default field handler for the given field or type.
	 *
	 * @param field
	 *            the instpected field, null to ignore the field
	 * @param type
	 *            the type of the handled values
	 * @return the defualt handler
	 * @throws IllegalArgumentException
	 *             if we don't know how to handle the given field
	 */
	static <T, E extends Enum<E>> PreferenceFieldHandler<T> findDefaultHandler(final Field field, final Class<T> type) {
		// Inspect the field
		final Class<? extends PreferenceFieldHandler<?>> defaultHandler = DEFAULT_HANDLERS.get(type);
		if (defaultHandler != null) {
			return (PreferenceFieldHandler<T>) findExplicitHandler(field, defaultHandler);
		} else if (type.isAnnotationPresent(PreferenceEntity.class)) {
			return new PreferenceEntityHandler<>(type);
		} else if (field != null && Collection.class.isAssignableFrom(type)) {
			return (PreferenceFieldHandler<T>) new CollectionFieldHandler<>(field);
		} else if (Enum.class.isAssignableFrom(type)) {
			return (PreferenceFieldHandler<T>) new EnumFieldHandler<>((Class<E>) type);
		} else if (char.class.isAssignableFrom(type)) {
			return (PreferenceFieldHandler<T>) new CharacterFieldHandler();
		} else {
			try {
				return new ToStringFieldHandler<>(
						(Constructor<T>) ClassUtils.primitiveToWrapper(type).getConstructor(String.class));
			} catch (final NoSuchMethodException e) {
				if (Serializable.class.isAssignableFrom(Serializable.class)) {
					return (PreferenceFieldHandler<T>) findExplicitHandler(field, SerializableFieldHandler.class);
				}
			}
		}
		throw new IllegalArgumentException("Can't handle " + type + (field == null ? "" : " for field " + field));
	}

	private static <T> PreferenceFieldHandler<T> findExplicitHandler(final Field field,
			final Class<? extends PreferenceFieldHandler<?>> handlerClass) {
		PreferenceFieldHandler<?> handler = HANDLERS.get(handlerClass);
		if (handler == null) {
			try {
				if (field != null) {
					try {
						final Constructor<? extends PreferenceFieldHandler<?>> constructor = handlerClass
								.getConstructor(Field.class);
						handler = constructor.newInstance(field);
					} catch (final NoSuchMethodException e) {
						// Ignore and try to use the default
					}
				}
				if (handler == null) {
					handler = handlerClass.newInstance();
					HANDLERS.put(handlerClass, handler);
				}
			} catch (final InvocationTargetException | InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException("Can't instantiate handler: " + handlerClass, e);
			}
		}
		return (PreferenceFieldHandler<T>) handler;
	}

	/**
	 * Resolves the handler for the given class.
	 *
	 * If such an handler was not created up to now, a new one is created.
	 *
	 * @param field
	 *            the current field
	 *
	 * @param handlerClass
	 *            the class of the handler, {@link PreferenceFieldHandler} itself indicates to resolve the type from the
	 *            field
	 * @return the handler
	 */
	static <T> PreferenceFieldHandler<T> findHandler(final Field field,
			final Class<? extends PreferenceFieldHandler<?>> handlerClass) {
		if ((Class<?>) handlerClass == PreferenceFieldHandler.class) {
			final Class<T> type = (Class<T>) field.getType();
			return findDefaultHandler(field, type);
		}
		return findExplicitHandler(field, handlerClass);
	}

	/**
	 * Resolves all declared preference fields for the given class.
	 *
	 * @param c
	 *            the inspected class
	 * @return the annotated fields of this class and all superclasses
	 */
	private static List<PreferenceFieldDescription<?>> getPreferenceFields(final Class<?> c) {
		if (c == Object.class) {
			return Collections.emptyList();
		}
		List<PreferenceFieldDescription<?>> fields = PREFERENCE_FIELDS.get(c);
		if (fields == null) {
			fields = getPreferenceFields(c.getSuperclass());
			final boolean isPreferenceEntity = c.isAnnotationPresent(PreferenceEntity.class);
			boolean modified = false;
			for (final Field field : c.getDeclaredFields()) {
				if (isPreferenceField(field, isPreferenceEntity)) {
					if (!modified) {
						fields = new ArrayList<>(fields);
						modified = true;
					}

					fields.add(new PreferenceFieldDescription<>(field));
				}
			}
			PREFERENCE_FIELDS.put(c, fields);
		}
		return fields;
	}

	private static boolean isPreferenceField(final Field field, final boolean isPreferenceEntity) {
		final int modifiers = field.getModifiers();
		if (Modifier.isStatic(modifiers)) {
			return false;
		}
		if (field.isAnnotationPresent(PreferenceField.class)) {
			return true;
		}
		return isPreferenceEntity && !Modifier.isTransient(modifiers) && !field.isAnnotationPresent(Transient.class);
	}

	/**
	 * Loads initial values for an object.
	 *
	 * @param preferences
	 *            the current portlet preferences
	 * @param prefix
	 *            the prefix for all used keys - useful if a collection of elements was stored
	 * @param instance
	 *            the current instance to initialize
	 * @return the loaded instance (for chaining purposes)
	 */
	public static <T> T load(final PortletPreferences preferences, final String prefix, final T instance) {
		for (final PreferenceFieldDescription<?> field : getPreferenceFields(instance.getClass())) {
			field.load(preferences, prefix, instance);
		}
		return instance;
	}

	/**
	 * Loads initial values for an object.
	 *
	 * @param preferences
	 *            the current portlet preferences
	 * @param instance
	 *            the current instance to initialize
	 * @return the loaded instance (for chaining purposes)
	 */
	public static <T> T load(final PortletPreferences preferences, final T instance) {
		return load(preferences, null, instance);
	}

	/**
	 * Stores all annotated fields of an object to the preferences.
	 *
	 * This method does not invoke {@link PortletPreferences#store()}, that should be done by the caller.
	 *
	 * @param preferences
	 *            the current portlet preferences
	 * @param instance
	 *            the current instance to store
	 */
	public static void store(final PortletPreferences preferences, final Object instance) {
		store(preferences, null, instance);
	}

	/**
	 * Stores all annotated fields of an object to the preferences.
	 *
	 * This method does not invoke {@link PortletPreferences#store()}, that should be done by the caller.
	 *
	 * @param preferences
	 *            the current portlet preferences
	 * @param prefix
	 *            the prefix for all used keys - useful if a collection of elements has to be stored
	 * @param instance
	 *            the current instance to store
	 */
	public static void store(final PortletPreferences preferences, final String prefix, final Object instance) {
		for (final PreferenceFieldDescription<?> field : getPreferenceFields(instance.getClass())) {
			field.store(preferences, prefix, instance);
		}
	}

	/**
	 * Prevent creation.
	 */
	private PreferenceFieldRegistry() {
		throw new AssertionError("Utility class");
	}
}
