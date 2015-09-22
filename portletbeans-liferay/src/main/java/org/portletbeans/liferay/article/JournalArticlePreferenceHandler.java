package org.portletbeans.liferay.article;

import java.io.IOException;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.portletbeans.preferences.PreferenceFieldHandler;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.service.ServiceContextThreadLocal;
import com.liferay.portlet.journal.NoSuchArticleException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleResource;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalArticleResourceLocalServiceUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Handles conversion of {@link JournalArticle documents} into preference values.
 *
 * @author Tobias Liefke
 */
@Slf4j
public final class JournalArticlePreferenceHandler implements PreferenceFieldHandler<JournalArticle> {

	private static final String RESOURCE_UUID_SUFFIX = ".resourceUuid";

	private static final String ARTICLE_ID_SUFFIX = ".articleID";

	@Override
	public JournalArticle load(final PortletPreferences preferences, final String key, final String defaultValue) {
		final String resourceUuid = preferences.getValue(key + RESOURCE_UUID_SUFFIX, defaultValue);

		if (StringUtils.isEmpty(resourceUuid)) {
			return null;
		}

		final long groupId = ServiceContextThreadLocal.getServiceContext().getScopeGroupId();
		try {
			JournalArticle result = null;
			String articleId = preferences.getValue(key + ARTICLE_ID_SUFFIX, "");
			if (StringUtils.isNotEmpty(articleId)) {
				try {
					result = JournalArticleLocalServiceUtil.getLatestArticle(groupId, articleId,
							WorkflowConstants.STATUS_APPROVED);
				} catch (final NoSuchArticleException e) {
					// Ignore and try the fallback
				}
			}
			if (result == null || !result.getArticleResourceUuid().equals(resourceUuid)) {
				// Fallback - the article id has changed
				final JournalArticleResource articleResource = JournalArticleResourceLocalServiceUtil
						.fetchJournalArticleResourceByUuidAndGroupId(resourceUuid, groupId);
				articleId = articleResource.getArticleId();
				result = JournalArticleLocalServiceUtil.getLatestArticle(groupId, articleId,
						WorkflowConstants.STATUS_APPROVED);
				try {
					preferences.setValue(key + ARTICLE_ID_SUFFIX, articleId);
					preferences.store();
				} catch (final ReadOnlyException | ValidatorException | IOException e) {
					log.error("Could not store article {}", articleId, e);
				}
			}
			return result;
		} catch (final SystemException | PortalException e) {
			log.error("Could not load article {}", resourceUuid, e);
			return null;
		}
	}

	@Override
	public void store(final PortletPreferences preferences, final String key, final JournalArticle article)
			throws ReadOnlyException {
		String articleResourceUuid = null;
		String articleId = null;
		if (article != null) {
			try {
				articleResourceUuid = article.getArticleResourceUuid();
				articleId = article.getArticleId();
			} catch (final PortalException | SystemException e) {
				log.error("Could not load article resource id for article {}", article.getArticleId(), e);
			}
		}
		preferences.setValue(key + RESOURCE_UUID_SUFFIX, articleResourceUuid);
		preferences.setValue(key + ARTICLE_ID_SUFFIX, articleId);
	}

}