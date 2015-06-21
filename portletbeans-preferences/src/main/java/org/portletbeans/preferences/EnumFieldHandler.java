package org.portletbeans.preferences;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang.StringUtils;

/**
 * Handles the conversion of enum fields to / from preference fields.
 *
 * @author Tobias Liefke
 * @param <E>
 *            the type of the enum
 */
@RequiredArgsConstructor
public class EnumFieldHandler<E extends Enum<E>> implements PreferenceFieldHandler<E> {

	private final Class<E> enumClass;

	@Override
	public E load(final PortletPreferences preferences, final String key, final String defaultValue) {
		try {
			final String name = preferences.getValue(key, defaultValue);
			return StringUtils.isEmpty(name) ? null : Enum.valueOf(this.enumClass, name);
		} catch (final IllegalArgumentException e) {
			// A previous element does not exist anymore
			return StringUtils.isEmpty(defaultValue) ? null : Enum.valueOf(this.enumClass, defaultValue);
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final E value) throws ReadOnlyException {
		preferences.setValue(key, value == null ? null : value.name());
	}

}
