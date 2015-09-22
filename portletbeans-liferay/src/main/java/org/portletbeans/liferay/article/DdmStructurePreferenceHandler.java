package org.portletbeans.liferay.article;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.preferences.PreferenceFieldHandler;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContextThreadLocal;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles conversion of {@link DDMStructure document types} into preference values.
 *
 * @author Stefanie Pöschl
 * @author Tobias Liefke
 */
@Slf4j
public final class DdmStructurePreferenceHandler implements PreferenceFieldHandler<DDMStructure> {

	@Override
	public DDMStructure load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final String uuid = preferences.getValue(key, "");
		if (StringUtils.isEmpty(uuid)) {
			return null;
		}
		final long groupId = ServiceContextThreadLocal.getServiceContext().getScopeGroupId();
		try {
			return DDMStructureLocalServiceUtil.fetchDDMStructureByUuidAndGroupId(uuid, groupId);
		} catch (final SystemException e) {
			log.error("Could not load structure {} from group {}", new Object[] { uuid, groupId, e });
			return null;
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final DDMStructure structure)
			throws ReadOnlyException {
		preferences.setValue(key, structure == null ? null : structure.getUuid());
	}

}