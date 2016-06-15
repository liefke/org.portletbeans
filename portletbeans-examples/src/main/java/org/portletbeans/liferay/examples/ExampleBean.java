package org.portletbeans.liferay.examples;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.portletbeans.jsf.AbstractMangedBean;
import org.portletbeans.preferences.PreferenceEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * The controller for the working with preferences.
 *
 * @author Tobias Liefke
 */
@Getter
@Setter
@ManagedBean
@RequestScoped
@PreferenceEntity
public class ExampleBean extends AbstractMangedBean {

	private static final long serialVersionUID = 1L;

	private String text;

	private int number;

	private boolean enabled;

}