package org.portletbeans.liferay.ddm;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.liferay.ddm.DynamicElement.Type;

/**
 * Handles conversion of {@code char} and {@link Character} fields into structure fields.
 *
 * @author Tobias Liefke
 */
public final class CharacterFieldHandler implements StructureFieldHandler<Character> {

	@Override
	public Type getType() {
		return Type.TEXT;
	}

	@Override
	public Character read(final DynamicElement element, final Locale locale, final String defaultValue) {
		final String value = element.getValue(locale, defaultValue);
		return StringUtils.isEmpty(value) ? null : value.charAt(0);
	}

	@Override
	public void write(final DynamicElement element, final Locale locale, final Character value,
			final Map<String, byte[]> images) {
		element.setValue(locale, value == null ? null : String.valueOf(value));
	}

}