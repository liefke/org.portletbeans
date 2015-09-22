package org.portletbeans.liferay.test;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.liferay.portal.LocaleException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.model.impl.BaseModelImpl;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleResource;
import com.liferay.portlet.journal.model.JournalFolder;
import com.liferay.portlet.trash.model.TrashEntry;

/**
 * Implentation of a article for testing load and store.
 *
 * @author Tobias Liefke
 */
public class TestArticle extends BaseModelImpl<JournalArticle>implements JournalArticle {

	private static final long serialVersionUID = 1L;

	private String title;

	private String content;

	@Override
	public String buildTreePath() throws PortalException, SystemException {
		return null;
	}

	@Override
	public Object clone() {
		return null;
	}

	@Override
	public int compareTo(final JournalArticle journalArticle) {
		return 0;
	}

	@Override
	public boolean getApproved() {
		return false;
	}

	@Override
	public String getArticleId() {
		return null;
	}

	@Override
	public String getArticleImageURL(final ThemeDisplay themeDisplay) {
		return null;
	}

	@Override
	public JournalArticleResource getArticleResource() throws PortalException, SystemException {
		return null;
	}

	@Override
	public String getArticleResourceUuid() throws PortalException, SystemException {
		return null;
	}

	@Override
	public String[] getAvailableLanguageIds() {
		return null;
	}

	@Override
	public String[] getAvailableLocales() {
		return null;
	}

	@Override
	public String getClassName() {
		return null;
	}

	@Override
	public long getClassNameId() {
		return 0;
	}

	@Override
	public long getClassPK() {
		return 0;
	}

