package org.portletbeans.liferay.ddm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.portletbeans.liferay.ddm.DynamicElement.Type;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Contains a list of {@link DynamicElement}s.
 *
 * @author Tobias Liefke
 */
public abstract class DynamicElementsContainer implements Iterable<DynamicElement> {

	private final List<DynamicElement> elements = new ArrayList<>();

	private final ListMultimap<String, DynamicElement> elementNames = ArrayListMultimap.create();

	/**
	 * Adds a boolean element.
	 *
	 * @param name
	 *            the name of the element
	 * @param value
	 *            the value for the default locale
	 * @return the created element
	 */
	public DynamicElement add(final String name, final boolean value) {
		return addElement(name, Type.BOOLEAN, String.valueOf(value));
	}

	/**
	 * Adds a date element.
	 *
	 * @param name
	 *            the name of the element
	 * @param value
	 *            the value for the default locale
	 * @return the created element
	 */
	public DynamicElement add(final String name, final Date value) {
		return addElement(name, Type.DDM_DATE, value == null ? "" : String.valueOf(value.getTime()));
	}

	/**
	 * Adds a number element.
	 *
	 * @param name
	 *            the name of the element
	 * @param value
	 *            the value for the default locale
	 * @return the created element
	 */
	public DynamicElement add(final String name, final double value) {
		return addElement(name, Type.DDM_NUMBER, String.valueOf(value));
	}

	/**
	 * Adds an integer element.
	 *
	 * @param name
	 *            the name of the element
	 * @param value
	 *            the value for the default locale
	 * @return the created element
	 */
	public DynamicElement add(final String name, final long value) {
		return addElement(name, Type.DDM_INTEGER, String.valueOf(value));
	}

	/**
	 * Adds a string element.
	 *
	 * @param name
	 *            the name of the element
	 * @param value
	 *            the value for the default locale
	 * @return the created element
	 */
	public DynamicElement add(final String name, final String value) {
		return addElement(name, Type.TEXT, value == null ? "" : value);
	}

	/**
	 * Adds an empty element to the list of elements.
	 *
	 * @param name
	 *            the name of the element
	 * @param type
	 *            the type of the element
	 * @return the created element
	 */
	public DynamicElement addElement(final String name, final String type) {
		final DynamicElement element = new DynamicElement(this, name, type);
		final List<DynamicElement> namedElements = this.elementNames.get(name);
		if (namedElements.isEmpty()) {
			this.elements.add(element);
		} else {
			// Add after the last element with same name
			final DynamicElement lastElement = namedElements.get(namedElements.size() - 1);
			int index = this.elements.size() - 1;
			while (index >= 0 && this.elements.get(index) != lastElement) {
				index--;
			}
			this.elements.add(index + 1, element);
		}
		namedElements.add(element);
		return element;
	}

	/**
	 * Adds an empty element to the list of elements.
	 *
	 * @param name
	 *            the name of the element
	 * @param type
	 *            the type of the element
	 * @return the created element
	 */
	public DynamicElement addElement(final String name, final Type type) {
		return addElement(name, type.getId());
	}

	/**
	 * Adds a element with one value in the {@link #getDefaultLocale() default locale} to the list of elements.
	 *
	 * @param name
	 *            the name of the element
	 * @param type
	 *            the type of the element
	 * @param value
	 *            the value for the default locale
	 * @return the created element
	 */
	public DynamicElement addElement(final String name, final Type type, final String value) {
		final DynamicElement element = addElement(name, type);
		element.getValues().put(getDefaultLocale(), value);
		return element;
	}

	/**
	 * Removes all elements from this container.
	 */
	public void clear() {
		this.elements.clear();
		this.elementNames.clear();
	}

	/**
	 * The default locale of the associated {@link DynamicElements}.
	 *
	 * @return the default locale for all elements
	 */
	public abstract Locale getDefaultLocale();

	/**
	 * Finds all elements with the given name.
	 *
	 * @param name
	 *            the name of the elements.
	 * @return all elements with that name
	 */
	public List<DynamicElement> getElements(final String name) {
		return Collections.unmodifiableList(this.elementNames.get(name));
	}

	/**
	 * Finds the first value of the given element in the given locale.
	 *
	 * @param name
	 *            the name of the element
	 * @param locale
	 *            the locale of the value
	 * @return the value of the element with the given name or {@code null} if no such element exists
	 */
	public String getValue(final String name, final Locale locale) {
		return getValue(name, locale, (String) null);
	}

