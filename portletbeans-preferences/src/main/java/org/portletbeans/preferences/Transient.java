package org.portletbeans.preferences;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field from a {@link PreferenceEntity}, which should not be a {@link PreferenceField}.
 *
 * @author Tobias Liefke
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transient {

	// This annotation has no parameters

}