	@Override
	public long getCompanyId() {
		return 0;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public String getContentByLocale(final String languageId) {
		return this.content;
	}

	@Override
	public Date getCreateDate() {
		return null;
	}

	@Override
	public String getDefaultLanguageId() {
		return null;
	}

	@Override
	public String getDefaultLocale() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getDescription(final Locale locale) {
		return null;
	}

	@Override
	public String getDescription(final Locale locale, final boolean useDefault) {
		return null;
	}

	@Override
	public String getDescription(final String languageId) {
		return null;
	}

	@Override
	public String getDescription(final String languageId, final boolean useDefault) {
		return null;
	}

	@Override
	public String getDescriptionCurrentLanguageId() {
		return null;
	}

	@Override
	public String getDescriptionCurrentValue() {
		return null;
	}

	@Override
	public Map<Locale, String> getDescriptionMap() {
		return null;
	}

	@Override
	public Date getDisplayDate() {
		return null;
	}

	@Override
	public Date getExpirationDate() {
		return null;
	}

	@Override
	public JournalFolder getFolder() throws PortalException, SystemException {
		return null;
	}

	@Override
	public long getFolderId() {
		return 0;
	}

	@Override
	public long getGroupId() {
		return 0;
	}

	@Override
	public long getId() {
		return 0;
	}

	@Override
	public boolean getIndexable() {
		return false;
	}

	@Override
	public String getLayoutUuid() {
		return null;
	}

	@Override
	public Class<?> getModelClass() {
		return null;
	}

	@Override
	public String getModelClassName() {
		return null;
	}

	@Override
	public Date getModifiedDate() {
		return null;
	}

	@Override
	public long getPrimaryKey() {
		return 0;
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return null;
	}

	@Override
	public long getResourcePrimKey() {
		return 0;
	}

	@Override
	public Date getReviewDate() {
		return null;
	}

	@Override
	public boolean getSmallImage() {
		return false;
	}

	@Override
	public long getSmallImageId() {
		return 0;
	}

	@Override
	public String getSmallImageType() throws PortalException, SystemException {
		return null;
	}

	@Override
	public String getSmallImageURL() {
		return null;
	}

	@Override
	public StagedModelType getStagedModelType() {
		return null;
	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public long getStatusByUserId() {
		return 0;
	}

	@Override
	public String getStatusByUserName() {
		return null;
	}

	@Override
	public String getStatusByUserUuid() throws SystemException {
		return null;
	}

	@Override
	public Date getStatusDate() {
		return null;
	}

	@Override
	public String getStructureId() {
		return null;
	}

	@Override
	public String getTemplateId() {
		return null;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getTitle(final Locale locale) {
		return this.title;
	}

	@Override
	public String getTitle(final Locale locale, final boolean useDefault) {
		return this.title;
	}

	@Override
	public String getTitle(final String languageId) {
		return this.title;
	}

	@Override
	public String getTitle(final String languageId, final boolean useDefault) {
		return this.title;
	}

	@Override
	public String getTitleCurrentLanguageId() {
		return null;
	}

	@Override
	public String getTitleCurrentValue() {
		return this.title;
	}

	@Override
	public Map<Locale, String> getTitleMap() {
		return null;
	}

	@Override
	public TrashEntry getTrashEntry() throws PortalException, SystemException {
		return null;
	}

	@Override
	public long getTrashEntryClassPK() {
		return 0;
	}

	@Override
	public TrashHandler getTrashHandler() {
		return null;
	}

	@Override
	public String getTreePath() {
		return null;
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public String getUrlTitle() {
		return null;
	}

	@Override
	public long getUserId() {
		return 0;
	}

	@Override
	public String getUserName() {
		return null;
	}

	@Override
	public String getUserUuid() throws SystemException {
		return null;
	}

	@Override
	public String getUuid() {
		return null;
	}

	@Override
	public double getVersion() {
		return 0;
	}

	@Override
	public boolean hasApprovedVersion() throws SystemException {
		return false;
	}

	@Override
	public boolean isApproved() {
		return false;
	}

	@Override
	public boolean isDenied() {
		return false;
	}

	@Override
	public boolean isDraft() {
		return false;
	}

	@Override
	public boolean isExpired() {
		return false;
	}

	@Override
	public boolean isInactive() {
		return false;
	}

	@Override
	public boolean isIncomplete() {
		return false;
	}

	@Override
	public boolean isIndexable() {
		return false;
	}

	@Override
	public boolean isInTrash() {
		return false;
	}

	@Override
	public boolean isInTrashContainer() {
		return false;
	}

	@Override
	public boolean isInTrashExplicitly() throws SystemException {
		return false;
	}

	@Override
	public boolean isPending() {
		return false;
	}

	@Override
	public boolean isResourceMain() {
		return false;
	}

	@Override
	public boolean isScheduled() {
		return false;
	}

	@Override
	public boolean isSmallImage() {
		return false;
	}

	@Override
	public boolean isTemplateDriven() {
		return false;
	}

	@Override
	public void persist() throws SystemException {
		// Nothing to do
	}

	@Override
	public void prepareLocalizedFieldsForImport() throws LocaleException {
		// Nothing to do
	}

	@Override
	public void prepareLocalizedFieldsForImport(final Locale defaultImportLocale) throws LocaleException {
		// Nothing to do
	}

	@Override
	public void setArticleId(final String articleId) {
		// Nothing to do
	}

	@Override
	public void setClassName(final String className) {
		// Nothing to do
	}

	@Override
	public void setClassNameId(final long classNameId) {
		// Nothing to do
	}

	@Override
	public void setClassPK(final long classPK) {
		// Nothing to do
	}

	@Override
	public void setCompanyId(final long companyId) {
		// Nothing to do
	}

	@Override
	public void setContent(final String content) {
		this.content = content;
	}

	@Override
	public void setCreateDate(final Date createDate) {
		// Nothing to do
	}

	@Override
	public void setDescription(final String description) {
		// Nothing to do
	}

	@Override
	public void setDescription(final String description, final Locale locale) {
		// Nothing to do
	}

	@Override
	public void setDescription(final String description, final Locale locale, final Locale defaultLocale) {
		// Nothing to do
	}

	@Override
	public void setDescriptionCurrentLanguageId(final String languageId) {
		// Nothing to do
	}

	@Override
	public void setDescriptionMap(final Map<Locale, String> descriptionMap) {
		// Nothing to do
	}

	@Override
	public void setDescriptionMap(final Map<Locale, String> descriptionMap, final Locale defaultLocale) {
		// Nothing to do
	}

	@Override
	public void setDisplayDate(final Date displayDate) {
		// Nothing to do
	}

	@Override
	public void setExpirationDate(final Date expirationDate) {
		// Nothing to do
	}

	@Override
	public void setFolderId(final long folderId) {
		// Nothing to do
	}

	@Override
	public void setGroupId(final long groupId) {
		// Nothing to do
	}

	@Override
	public void setId(final long id) {
		// Nothing to do
	}

	@Override
	public void setIndexable(final boolean indexable) {
		// Nothing to do
	}

	@Override
	public void setLayoutUuid(final String layoutUuid) {
		// Nothing to do
	}

	@Override
	public void setModifiedDate(final Date modifiedDate) {
		// Nothing to do
	}

	@Override
	public void setPrimaryKey(final long primaryKey) {
		// Nothing to do
	}

	@Override
	public void setPrimaryKeyObj(final Serializable primaryKeyObj) {
		// Nothing to do
	}

	@Override
	public void setResourcePrimKey(final long resourcePrimKey) {
		// Nothing to do
	}

	@Override
	public void setReviewDate(final Date reviewDate) {
		// Nothing to do
	}

	@Override
	public void setSmallImage(final boolean smallImage) {
		// Nothing to do
	}

	@Override
	public void setSmallImageId(final long smallImageId) {
		// Nothing to do
	}

	@Override
	public void setSmallImageType(final String smallImageType) {
		// Nothing to do
	}

	@Override
	public void setSmallImageURL(final String smallImageURL) {
		// Nothing to do
	}

	@Override
	public void setStatus(final int status) {
		// Nothing to do
	}

	@Override
	public void setStatusByUserId(final long statusByUserId) {
		// Nothing to do
	}

	@Override
	public void setStatusByUserName(final String statusByUserName) {
		// Nothing to do
	}

	@Override
	public void setStatusByUserUuid(final String statusByUserUuid) {
		// Nothing to do
	}

	@Override
	public void setStatusDate(final Date statusDate) {
		// Nothing to do
	}

	@Override
	public void setStructureId(final String structureId) {
		// Nothing to do
	}

	@Override
	public void setTemplateId(final String templateId) {
		// Nothing to do
	}

	@Override
	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public void setTitle(final String newTitle, final Locale locale) {
		this.title = newTitle;
	}

	@Override
	public void setTitle(final String newTitle, final Locale locale, final Locale defaultLocale) {
		this.title = newTitle;
	}

	@Override
	public void setTitleCurrentLanguageId(final String languageId) {
		// Nothing to do
	}

	@Override
	public void setTitleMap(final Map<Locale, String> titleMap) {
		// Nothing to do
	}

	@Override
	public void setTitleMap(final Map<Locale, String> titleMap, final Locale defaultLocale) {
		// Nothing to do
	}

	@Override
	public void setTreePath(final String treePath) {
		// Nothing to do
	}

	@Override
	public void setType(final String type) {
		// Nothing to do
	}

	@Override
	public void setUrlTitle(final String urlTitle) {
		// Nothing to do
	}

	@Override
	public void setUserId(final long userId) {
		// Nothing to do
	}

	@Override
	public void setUserName(final String userName) {
		// Nothing to do
	}

	@Override
	public void setUserUuid(final String userUuid) {
		// Nothing to do
	}

	@Override
	public void setUuid(final String uuid) {
		// Nothing to do
	}

	@Override
	public void setVersion(final double version) {
		// Nothing to do
	}

	@Override
	public String toXmlString() {
		return null;
	}

	@Override
	public void updateTreePath(final String treePath) throws SystemException {
		// Nothing to do
	}
}
