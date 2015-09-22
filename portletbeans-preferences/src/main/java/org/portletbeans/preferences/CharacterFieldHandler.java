package org.portletbeans.preferences;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

/**
 * Handles conversion of character fields into preference values.
 *
 * @author Tobias Liefke
 */
public final class CharacterFieldHandler implements PreferenceFieldHandler<Character> {

	@Override
	public Character load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final String value = preferences.getValue(key, defaultValue);
		if (value == null || value.length() == 0) {
			return null;
		}
		return value.charAt(0);
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final Character value)
			throws ReadOnlyException {
		preferences.setValue(key, value == null ? null : value.toString());
	}

}