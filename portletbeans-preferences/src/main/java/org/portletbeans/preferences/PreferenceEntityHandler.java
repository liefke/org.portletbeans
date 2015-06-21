package org.portletbeans.preferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

/**
 * Handles the conversion of complex objects with own preference fields to / from preferences.
 *
 * @author Tobias Liefke
 * @param <E>
 *            the type of the handled field
 */
public class PreferenceEntityHandler<E> implements FinalFieldHandler<E> {

	private final Class<E> type;

	private final List<Method> postConstructMethods = new LinkedList<>();

	/**
	 * Creates a new instance of PreferenceEntityHandler.
	 *
	 * @param type
	 *            the type of the handled entity
	 */
	public PreferenceEntityHandler(final Class<E> type) {
		this.type = type;
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			for (final Method method : c.getDeclaredMethods()) {
				if (method.getAnnotation(PostConstruct.class) != null) {
					this.postConstructMethods.add(0, method);
				}
			}
		}
	}

	@Override
	public void load(final E currentValue, final PortletPreferences preferences, final String key,
			final String defaultValue) {
		if (preferences.getValue(key, "") != null) {
			PreferenceFieldRegistry.load(preferences, key + '.', currentValue);
		}
	}

	@Override
	public E load(final PortletPreferences preferences, final String key, final String defaultValue) {
		if (preferences.getValue(key, "") == null) {
			return null;
		}
		try {
			final E value = this.type.newInstance();
			PreferenceFieldRegistry.load(preferences, key + '.', value);
			for (final Method postConstruct : this.postConstructMethods) {
				postConstruct.setAccessible(true);
				postConstruct.invoke(value);
			}
			return value;
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new IllegalArgumentException("Can't instantiate " + this.type, e);
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final E value) throws ReadOnlyException {
		if (value == null) {
			preferences.setValue(key, null);
		} else {
			preferences.reset(key);
			PreferenceFieldRegistry.store(preferences, key + '.', value);
		}
	}

}