	/**
	 * Finds the boolean value for the given element in the given locale.
	 *
	 * @param elementName
	 *            the name of the element
	 * @param locale
	 *            the locale of value
	 * @param defaultValue
	 *            the value to return, if no such value exists
	 * @return the found value
	 */
	public boolean getValue(final String elementName, final Locale locale, final boolean defaultValue) {
		final List<DynamicElement> namedElements = getElements(elementName);
		if (namedElements != null && !namedElements.isEmpty()) {
			return namedElements.get(0).getValue(locale, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Finds an enum value for the given element in the given locale.
	 *
	 * @param elementName
	 *            the name of the element
	 * @param locale
	 *            the locale of value
	 * @param enumClass
	 *            the class of the enums to return
	 * @return the found value
	 */
	public <E extends Enum<E>> E getValue(final String elementName, final Locale locale, final Class<E> enumClass) {
		final List<DynamicElement> namedElements = getElements(elementName);
		if (namedElements != null && !namedElements.isEmpty()) {
			return namedElements.get(0).getValue(locale, enumClass);
		}
		try {
			return ((E[]) enumClass.getMethod("values").invoke(null))[0];
		} catch (final ReflectiveOperationException | IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Not an valid enum class " + enumClass, e);
		}
	}

	/**
	 * Finds the date in the given element and locale.
	 *
	 * @param elementName
	 *            the name of the element
	 * @param locale
	 *            the locale of value
	 * @param defaultValue
	 *            the value to return, if no such value exists
	 * @return the found value
	 */
	public Date getValue(final String elementName, final Locale locale, final Date defaultValue) {
		final List<DynamicElement> namedElements = getElements(elementName);
		if (namedElements != null && !namedElements.isEmpty()) {
			return namedElements.get(0).getValue(locale, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Finds the numeric value for the given element in the given locale.
	 *
	 * @param elementName
	 *            the name of the element
	 * @param locale
	 *            the locale of value
	 * @param defaultValue
	 *            the value to return, if no such value exists
	 * @return the found value
	 */
	public Number getValue(final String elementName, final Locale locale, final Number defaultValue) {
		final List<DynamicElement> namedElements = getElements(elementName);
		if (namedElements != null && !namedElements.isEmpty()) {
			return namedElements.get(0).getValue(locale, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Finds the first value of the given element in the given locale.
	 *
	 * @param name
	 *            the name of the element
	 * @param locale
	 *            the locale of the value
	 * @param defaultValue
	 *            the value to return, if no such element exists
	 * @return the value of the element with the given name or the default value if no such element exists
	 */
	public String getValue(final String name, final Locale locale, final String defaultValue) {
		final List<DynamicElement> namedElements = this.elementNames.get(name);
		if (!namedElements.isEmpty()) {
			return namedElements.get(0).getValue(locale, defaultValue);
		}
		return defaultValue;
	}

	/**
	 * Indicates that this container has at least one child element.
	 *
	 * @return {@code true} if at least one element exists
	 */
	public boolean hasElements() {
		return !this.elements.isEmpty();
	}

	@Override
	public Iterator<DynamicElement> iterator() {
		return this.elements.iterator();
	}

	/**
	 * Removes one element from the list of elements.
	 *
	 * @param element
	 *            the element to remove
	 */
	public void remove(final DynamicElement element) {
		this.elementNames.get(element.getName()).remove(element);
		this.elements.remove(element);
	}

	/**
	 * Removes all elements with the given name.
	 *
	 * @param name
	 *            the name of the elements to remove
	 */
	public void remove(final String name) {
		int removedSize = this.elementNames.removeAll(name).size();
		if (removedSize > 0) {
			// Stop if we removed enough elements
			for (final Iterator<DynamicElement> it = this.elements.iterator(); removedSize > 0 && it.hasNext();) {
				if (name.equals(it.next().getName())) {
					it.remove();
					removedSize--;
				}
			}
		}
	}

	/**
	 * Sets the value of a boolean element.
	 *
	 * If an element with the given name does not exist, a new one is created.
	 *
	 * @param name
	 *            the name of the element
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 * @return the element that was changed or created
	 */
	public DynamicElement setValue(final String name, final Locale locale, final boolean value) {
		return setValue(name, Type.BOOLEAN, locale, String.valueOf(value));
	}

	/**
	 * Sets the value of a date element.
	 *
	 * If an element with the given name does not exist, a new one is created.
	 *
	 * @param name
	 *            the name of the element
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 * @return the element that was changed or created
	 */
	public DynamicElement setValue(final String name, final Locale locale, final Date value) {
		return setValue(name, Type.DDM_DATE, locale, value == null ? "" : String.valueOf(value.getTime()));
	}

	/**
	 * Sets the value of a number element.
	 *
	 * If an element with the given name does not exist, a new one is created.
	 *
	 * @param name
	 *            the name of the element
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 * @return the element that was changed or created
	 */
	public DynamicElement setValue(final String name, final Locale locale, final double value) {
		return setValue(name, Type.DDM_NUMBER, locale, String.valueOf(value));
	}

	/**
	 * Sets the value of an integer element.
	 *
	 * If an element with the given name does not exist, a new one is created.
	 *
	 * @param name
	 *            the name of the element
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 * @return the element that was changed or created
	 */
	public DynamicElement setValue(final String name, final Locale locale, final long value) {
		return setValue(name, Type.DDM_INTEGER, locale, String.valueOf(value));
	}

	/**
	 * Sets the value of a string element.
	 *
	 * If an element with the given name does not exist, a new one is created.
	 *
	 * @param name
	 *            the name of the element
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 * @return the element that was changed or created
	 */
	public DynamicElement setValue(final String name, final Locale locale, final String value) {
		return setValue(name, Type.TEXT, locale, value == null ? "" : value);
	}

	/**
	 * Sets the value of a an element.
	 *
	 * If an element with the given name does not exist, a new one is created.
	 *
	 * If more than one element with the given name exists, only the first is changed.
	 *
	 * @param name
	 *            the name of the element
	 * @param type
	 *            the type of the element, if it needs to be created
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 * @return the element that was changed or created
	 */
	public DynamicElement setValue(final String name, final Type type, final Locale locale, final String value) {
		final List<DynamicElement> namedElements = this.elementNames.get(name);
		final DynamicElement element;
		if (namedElements.isEmpty()) {
			// Create a new one
			element = new DynamicElement(this, name, type.getId());
			this.elements.add(element);
			namedElements.add(element);
			element.setValue(getDefaultLocale(), value);
		} else {
			// Use the existing one
			element = namedElements.get(0);
			element.setValue(locale, value);
		}
		return element;
	}

	@Override
	public String toString() {
		return this.elements.toString();
	}

}
