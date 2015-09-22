package org.portletbeans.preferences.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import java.util.Arrays;
import java.util.Date;

import javax.portlet.PortletPreferences;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.portletbeans.preferences.PreferenceFieldRegistry;

/**
 * Tests this preferences.
 *
 * @author Tobias Liefke
 */
// CHECKSTYLE OFF: MagicNumber
public class PreferenceTest {

	/**
	 * Tests that child entities are written and restored correctly from {@link PortletPreferences}.
	 */
	@Test
	public void testChildEntities() {
		// Initialize the test entities
		final RootTestEntity testEntity = new RootTestEntity();

		testEntity.setTransientProperty(new ChildTestEntity());

		testEntity.setChildProperty(new ChildTestEntity("child"));
		testEntity.getChildProperty().setSubchild(new ChildTestEntity());

		testEntity.getListProperty()
				.addAll(Arrays.asList(new ChildTestEntity("child1"), new ChildTestEntity("child2")));

		testEntity.getMapProperty().put("child3", new ChildTestEntity("3"));
		testEntity.getMapProperty().put("child4", new ChildTestEntity(""));

		// Store and load the preferences
		final PortletPreferences preferences = new TestPreferences();
		PreferenceFieldRegistry.store(preferences, testEntity);
		final RootTestEntity resultEntity = PreferenceFieldRegistry.load(preferences, new RootTestEntity());

		// Test the loaded result objects
		assertThat(resultEntity.getTransientProperty()).isNull();

		assertThat(resultEntity.getChildProperty().getName()).isEqualTo(testEntity.getChildProperty().getName());
		assertThat(resultEntity.getChildProperty().getSubchild().getName()).isNull();
		assertThat(resultEntity.getChildProperty().getSubchild().getSubchild()).isNull();

		assertThat(resultEntity.getListProperty().get(0).getName())
				.isEqualTo(testEntity.getListProperty().get(0).getName());
		assertThat(resultEntity.getListProperty().get(1).getName())
				.isEqualTo(testEntity.getListProperty().get(1).getName());

		assertThat(resultEntity.getMapProperty().get("child3").getName()).isEqualTo("3");
		assertThat(resultEntity.getMapProperty().get("child4").getName()).isEqualTo("");
	}

	/**
	 * Tests that {@link SimpleTestEntity} is written and restored correctly from {@link PortletPreferences}.
	 */
	@Test
	public void testSimplePreferences() {
		// Initialize the test entity
		final SimpleTestEntity testEntity = new SimpleTestEntity();
		testEntity.setStringProperty("Testing");
		testEntity.setBooleanProperty(true);
		testEntity.setCharProperty('c');
		testEntity.setDateProperty(new Date(DateUtils.MILLIS_PER_DAY));
		testEntity.setDoubleProperty(12.3456);
		testEntity.setIntProperty(7890);
		testEntity.setTransient1("transient1");
		testEntity.setTransient2("transient2");

		testEntity.setStringsProperty(Arrays.asList("s3", "s2", "s1"));
		testEntity.getFinalProperties().addAll(Arrays.asList("s5", "s4"));
		testEntity.getNumberMap().put(1, 2.3);
		testEntity.getNumberMap().put(4, 5.6);

		// Store and test the preferences
		final PortletPreferences preferences = new TestPreferences();
		PreferenceFieldRegistry.store(preferences, testEntity);

		assertThat(preferences.getValue("stringProperty", "")).isEqualTo("Testing");
		assertThat(preferences.getValue("booleanProperty", "false")).isEqualTo("true");
		assertThat(preferences.getValue("charProperty", "")).isEqualTo("c");
		assertThat(preferences.getValue("dateProperty", "")).isEqualTo("86400000");
		assertThat(preferences.getValue("doubleProperty", "")).isEqualTo("12.3456");
		assertThat(preferences.getValue("intProperty", "")).isEqualTo("7890");
		assertThat(preferences.getValue("transient1", "")).isEqualTo("");
		assertThat(preferences.getValue("transient2", "")).isEqualTo("");

		// Load the preferences
		final SimpleTestEntity resultEntity = PreferenceFieldRegistry.load(preferences, new SimpleTestEntity());

		// Test the loaded result object
		assertThat(resultEntity.getStringProperty()).isEqualTo(testEntity.getStringProperty());
		assertThat(resultEntity.isBooleanProperty()).isEqualTo(testEntity.isBooleanProperty());
		assertThat(resultEntity.getCharProperty()).isEqualTo(testEntity.getCharProperty());
		assertThat(resultEntity.getDateProperty()).isEqualTo(testEntity.getDateProperty());
		assertThat(resultEntity.getDoubleProperty()).isEqualTo(testEntity.getDoubleProperty());
		assertThat(resultEntity.getIntProperty()).isEqualTo(testEntity.getIntProperty());
		assertThat(resultEntity.getTransient1()).isNull();
		assertThat(resultEntity.getTransient2()).isNull();

		assertThat(resultEntity.getStringsProperty())
				.containsExactly(testEntity.getStringsProperty().toArray(new String[0]));
		assertThat(resultEntity.getFinalProperties())
				.containsExactly(testEntity.getFinalProperties().toArray(new String[0]));
		assertThat(resultEntity.getNumberMap()).containsExactly(entry(1, 2.3), entry(4, 5.6));
	}

}
