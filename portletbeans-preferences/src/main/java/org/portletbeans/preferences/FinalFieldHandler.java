package org.portletbeans.preferences;

import javax.portlet.PortletPreferences;

/**
 * Handles the load and store of a {@link PreferenceField}, if it is final.
 *
 * @author Tobias Liefke
 * @param <T>
 *            the type of the handled field
 */
public interface FinalFieldHandler<T> extends PreferenceFieldHandler<T> {

	/**
	 * Loads an object from the preferences.
	 *
	 * @param currentValue
	 *            the current value to fill
	 * @param preferences
	 *            the current preferences
	 * @param key
	 *            the key for the field
	 * @param defaultValue
	 *            the default value given in the annotation
	 */
	void load(final T currentValue, final PortletPreferences preferences, final String key, final String defaultValue);

}
