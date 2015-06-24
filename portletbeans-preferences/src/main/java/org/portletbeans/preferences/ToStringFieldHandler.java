package org.portletbeans.preferences;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles the conversion of fields that have a string constructor into preference values.
 *
 * @author Tobias Liefke
 * @param <T>
 *            The type of the field
 */
@RequiredArgsConstructor
@Slf4j
public final class ToStringFieldHandler<T> implements PreferenceFieldHandler<T> {

	private final Constructor<T> constructor;

	@Override
	public T load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final String value = preferences.getValue(key, defaultValue);
		if (value == null) {
			return null;
		}
		try {
			return this.constructor.newInstance(value);
		} catch (final InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			log.error(
					"Could not convert the preference value for " + key + " to a "
							+ this.constructor.getDeclaringClass() + ": " + value, e);
			return null;
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final T value) throws ReadOnlyException {
		preferences.setValue(key, value == null ? null : value.toString());
	}

}