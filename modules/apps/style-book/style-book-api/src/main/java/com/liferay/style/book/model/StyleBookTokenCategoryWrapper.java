/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link StyleBookTokenCategory}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookTokenCategory
 * @generated
 */
public class StyleBookTokenCategoryWrapper
	extends BaseModelWrapper<StyleBookTokenCategory>
	implements ModelWrapper<StyleBookTokenCategory>, StyleBookTokenCategory {

	public StyleBookTokenCategoryWrapper(
		StyleBookTokenCategory styleBookTokenCategory) {

		super(styleBookTokenCategory);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put(
			"styleBookTokenCategoryId", getStyleBookTokenCategoryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("styleBookEntryId", getStyleBookEntryId());
		attributes.put(
			"themeFrontendTokenDefinitionId",
			getThemeFrontendTokenDefinitionId());
		attributes.put("name", getName());
		attributes.put("description", getDescription());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		Long styleBookTokenCategoryId = (Long)attributes.get(
			"styleBookTokenCategoryId");

		if (styleBookTokenCategoryId != null) {
			setStyleBookTokenCategoryId(styleBookTokenCategoryId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long styleBookEntryId = (Long)attributes.get("styleBookEntryId");

		if (styleBookEntryId != null) {
			setStyleBookEntryId(styleBookEntryId);
		}

		String themeFrontendTokenDefinitionId = (String)attributes.get(
			"themeFrontendTokenDefinitionId");

		if (themeFrontendTokenDefinitionId != null) {
			setThemeFrontendTokenDefinitionId(themeFrontendTokenDefinitionId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}
	}

	@Override
	public StyleBookTokenCategory cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this style book token category.
	 *
	 * @return the company ID of this style book token category
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this style book token category.
	 *
	 * @return the create date of this style book token category
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this style book token category.
	 *
	 * @return the ct collection ID of this style book token category
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the description of this style book token category.
	 *
	 * @return the description of this style book token category
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the group ID of this style book token category.
	 *
	 * @return the group ID of this style book token category
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the modified date of this style book token category.
	 *
	 * @return the modified date of this style book token category
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this style book token category.
	 *
	 * @return the mvcc version of this style book token category
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this style book token category.
	 *
	 * @return the name of this style book token category
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the primary key of this style book token category.
	 *
	 * @return the primary key of this style book token category
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the style book entry ID of this style book token category.
	 *
	 * @return the style book entry ID of this style book token category
	 */
	@Override
	public long getStyleBookEntryId() {
		return model.getStyleBookEntryId();
	}

	/**
	 * Returns the style book token category ID of this style book token category.
	 *
	 * @return the style book token category ID of this style book token category
	 */
	@Override
	public long getStyleBookTokenCategoryId() {
		return model.getStyleBookTokenCategoryId();
	}

	/**
	 * Returns the theme frontend token definition ID of this style book token category.
	 *
	 * @return the theme frontend token definition ID of this style book token category
	 */
	@Override
	public String getThemeFrontendTokenDefinitionId() {
		return model.getThemeFrontendTokenDefinitionId();
	}

	/**
	 * Returns the user ID of this style book token category.
	 *
	 * @return the user ID of this style book token category
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this style book token category.
	 *
	 * @return the user name of this style book token category
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this style book token category.
	 *
	 * @return the user uuid of this style book token category
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this style book token category.
	 *
	 * @param companyId the company ID of this style book token category
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this style book token category.
	 *
	 * @param createDate the create date of this style book token category
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this style book token category.
	 *
	 * @param ctCollectionId the ct collection ID of this style book token category
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the description of this style book token category.
	 *
	 * @param description the description of this style book token category
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the group ID of this style book token category.
	 *
	 * @param groupId the group ID of this style book token category
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the modified date of this style book token category.
	 *
	 * @param modifiedDate the modified date of this style book token category
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this style book token category.
	 *
	 * @param mvccVersion the mvcc version of this style book token category
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this style book token category.
	 *
	 * @param name the name of this style book token category
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this style book token category.
	 *
	 * @param primaryKey the primary key of this style book token category
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the style book entry ID of this style book token category.
	 *
	 * @param styleBookEntryId the style book entry ID of this style book token category
	 */
	@Override
	public void setStyleBookEntryId(long styleBookEntryId) {
		model.setStyleBookEntryId(styleBookEntryId);
	}

	/**
	 * Sets the style book token category ID of this style book token category.
	 *
	 * @param styleBookTokenCategoryId the style book token category ID of this style book token category
	 */
	@Override
	public void setStyleBookTokenCategoryId(long styleBookTokenCategoryId) {
		model.setStyleBookTokenCategoryId(styleBookTokenCategoryId);
	}

	/**
	 * Sets the theme frontend token definition ID of this style book token category.
	 *
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID of this style book token category
	 */
	@Override
	public void setThemeFrontendTokenDefinitionId(
		String themeFrontendTokenDefinitionId) {

		model.setThemeFrontendTokenDefinitionId(themeFrontendTokenDefinitionId);
	}

	/**
	 * Sets the user ID of this style book token category.
	 *
	 * @param userId the user ID of this style book token category
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this style book token category.
	 *
	 * @param userName the user name of this style book token category
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this style book token category.
	 *
	 * @param userUuid the user uuid of this style book token category
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public Map<String, Function<StyleBookTokenCategory, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<StyleBookTokenCategory, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	protected StyleBookTokenCategoryWrapper wrap(
		StyleBookTokenCategory styleBookTokenCategory) {

		return new StyleBookTokenCategoryWrapper(styleBookTokenCategory);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1857618647