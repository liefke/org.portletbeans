package org.portletbeans.liferay.vocabulary;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.preferences.PreferenceFieldHandler;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContextThreadLocal;
import com.liferay.portlet.asset.model.AssetVocabulary;
import com.liferay.portlet.asset.service.AssetVocabularyLocalServiceUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles conversion of {@link AssetVocabulary category trees} into preference values.
 *
 * @author Tobias Liefke
 */
@Slf4j
public final class AssetVocabularyPreferenceHandler implements PreferenceFieldHandler<AssetVocabulary> {

	@Override
	public AssetVocabulary load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final String uuid = preferences.getValue(key, "");
		if (StringUtils.isEmpty(uuid)) {
			return null;
		}
		final long companyId = ServiceContextThreadLocal.getServiceContext().getCompanyId();
		try {
			return AssetVocabularyLocalServiceUtil.fetchAssetVocabularyByUuidAndCompanyId(uuid, companyId);
		} catch (final SystemException e) {
			log.error("Could not load vocabulary {}", uuid, e);
			return null;
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final AssetVocabulary vocabulary)
			throws ReadOnlyException {
		preferences.setValue(key, vocabulary == null ? null : vocabulary.getUuid());
	}

}