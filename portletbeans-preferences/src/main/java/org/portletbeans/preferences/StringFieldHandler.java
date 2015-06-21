package org.portletbeans.preferences;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

/**
 * Handles conversion of simple string fields into preference values.
 *
 * @author Tobias Liefke
 */
public final class StringFieldHandler implements PreferenceFieldHandler<String> {

	@Override
	public String load(final PortletPreferences preferences, final String key, final String defaultValue) {
		return preferences.getValue(key, defaultValue);
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final String value)
			throws ReadOnlyException {
		preferences.setValue(key, value);
	}

}