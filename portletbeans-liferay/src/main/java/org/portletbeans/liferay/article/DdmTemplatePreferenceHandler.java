package org.portletbeans.liferay.article;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.preferences.PreferenceFieldHandler;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContextThreadLocal;
import com.liferay.portlet.dynamicdatamapping.model.DDMTemplate;
import com.liferay.portlet.dynamicdatamapping.service.DDMTemplateLocalServiceUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles conversion of {@link DDMTemplate templates} into preference values.
 *
 * @author Stefanie Pöschl
 */
@Slf4j
public final class DdmTemplatePreferenceHandler implements PreferenceFieldHandler<DDMTemplate> {

	@Override
	public DDMTemplate load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final String uuid = preferences.getValue(key, "");
		if (StringUtils.isEmpty(uuid)) {
			return null;
		}
		final long groupId = ServiceContextThreadLocal.getServiceContext().getScopeGroupId();
		try {
			return DDMTemplateLocalServiceUtil.fetchDDMTemplateByUuidAndGroupId(uuid, groupId);
		} catch (final SystemException e) {
			log.error("Could not load template {} from group {}", new Object[] { uuid, groupId, e });
			return null;
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final DDMTemplate template)
			throws ReadOnlyException {
		preferences.setValue(key, template == null ? null : template.getUuid());
	}

}