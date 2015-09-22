package org.portletbeans.liferay.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;

/**
 * Represents one attribute for sorting.
 *
 * @author pfavero
 */
@Getter
@EqualsAndHashCode
public class DocumentAttribute implements Serializable {

	/**
	 * Constructs the complete fieldname for a custom field, as used in the lucene index.
	 *
	 * Only valid if the given field is a custom field.
	 *
	 * @param name
	 *            the field name of the structure
	 *
	 * @param structure
	 *            the structure
	 * @param locale
	 *            the locale of the current user
	 * @return the complete field name
	 */
	public static final String toField(final String name, final DDMStructure structure, final Locale locale) {
		return "ddm/" + structure.getStructureId() + '/' + name + "_" + LocaleUtil.toLanguageId(locale);
	}

	/**
	 * The attribute to use, when sorting for title.
	 */
	public static final String TITLE_ATTRIBUTE = "title_sortable";

	private static final long serialVersionUID = -3938497142904891064L;

	/**
	 * The mapping from the data type to its sort type.
	 *
	 * See liferay-ddm-structure...xsd.
	 */
	private static final Map<String, Integer> TYPE_MAPPING = new HashMap<>();

	static {
		TYPE_MAPPING.put("string", Sort.STRING_TYPE);
		TYPE_MAPPING.put("boolean", Sort.INT_TYPE);
		TYPE_MAPPING.put("integer", Sort.INT_TYPE);
		TYPE_MAPPING.put("date", Sort.LONG_TYPE);
		TYPE_MAPPING.put("double", Sort.DOUBLE_TYPE);
		TYPE_MAPPING.put("float", Sort.FLOAT_TYPE);
		TYPE_MAPPING.put("long", Sort.LONG_TYPE);
		TYPE_MAPPING.put("number", Sort.DOUBLE_TYPE);
		TYPE_MAPPING.put("short", Sort.INT_TYPE);
	}

	private final String name;

	private final int type;

	private final boolean customField;

	/**
	 * Creates a new instance of SortAttribute.
	 *
	 * @param nameAndType
	 *            the name and optional the type of the field
	 */
	public DocumentAttribute(final String nameAndType) {
		int finalType = Sort.STRING_TYPE;
		String finalName = nameAndType;
		final String[] names = nameAndType.split("\\|");
		this.customField = names[0].equals("ddm");
		if (names.length > 2) {
			try {
				finalType = Integer.parseInt(names[2]);
				finalName = names[1];
			} catch (final NumberFormatException e) {
				// Ignore
			}
		} else if (names.length == 2) {
			if (this.customField) {
				finalName = names[1];
			} else {
				try {
					finalType = Integer.parseInt(names[1]);
					finalName = names[0];
				} catch (final NumberFormatException e) {
					// Ignore
				}
			}
		}

		this.type = finalType;
		this.name = finalName;
	}

	/**
	 * Creates a new instance of SortAttribute from a {@link DDMStructure}.
	 *
	 * @param name
	 *            the name of the field
	 * @param structure
	 *            the structure of the field
	 */
	public DocumentAttribute(final String name, final DDMStructure structure) {
		this.customField = true;
		this.name = name;
		Integer newType;
		try {
			newType = TYPE_MAPPING.get(structure.getFieldDataType(name));
		} catch (final PortalException | SystemException e) {
			throw new IllegalArgumentException(e);
		}
		this.type = newType == null ? Sort.INT_TYPE : newType;
	}

	/**
	 * Creates a new instance of SortAttribute for a predefined field.
	 *
	 * @param name
	 *            the name of the field
	 * @param type
	 *            the type of the field
	 */
	public DocumentAttribute(final String name, final int type) {
		this.name = name;
		this.type = type;
		this.customField = false;
	}

	/**
	 * Constructs the complete fieldname, as used in the lucene index.
	 *
	 * Only valid if this is a {@link #isCustomField() custom field}.
	 *
	 * @param structure
	 *            the structure
	 * @param locale
	 *            the locale of the current user
	 * @return the complete field name
	 */
	public String toField(final DDMStructure structure, final Locale locale) {
		return toField(this.name, structure, locale);
	}

	/**
	 * Converts this model object to the one used by Liferay.
	 *
	 * @param ascending
	 *            {@code true} to sort ascending
	 * @param structure
	 *            the current structure
	 * @param locale
	 *            the locale of the current user
	 * @return the Liferay model object.
	 */
	public Sort toSort(final boolean ascending, final DDMStructure structure, final Locale locale) {
		String field = this.name;
		if (this.customField) {
			field = toField(structure, locale).concat("_sortable");
		}
		return new Sort(field, this.type, !ascending);
	}

	@Override
	public String toString() {
		if (this.type == Sort.STRING_TYPE) {
			return this.customField ? "ddm|" + this.name : this.name;
		}
		return (this.customField ? "ddm|" : "") + this.name + '|' + this.type;
	}
}
