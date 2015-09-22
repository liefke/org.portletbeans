package org.portletbeans.liferay.ddm;

import java.util.Locale;
import java.util.Map;

import org.portletbeans.liferay.ddm.DynamicElement.Type;

/**
 * Handles conversion of simple string fields into structure fields.
 *
 * Only for performance reasons, as {@link ToStringFieldHandler} would work as well.
 *
 * @author Tobias Liefke
 */
public final class StringFieldHandler implements StructureFieldHandler<String> {

	@Override
	public Type getType() {
		return Type.TEXT;
	}

	@Override
	public String read(final DynamicElement element, final Locale locale, final String defaultValue) {
		return element.getValue(locale, defaultValue);
	}

	@Override
	public void write(final DynamicElement element, final Locale locale, final String value,
			final Map<String, byte[]> images) {
		element.setValue(locale, value);
	}

}