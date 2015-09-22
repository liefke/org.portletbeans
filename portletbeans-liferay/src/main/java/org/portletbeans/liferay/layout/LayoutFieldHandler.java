package org.portletbeans.liferay.layout;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.preferences.PreferenceFieldHandler;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.ServiceContextThreadLocal;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles conversion of {@link Layout pages} into preference values.
 *
 * @author Tobias Liefke
 */
@Slf4j
public final class LayoutFieldHandler implements PreferenceFieldHandler<Layout> {

	@Override
	public Layout load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final String uuid = preferences.getValue(key, "");
		if (StringUtils.isEmpty(uuid)) {
			return null;
		}
		final long companyId = ServiceContextThreadLocal.getServiceContext().getCompanyId();
		try {
			return LayoutLocalServiceUtil.getLayoutByUuidAndCompanyId(uuid, companyId);
		} catch (final SystemException | PortalException e) {
			log.error("Could not load layout {}", uuid, e);
			return null;
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final Layout layout)
			throws ReadOnlyException {
		preferences.setValue(key, layout == null ? null : layout.getUuid());
	}

}