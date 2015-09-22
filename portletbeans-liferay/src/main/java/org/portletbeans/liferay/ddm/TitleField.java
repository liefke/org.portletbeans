package org.portletbeans.liferay.ddm;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for fields of a class that represent the title in a article.
 *
 * @author Tobias Liefke
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TitleField {

	// This is just an annotation for tagging

}
