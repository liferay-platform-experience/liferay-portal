/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.style.book.model.StyleBookTokenCategory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing StyleBookTokenCategory in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class StyleBookTokenCategoryCacheModel
	implements CacheModel<StyleBookTokenCategory>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof StyleBookTokenCategoryCacheModel)) {
			return false;
		}

		StyleBookTokenCategoryCacheModel styleBookTokenCategoryCacheModel =
			(StyleBookTokenCategoryCacheModel)object;

		if ((styleBookTokenCategoryId ==
				styleBookTokenCategoryCacheModel.styleBookTokenCategoryId) &&
			(mvccVersion == styleBookTokenCategoryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, styleBookTokenCategoryId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(27);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", styleBookTokenCategoryId=");
		sb.append(styleBookTokenCategoryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", styleBookEntryId=");
		sb.append(styleBookEntryId);
		sb.append(", themeFrontendTokenDefinitionId=");
		sb.append(themeFrontendTokenDefinitionId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", description=");
		sb.append(description);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public StyleBookTokenCategory toEntityModel() {
		StyleBookTokenCategoryImpl styleBookTokenCategoryImpl =
			new StyleBookTokenCategoryImpl();

		styleBookTokenCategoryImpl.setMvccVersion(mvccVersion);
		styleBookTokenCategoryImpl.setCtCollectionId(ctCollectionId);
		styleBookTokenCategoryImpl.setStyleBookTokenCategoryId(
			styleBookTokenCategoryId);
		styleBookTokenCategoryImpl.setGroupId(groupId);
		styleBookTokenCategoryImpl.setCompanyId(companyId);
		styleBookTokenCategoryImpl.setUserId(userId);

		if (userName == null) {
			styleBookTokenCategoryImpl.setUserName("");
		}
		else {
			styleBookTokenCategoryImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			styleBookTokenCategoryImpl.setCreateDate(null);
		}
		else {
			styleBookTokenCategoryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			styleBookTokenCategoryImpl.setModifiedDate(null);
		}
		else {
			styleBookTokenCategoryImpl.setModifiedDate(new Date(modifiedDate));
		}

		styleBookTokenCategoryImpl.setStyleBookEntryId(styleBookEntryId);

		if (themeFrontendTokenDefinitionId == null) {
			styleBookTokenCategoryImpl.setThemeFrontendTokenDefinitionId("");
		}
		else {
			styleBookTokenCategoryImpl.setThemeFrontendTokenDefinitionId(
				themeFrontendTokenDefinitionId);
		}

		if (name == null) {
			styleBookTokenCategoryImpl.setName("");
		}
		else {
			styleBookTokenCategoryImpl.setName(name);
		}

		if (description == null) {
			styleBookTokenCategoryImpl.setDescription("");
		}
		else {
			styleBookTokenCategoryImpl.setDescription(description);
		}

		styleBookTokenCategoryImpl.resetOriginalValues();

		return styleBookTokenCategoryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();

		styleBookTokenCategoryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();

		styleBookEntryId = objectInput.readLong();
		themeFrontendTokenDefinitionId = objectInput.readUTF();
		name = objectInput.readUTF();
		description = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		objectOutput.writeLong(styleBookTokenCategoryId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		objectOutput.writeLong(styleBookEntryId);

		if (themeFrontendTokenDefinitionId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(themeFrontendTokenDefinitionId);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (description == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(description);
		}
	}

	public long mvccVersion;
	public long ctCollectionId;
	public long styleBookTokenCategoryId;
	public long groupId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public long styleBookEntryId;
	public String themeFrontendTokenDefinitionId;
	public String name;
	public String description;

}
// LIFERAY-SERVICE-BUILDER-HASH:750919341