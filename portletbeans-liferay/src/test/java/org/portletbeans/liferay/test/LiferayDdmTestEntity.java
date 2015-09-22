package org.portletbeans.liferay.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.portletbeans.liferay.ddm.StructureField;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Test entity for loading and storing a web content article.
 *
 * @author Tobias Liefke
 */
@NoArgsConstructor
@Getter
@Setter
public class LiferayDdmTestEntity {

	@StructureField
	private String stringProperty;

	@StructureField
	private Double doubleProperty;

	@StructureField
	private int intProperty;

	@StructureField
	private boolean booleanProperty;

	@StructureField
	private char charProperty;

	@StructureField
	private Date dateProperty;

	@StructureField
	private List<String> stringsProperty;

	@StructureField
	private final List<String> finalProperties = new ArrayList<>();

	private String transientProperty;

	@PostConstruct
	private void init() {
		if (this.transientProperty == null) {
			this.transientProperty = "init() called";
		}
	}

}
