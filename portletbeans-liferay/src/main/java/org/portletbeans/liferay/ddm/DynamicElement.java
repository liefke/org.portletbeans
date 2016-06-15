package org.portletbeans.liferay.ddm;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Represents one element from the {@link DynamicElements article content}.
 *
 * @author Tobias Liefke
 */
@RequiredArgsConstructor
public class DynamicElement extends DynamicElementsContainer {

	/**
	 * The list of available types.
	 */
	@RequiredArgsConstructor
	@Getter
	public enum Type {

		/**
		 * A selection of options for the user to choose from using a combo box. Can be configured to allow multiple
		 * selections, unlike {@link #RADIO}.
		 */
		LIST,

		/**
		 * An inline image. In difference to {@link #DOCUMENT_LIBRARY}, an image is removed when the reference is
		 * removed.
		 */
		IMAGE,

		/** Presents a checkbox to the user and stores either a true (checked) or false (unchecked) based on state. */
		BOOLEAN,

		/**
		 * A preformatted text field that displays a convenient date picker to assist in selecting the desired date. The
		 * format for the date is governed by the current locale.
		 */
		DDM_DATE("ddm-date"),

		/** Similar to {@link #DDM_NUMBER} except that it requires a decimal point (.) be present. */
		DDM_DECIMAL("ddm-decimal"),

		/**
		 * Select an existing uploaded document or image to attach to the data record. Also has the ability to upload
		 * documents and images into the Document Library.
		 */
		DOCUMENT_LIBRARY,

		/** Similar to {@link #DDM_NUMBER}, except that it constrains user input to non-fractional numbers. */
		DDM_INTEGER("ddm-integer"),

		/** An HTML text area. */
		TEXT_AREA,

		/** Inserts a link to another page in the same site. */
		LINK_TO_LAYOUT,

		/**
		 * Presents the user with a list of options to choose from using radio button inputs. Values are stored as
		 * strings. Similar to Select.
		 */
		RADIO,

		/** A simple text field for any string input. */
		TEXT,

		/** A large text box for long text input. */
		TEXT_BOX,

		/** Just a separator without any content. */
		SELECTION_BREAK,

		/** A text box that only accepts numbers as inputs, but puts no constraints on the kind of number entered. */
		DDM_NUMBER("ddm-number");

		private static final Map<String, Type> TYPES = new HashMap<>();

		static {
			for (final Type type : values()) {
				TYPES.put(type.id, type);
			}
		}

		/**
		 * Finds a type by its id.
		 *
		 * @param id
		 *            the id of the type in {@link DynamicElement#getType()}
		 * @return the type value or {@code null} if not found
		 */
		public static Type findImportedParameter(final String id) {
			return TYPES.get(id);
		}

		/** Lower case name. */
		private final String id;

		/** Initialize lower case id from our (upper case) name. */
		Type() {
			this.id = name().toLowerCase();
		}

	}

	/** The value of this element per locale. */
	@Getter
	private final Map<Locale, String> values = new HashMap<>();

	/** The parent element / the element values that contain the default locale. */
	@Getter
	private final DynamicElementsContainer parent;

	/** The name of this element. */
	@Getter
	private final String name;

	/**
	 * The type of this element.
	 *
	 * We use a string instead of the enum, as different types may appear in different version of Liferay.
	 */
	@Getter
	private final String type;

	/**
	 * The index type of this element.
	 *
	 * Usually just "keyword".
	 */
	@Getter
	@Setter
	private String indexType = "keyword";

	/**
	 * Creates a new filled element.
	 *
	 * @param parent
	 *            the parent container for accessing the default locale
	 * @param name
	 *            the name of the element
	 * @param type
	 *            the type of the element
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the new value
	 */
	public DynamicElement(final DynamicElementsContainer parent, final String name, final String type,
			final Locale locale, final String value) {
		this(parent, name, type);
		this.values.put(locale, value);
	}

	@Override
	public Locale getDefaultLocale() {
		return this.parent.getDefaultLocale();
	}

	/**
	 * Finds the boolean value in the given locale.
	 *
	 * @param locale
	 *            the locale of value
	 * @param defaultValue
	 *            the value to return, if no such value exists
	 * @return the found value
	 */
	public boolean getValue(final Locale locale, final boolean defaultValue) {
		final String value = getValue(locale, (String) null);
		if (StringUtils.isNotEmpty(value)) {
			return "true".equals(value);
		}
		return defaultValue;
	}

