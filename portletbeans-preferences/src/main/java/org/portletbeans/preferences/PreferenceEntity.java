package org.portletbeans.preferences;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an object which contains one ore more {@link PreferenceField} and may be used in conjunction with
 * {@link PreferenceEntityHandler}.
 *
 * @author Tobias Liefke
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreferenceEntity {
	// Tagging interface

}
