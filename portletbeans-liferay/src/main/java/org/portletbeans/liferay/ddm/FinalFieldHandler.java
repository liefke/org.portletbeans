package org.portletbeans.liferay.ddm;

import java.util.Locale;

/**
 * Handles the conversion of a {@link StructureField}, if it is final.
 *
 * @author Tobias Liefke
 * @param <T>
 *            the type of the handled field
 */
public interface FinalFieldHandler<T> extends StructureFieldHandler<T> {

	/**
	 * Fills a final field from a structure.
	 *
	 * @param currentValue
	 *            the value to fill
	 * @param element
	 *            the element to read
	 * @param locale
	 *            the locale of the current user
	 * @param defaultValue
	 *            the default value, as given in the annotation
	 */
	void read(final T currentValue, final DynamicElement element, final Locale locale, final String defaultValue);

}
