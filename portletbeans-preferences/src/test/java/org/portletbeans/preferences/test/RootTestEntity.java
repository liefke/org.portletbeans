package org.portletbeans.preferences.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.portletbeans.preferences.PreferenceField;

/**
 * Entity to test linked {@link ChildTestEntity child entities}.
 *
 * @author Tobias Liefke
 */
@Getter
@Setter
public class RootTestEntity {

	@PreferenceField
	private final List<ChildTestEntity> listProperty = new ArrayList<>();

	@PreferenceField
	private final Map<String, ChildTestEntity> mapProperty = new HashMap<>();

	@PreferenceField
	private ChildTestEntity childProperty;

	// This property is _not_ stored, as we have no PreferenceEntity annotation on the class
	private ChildTestEntity transientProperty;

}
