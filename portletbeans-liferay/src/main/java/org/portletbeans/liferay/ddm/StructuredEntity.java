package org.portletbeans.liferay.ddm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an object which contains one ore more {@link StructureField}s and is the the type of a structured field (in
 * another class).
 *
 * @author Tobias Liefke
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StructuredEntity {
	// Tagging interface

}