	/**
	 * Finds an enum value for this element in the given locale.
	 *
	 * @param locale
	 *            the locale of value
	 * @param enumClass
	 *            the class of the enums to return
	 * @return the found value or the first value from the enum class, if no value was found
	 */
	public <E extends Enum<E>> E getValue(final Locale locale, final Class<E> enumClass) {
		final String value = getValue(locale, (String) null);
		if (StringUtils.isNotBlank(value)) {
			try {
				return Enum.valueOf(enumClass, value.toUpperCase());
			} catch (final IllegalArgumentException e) {
				// Ignore and return the default
			}
		}
		try {
			return ((E[]) enumClass.getMethod("values").invoke(null))[0];
		} catch (final ReflectiveOperationException | IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Not an valid enum class " + enumClass, e);
		}
	}

	/**
	 * Finds the date in the given locale.
	 *
	 * @param locale
	 *            the locale of value
	 * @param defaultValue
	 *            the value to return, if no such value exists
	 * @return the found value
	 */
	public Date getValue(final Locale locale, final Date defaultValue) {
		final String value = getValue(locale, (String) null);
		if (StringUtils.isNotEmpty(value)) {
			try {
				return new Date(Long.parseLong(value));
			} catch (final NumberFormatException e) {
				// Ignore and return the default
			}
		}
		return defaultValue;
	}

	/**
	 * Finds the numeric value for this element in the given locale.
	 *
	 * @param locale
	 *            the locale of the value
	 * @param defaultValue
	 *            the value to return if no such value exists
	 * @return the found value
	 */
	public Number getValue(final Locale locale, final Number defaultValue) {
		final String value = getValue(locale, (String) null);
		if (StringUtils.isNotEmpty(value)) {
			if (!(defaultValue instanceof Double) && !(defaultValue instanceof Float)) {
				try {
					return Long.parseLong(value);
				} catch (final NumberFormatException e) {
					// Ignore and return the default
				}
			}
			try {
				return Double.parseDouble(value);
			} catch (final NumberFormatException e) {
				// Ignore and return the default
			}
		}
		return defaultValue;
	}

	/**
	 * Finds the value for the given locale or returns the default value if that one was not found.
	 *
	 * @param locale
	 *            the current users locale
	 * @param defaultValue
	 *            the value to return if no souch value exists (or the string is empty).
	 * @return the value
	 */
	public String getValue(final Locale locale, final String defaultValue) {
		String value = this.values.get(locale);
		if (StringUtils.isNotEmpty(value)) {
			return value;
		}
		final Locale defaultLocale = getDefaultLocale();
		if (defaultLocale != null && !defaultLocale.equals(locale)) {
			value = this.values.get(defaultLocale);
			if (StringUtils.isNotEmpty(value)) {
				return value;
			}
		}
		return defaultValue;
	}

	/**
	 * Sets a boolean value for this element.
	 *
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 */
	public void setValue(final Locale locale, final boolean value) {
		setValue(locale, String.valueOf(value));
	}

	/**
	 * Sets a date value for this element.
	 *
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 */
	public void setValue(final Locale locale, final Date value) {
		setValue(locale, value == null ? "" : String.valueOf(value.getTime()));
	}

	/**
	 * Sets a number value for this element.
	 *
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 */
	public void setValue(final Locale locale, final double value) {
		setValue(locale, String.valueOf(value));
	}

	/**
	 * Sets an integer value for this element.
	 *
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the value for the locale
	 */
	public void setValue(final Locale locale, final long value) {
		setValue(locale, String.valueOf(value));
	}

	/**
	 * Sets the value for the given locale.
	 *
	 * @param locale
	 *            the locale of the value
	 * @param value
	 *            the new value, {@code null} to remove the value for the given locale
	 */
	public void setValue(final Locale locale, final String value) {
		if (value == null) {
			if (locale == getDefaultLocale()) {
				this.values.put(locale, "");
			} else {
				this.values.remove(locale);
			}
		} else {
			this.values.put(locale, value);
		}
	}

	@Override
	public String toString() {
		return this.name + ": " + this.values + (hasElements() ? " " + super.toString() : "");
	}

}
