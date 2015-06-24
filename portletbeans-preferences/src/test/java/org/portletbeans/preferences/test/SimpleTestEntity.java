package org.portletbeans.preferences.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.portletbeans.preferences.PreferenceEntity;
import org.portletbeans.preferences.PreferenceField;
import org.portletbeans.preferences.Transient;

/**
 * An entity for testing {@link PreferenceField} entities.
 *
 * @author Tobias Liefke
 */
@PreferenceEntity
@Getter
@Setter
public class SimpleTestEntity {

	private String stringProperty;

	private Double doubleProperty;

	private int intProperty;

	private boolean booleanProperty;

	private char charProperty;

	private Date dateProperty;

	private List<String> stringsProperty;

	private final List<String> finalProperties = new ArrayList<>();

	private Map<Integer, Double> numberMap = new LinkedHashMap<>();

	private transient String transient1;

	@Transient
	private String transient2;

}
