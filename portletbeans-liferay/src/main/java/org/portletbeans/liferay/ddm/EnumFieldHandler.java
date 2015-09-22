package org.portletbeans.liferay.ddm;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.liferay.ddm.DynamicElement.Type;

import lombok.RequiredArgsConstructor;

/**
 * Handles the conversion of enum fields to / from structure fields.
 *
 * @author Tobias Liefke
 * @param <E>
 *            the type of the enum
 */
@RequiredArgsConstructor
public class EnumFieldHandler<E extends Enum<E>> implements StructureFieldHandler<E> {

	private final Class<E> enumClass;

	@Override
	public Type getType() {
		return Type.TEXT;
	}

	@Override
	public E read(final DynamicElement element, final Locale locale, final String defaultValue) {
		final String value = element.getValue(locale, "");
		if (StringUtils.isNotEmpty(value)) {
			try {
				return Enum.valueOf(this.enumClass, value);
			} catch (final IllegalArgumentException e) {
				// Ignore and use the default
			}
		}

		if (StringUtils.isNotEmpty(defaultValue)) {
			try {
				return Enum.valueOf(this.enumClass, defaultValue);
			} catch (final IllegalArgumentException e) {
				// Ignore and use null
			}
		}
		return null;
	}

	@Override
	public void write(final DynamicElement element, final Locale locale, final E value,
			final Map<String, byte[]> images) {
		element.setValue(locale, value == null ? "" : value.name());
	}

}
