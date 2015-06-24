package org.portletbeans.preferences;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an object which contains one ore more {@link PreferenceField}.
 *
 * All fields of the annotated class are included as {@link PreferenceField}, even without the annotation. To exclude a
 * field from the preferences it has to be marked as {@code transient} or {@link Transient}.
 *
 * A field that references an object that is annotated with {@code @PreferenceEntity} is automatically read with the
 * {@link PreferenceEntityHandler}.
 *
 * @author Tobias Liefke
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreferenceEntity {
	// Annotation for tagging

}
