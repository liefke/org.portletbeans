package org.portletbeans.preferences;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

/**
 * Handles the load and store of a {@link PreferenceField}.
 *
 * @author Tobias Liefke
 * @param <T>
 *            the type of the handled field
 */
public interface PreferenceFieldHandler<T> {

	/**
	 * Loads an object from the preferences.
	 *
	 * @param preferences
	 *            the current preferences
	 * @param key
	 *            the key for the field
	 * @param defaultValue
	 *            the default value given in the annotation
	 * @return the loaded value or {@code null} if none was stored
	 */
	T load(final PortletPreferences preferences, final String key, final String defaultValue);

	/**
	 * Saves an object to the preferences.
	 *
	 * The handler doesn't need to call {@link PortletPreferences#store()}, this is done by the caller.
	 *
	 * @param preferences
	 *            the current preferences
	 * @param key
	 *            the key for the field
	 * @param value
	 *            the value or {@code null} if none was selected
	 * @throws ReadOnlyException
	 *             if the preference is readonly
	 */
	void store(final PortletPreferences preferences, final String key, final T value) throws ReadOnlyException;

}
