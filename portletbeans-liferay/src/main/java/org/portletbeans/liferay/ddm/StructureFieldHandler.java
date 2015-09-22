package org.portletbeans.liferay.ddm;

import java.util.Locale;
import java.util.Map;

import org.portletbeans.liferay.ddm.DynamicElement.Type;

/**
 * Handles the read and write of a {@link StructureField}.
 *
 * @author Tobias Liefke
 * @param <T>
 *            the type of the handled field
 */
public interface StructureFieldHandler<T> {

	/**
	 * Loads an object from a structure element.
	 *
	 * @param element
	 *            the element to load
	 * @param locale
	 *            the locale of the current user
	 * @param defaultValue
	 *            the default value, as given in the annotation
	 * @return the loaded value or {@code null} if none was stored
	 */
	T read(final DynamicElement element, final Locale locale, final String defaultValue);

	/**
	 * Saves an object to a structure element.
	 *
	 * @param element
	 *            the target element
	 * @param locale
	 *            the locale of the current user
	 * @param value
	 *            the value or {@code null} if none was set for the field
	 * @param images
	 *            the mapping from a qualified element name to image data for storing images
	 */
	void write(final DynamicElement element, final Locale locale, final T value, final Map<String, byte[]> images);

	/**
	 * The type for new dynamic elements and structure definitions.
	 *
	 * @return the type as defined in Liferay, e.g. {@link Type#TEXT}
	 */
	Type getType();

}
