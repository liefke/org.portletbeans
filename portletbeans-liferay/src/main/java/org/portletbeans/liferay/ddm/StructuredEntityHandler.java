package org.portletbeans.liferay.ddm;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.liferay.ddm.DynamicElement.Type;

/**
 * Handles the conversion of complex objects with own preference fields to / from preferences.
 *
 * @author Tobias Liefke
 * @param <E>
 *            the type of the handled field
 */
public class StructuredEntityHandler<E> implements FinalFieldHandler<E> {

	private final Class<E> type;

	private final Type primaryType;

	/**
	 * Creates a new instance of a handler for a specific type.
	 *
	 * @param type
	 *            the type of the handled entity
	 * @param primaryType
	 *            the type of the {@link StructureField primary structure field}
	 */
	public StructuredEntityHandler(final Class<E> type, final Type primaryType) {
		this.type = type;
		this.primaryType = primaryType;
	}

	@Override
	public Type getType() {
		return this.primaryType;
	}

	@Override
	public E read(final DynamicElement element, final Locale locale, final String defaultValue) {
		if (StringUtils.isEmpty(element.getValue(locale, defaultValue))) {
			return null;
		}
		return StructureFieldRegistry.createEntity(this.type, element, locale);
	}

	@Override
	public void read(final E currentValue, final DynamicElement element, final Locale locale,
			final String defaultValue) {
		StructureFieldRegistry.read(element, locale, currentValue);
	}

	@Override
	public void write(final DynamicElement element, final Locale locale, final E value,
			final Map<String, byte[]> images) {
		if (value == null) {
			element.setValue(locale, "");
			element.clear();
		} else {
			StructureFieldRegistry.write(value, element, locale, images);
		}
	}

}
