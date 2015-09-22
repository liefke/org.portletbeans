package org.portletbeans.liferay.ddm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.liferay.ddm.DynamicElement.Type;

/**
 * Handles conversion for a structured field of type {@link Date}.
 *
 * @author Tobias Liefke
 */
public class DateFieldHandler implements FinalFieldHandler<Date> {

	@Override
	public Type getType() {
		return Type.DDM_DATE;
	}

	@Override
	public void read(final Date currentValue, final DynamicElement element, final Locale locale,
			final String defaultValue) {
		final Date read = read(element, locale, defaultValue);
		if (read != null) {
			currentValue.setTime(read.getTime());
		}
	}

	@Override
	public Date read(final DynamicElement element, final Locale locale, final String defaultValue) {
		final String value = element.getValue(locale, "");
		if (StringUtils.isNotEmpty(value)) {
			try {
				return new Date(Long.parseLong(value));
			} catch (final NumberFormatException e) {
				// Ignore and use the default
			}
		}
		if (StringUtils.isNotEmpty(defaultValue)) {
			if (defaultValue.matches("-?\\d+")) {
				return new Date(Long.valueOf(defaultValue));
			}
			final SimpleDateFormat format;
			if (defaultValue.matches("\\d+-\\d+-\\d+")) {
				// Just a date
				format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
			} else if (defaultValue.matches("\\d+-\\d+-\\d+T\\d+:\\d+:\\d+")) {
				// A date with a time in the system time zone
				format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
			} else {
				// A date with a time in a custom time zone
				format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.ENGLISH);
			}
			try {
				return format.parse(defaultValue);
			} catch (final ParseException e) {
				// Ignore and return null
			}
		}
		return null;
	}

	@Override
	public void write(final DynamicElement element, final Locale locale, final Date value,
			final Map<String, byte[]> images) {
		element.setValue(locale, value == null ? null : String.valueOf(value.getTime()));
	}

}
