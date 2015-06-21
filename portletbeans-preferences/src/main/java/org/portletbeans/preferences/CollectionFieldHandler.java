package org.portletbeans.preferences;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;

/**
 * Handles the conversion of collections fields to / from preference fields.
 *
 * @author Tobias Liefke
 * @param <E>
 *            the type of the elements of the collection
 */
public class CollectionFieldHandler<E> implements FinalFieldHandler<Collection<E>> {

	private static final Map<Class<?>, Class<?>> COLLECTION_CLASSES = ImmutableMap.<Class<?>, Class<?>> builder().//
			put(Collection.class, ArrayList.class).//
			put(List.class, ArrayList.class).//
			put(Set.class, LinkedHashSet.class).//
			put(SortedSet.class, TreeSet.class).//
			build();

	private final Class<Collection<E>> collectionClass;

	private final PreferenceFieldHandler<E> elementHandler;

	/**
	 * Constructs a CollectionFieldHandler for the given field.
	 *
	 * @param field
	 *            the field to handle
	 */
	public CollectionFieldHandler(final Field field) {
		this.collectionClass = (Class<Collection<E>>) COLLECTION_CLASSES.get(field.getType());
		if (this.collectionClass == null) {
			throw new IllegalArgumentException("Can't handle collection field: " + field);
		}
		final Type type = field.getGenericType();
		if (!(type instanceof ParameterizedType)) {
			throw new IllegalArgumentException("Collection field must specify the element type: " + field);
		}
		final ParameterizedType parameterizedType = (ParameterizedType) type;
		if (parameterizedType.getActualTypeArguments().length != 1) {
			throw new IllegalArgumentException("Collection field must specify exactly one element type: " + field);
		}
		if (!(parameterizedType.getActualTypeArguments()[0] instanceof Class)) {
			throw new IllegalArgumentException("Needs an explicit element type: " + field);
		}
		final Class<E> elementClass = (Class<E>) parameterizedType.getActualTypeArguments()[0];
		this.elementHandler = PreferenceFieldRegistry.findDefaultHandler(null, elementClass);
	}

	@Override
	public void load(final Collection<E> currentValue, final PortletPreferences preferences, final String key,
			final String defaultValue) {
		try {
			currentValue.clear();
			final int length = Integer.parseInt(preferences.getValue(key + ".length",
					StringUtils.isEmpty(defaultValue) ? "0" : defaultValue));
			for (int i = 0; i < length; i++) {
				currentValue.add(this.elementHandler.load(preferences, key + '.' + i, ""));
			}
		} catch (final NumberFormatException e) {
			// Ignore and return the empty result
		}
	}

	@Override
	public Collection<E> load(final PortletPreferences preferences, final String key, final String defaultValue) {
		try {
			final Collection<E> result = this.collectionClass.newInstance();
			load(result, preferences, key, defaultValue);
			return result;
		} catch (final InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Can't instantiate collection class " + this.collectionClass + ": " + e,
					e);
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final Collection<E> values)
			throws ReadOnlyException {
		int index = 0;
		if (values != null) {
			for (final E element : values) {
				this.elementHandler.store(preferences, key + '.' + index, element);
				index++;
			}
		}
		preferences.setValue(key + ".length", String.valueOf(index));
	}

}
