package org.portletbeans.preferences.test;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.portletbeans.preferences.PreferenceEntity;

/**
 * Entity to test {@link PreferenceEntity}.
 *
 * @author Tobias Liefke
 */
@PreferenceEntity
@Getter
@Setter
@NoArgsConstructor
public class ChildTestEntity {

	private String name;

	private ChildTestEntity subchild;

	/**
	 * Creates a new instance of {@link ChildTestEntity}.
	 * 
	 * @param name
	 *            the name of the entity
	 */
	public ChildTestEntity(final String name) {
		this.name = name;
	}

}
