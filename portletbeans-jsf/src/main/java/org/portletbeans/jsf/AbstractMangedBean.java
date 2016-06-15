package org.portletbeans.jsf;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.portletbeans.preferences.PreferenceFieldRegistry;

import lombok.extern.slf4j.Slf4j;

/**
 * Base class for {@link ManagedBean} that reads and stores preferences.
 *
 * @author Tobias Liefke
 */
@Slf4j
public abstract class AbstractMangedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The portlet preferences of the current request.
	 *
	 * @return the portlet preferences from the faces context
	 */
	protected PortletPreferences getPortletPreferences() {
		return ((PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getPreferences();
	}

	@PostConstruct
	private void init() {
		load(getPortletPreferences());
	}

	/**
	 * Loads the initial values from the preferences.
	 *
	 * @param preferences
	 *            the current portlet preferences
	 */
	protected void load(final PortletPreferences preferences) {
		PreferenceFieldRegistry.load(preferences, this);
	}

	/**
	 * Stores the values of this bean into the preferences and stays in the current portlet mode.
	 */
	public void store() {
		try {
			final PortletPreferences portletPreferences = getPortletPreferences();
			store(portletPreferences);
			portletPreferences.store();
		} catch (final ReadOnlyException | ValidatorException | IOException e) {
			log.error("Could not store portlet preferences", e);
		}
	}

	/**
	 * Stores the values of this bean into the preferences.
	 *
	 * @param preferences
	 *            the current portlet preferences
	 * @throws ReadOnlyException
	 *             if the preferences throw one
	 */
	protected void store(final PortletPreferences preferences) throws ReadOnlyException {
		PreferenceFieldRegistry.store(preferences, this);
	}

	/**
	 * Stores the values of this bean into the preferences and switches to the view mode of the portlet.
	 */
	public void storeAndView() {
		store();
		view();
	}

	/**
	 * Switches to the view mode of the portlet without storing the current preferences.
	 */
	public void view() {
		try {
			((ActionResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse())
					.setPortletMode(PortletMode.VIEW);
		} catch (final PortletModeException e) {
			throw new IllegalStateException(e);
		}
	}
}