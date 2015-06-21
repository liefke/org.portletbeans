package org.portletbeans.preferences;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.portlet.PortletPreferences;

/**
 * Annotation for fields of portlet beans that should be stored to {@link PortletPreferences}.
 *
 * @author Tobias Liefke
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreferenceField {

	/**
	 * The default value of this field.
	 *
	 * @return the default value for this field
	 */
	String defaultValue() default "";

	/**
	 * References the handler to use for loading and storing this field.
	 *
	 * The given class needs an empty public constructor.
	 *
	 * @return the handler class, derived from the field class by default
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends PreferenceFieldHandler> handler() default PreferenceFieldHandler.class;

	/**
	 * The key in the preferences for this field.
	 *
	 * @return the key to use - the field name is used if none other given
	 */
	String value() default "";

}
