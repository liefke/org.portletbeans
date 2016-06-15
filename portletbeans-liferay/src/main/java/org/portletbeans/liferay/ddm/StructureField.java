package org.portletbeans.liferay.ddm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.portletbeans.liferay.ddm.DynamicElement.Type;

/**
 * Annotation for fields of a class that represent a {@link DynamicElement} in a Liferay structure.
 *
 * @author Tobias Liefke
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StructureField {

	/**
	 * The default value of this field.
	 *
	 * @return the default value for this field
	 */
	String defaultValue() default "";

	/**
	 * References the handler to use for loading and storing this field.
	 *
	 * The handler class needs either an empty public constructor, or a public constructor that takes the handled field
	 * as argument
	 *
	 * @return the handler class, derived from the field class by default
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends StructureFieldHandler>handler() default StructureFieldHandler.class;

	/**
	 * This field stores the value of the surrounding element for a {@link StructuredEntity}.
	 *
	 * Only one field per class hierarchy may be marked as "primary". The content of {@link #value()} is ignored for
	 * this field.
	 *
	 * @return {@code true} if this field stores the value of the target element for a structured entity
	 */
	boolean primary() default false;

	/**
	 * The type of the element, if the default of the handler is not enough.
	 *
	 * Example:
	 *
	 * <pre>
	 *   &#64;StructureField(type = Type.TEXT_AREA)
	 * </pre>
	 *
	 * @return type of the element (an array to allow "empty" = {@code null} value)
	 */
	Type[]type() default {};

	/**
	 * The element name in the structure for this field.
	 *
	 * @return the element name to use - defaults to the the name of the annotated field
	 */
	String value() default "";

}
