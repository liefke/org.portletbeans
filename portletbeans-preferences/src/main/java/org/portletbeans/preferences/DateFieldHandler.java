package org.portletbeans.preferences;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Handles conversion of simple string fields into preference values.
 *
 * @author Tobias Liefke
 */
@Slf4j
public final class DateFieldHandler implements PreferenceFieldHandler<Date> {

	private static Date parseValue(final String value) throws ParseException {
		try {
			return new Date(Long.parseLong(value));
		} catch (final NumberFormatException e) {
			return FORMAT.parse(value);
		}
	}

	private static final FastDateFormat FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss",
			TimeZone.getTimeZone("GMT"), Locale.ENGLISH);

	@Override
	public Date load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final String value = preferences.getValue(key, defaultValue);
		if (value == null) {
			return null;
		}
		try {
			return parseValue(value);
		} catch (final ParseException e) {
			log.error("Could not parse date object " + key + " from preferences: " + value, e);
			if (!value.equals(defaultValue)) {
				// Try to return the default value
				try {
					return parseValue(defaultValue);
				} catch (final ParseException e2) {
					// Ignore - we already logged an exception
				}
			}
			return null;
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final Date value)
			throws ReadOnlyException {
		preferences.setValue(key, value == null ? null : String.valueOf(value.getTime()));
	}

}