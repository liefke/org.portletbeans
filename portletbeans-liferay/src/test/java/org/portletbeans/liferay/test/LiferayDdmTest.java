package org.portletbeans.liferay.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.portletbeans.liferay.ddm.StructureFieldRegistry;

import com.liferay.portlet.dynamicdatamapping.ContentXmlException;

/**
 * Tests the preferences with the Liferay extensions.
 *
 * @author Tobias Liefke
 */
// CHECKSTYLE OFF: MagicNumber
public class LiferayDdmTest {

	/**
	 * Tests that {@link LiferayDdmTestEntity} is written and restored correctly from a DDM.
	 *
	 * @throws ContentXmlException
	 *             should not happen
	 */
	@Test
	public void testSimpleEntity() throws ContentXmlException {
		// Initialize the test entity
		final LiferayDdmTestEntity testEntity = new LiferayDdmTestEntity();
		testEntity.setStringProperty("Testing");
		testEntity.setBooleanProperty(true);
		testEntity.setCharProperty('c');
		testEntity.setDateProperty(new Date(DateUtils.MILLIS_PER_DAY));
		testEntity.setDoubleProperty(12.3456);
		testEntity.setIntProperty(7890);
		testEntity.setTransientProperty("transient");

		testEntity.setStringsProperty(Arrays.asList("s3", "s2", "s1"));
		testEntity.getFinalProperties().addAll(Arrays.asList("s5", "s4"));

		// Store and load the entity
		final TestArticle article = new TestArticle();
		final HashMap<String, byte[]> images = new HashMap<>();
		StructureFieldRegistry.write(testEntity, article, Locale.ENGLISH, images);
		final LiferayDdmTestEntity resultEntity = StructureFieldRegistry.createEntity(LiferayDdmTestEntity.class,
				article, Locale.ENGLISH);

		// Test the loaded result object
		assertThat(resultEntity.getStringProperty()).isEqualTo(testEntity.getStringProperty());
		assertThat(resultEntity.isBooleanProperty()).isEqualTo(testEntity.isBooleanProperty());
		assertThat(resultEntity.getCharProperty()).isEqualTo(testEntity.getCharProperty());
		assertThat(resultEntity.getDateProperty()).isEqualTo(testEntity.getDateProperty());
		assertThat(resultEntity.getDoubleProperty()).isEqualTo(testEntity.getDoubleProperty());
		assertThat(resultEntity.getIntProperty()).isEqualTo(testEntity.getIntProperty());
		assertThat(resultEntity.getTransientProperty()).isEqualTo("init() called");

		assertThat(resultEntity.getStringsProperty())
				.containsExactly(testEntity.getStringsProperty().toArray(new String[0]));
		assertThat(resultEntity.getFinalProperties())
				.containsExactly(testEntity.getFinalProperties().toArray(new String[0]));
	}

}
