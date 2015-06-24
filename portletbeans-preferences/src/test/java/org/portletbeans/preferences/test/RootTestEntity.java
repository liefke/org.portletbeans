package org.portletbeans.preferences.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.portletbeans.preferences.PreferenceEntity;
import org.portletbeans.preferences.PreferenceField;

/**
 * Entity to test linked {@link PreferenceEntity}.
 *
 * @author Tobias Liefke
 */
@Getter
@Setter
public class RootTestEntity {

	// This property is _not_ stored, as we have no PreferenceEntity annotation
	private ChildTestEntity transientProperty;

	@PreferenceField
	private final List<ChildTestEntity> listProperty = new ArrayList<>();

	@PreferenceField
	private final Map<String, ChildTestEntity> mapProperty = new HashMap<>();

	@PreferenceField
	private ChildTestEntity childProperty;

}
