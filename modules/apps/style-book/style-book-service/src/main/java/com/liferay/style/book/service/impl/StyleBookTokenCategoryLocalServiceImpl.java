/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.impl;

import com.liferay.frontend.token.definition.FrontendTokenCategory;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.exception.DuplicateStyleBookTokenCategoryNameException;
import com.liferay.style.book.exception.StyleBookTokenCategoryNameException;
import com.liferay.style.book.model.StyleBookTokenCategory;
import com.liferay.style.book.service.base.StyleBookTokenCategoryLocalServiceBaseImpl;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.style.book.model.StyleBookTokenCategory",
	service = AopService.class
)
public class StyleBookTokenCategoryLocalServiceImpl
	extends StyleBookTokenCategoryLocalServiceBaseImpl {

	@Override
	public StyleBookTokenCategory addStyleBookTokenCategory(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		User user = _userLocalService.getUser(serviceContext.getUserId());

		_validate(
			user.getCompanyId(), styleBookEntryId,
			themeFrontendTokenDefinitionId, name);

		long styleBookTokenCategoryId = counterLocalService.increment(
			StyleBookTokenCategory.class.getName());

		StyleBookTokenCategory styleBookTokenCategory =
			styleBookTokenCategoryPersistence.create(styleBookTokenCategoryId);

		styleBookTokenCategory.setGroupId(serviceContext.getScopeGroupId());
		styleBookTokenCategory.setCompanyId(user.getCompanyId());
		styleBookTokenCategory.setUserId(user.getUserId());
		styleBookTokenCategory.setUserName(user.getFullName());
		styleBookTokenCategory.setCreateDate(
			serviceContext.getCreateDate(new Date()));
		styleBookTokenCategory.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		styleBookTokenCategory.setStyleBookEntryId(styleBookEntryId);
		styleBookTokenCategory.setThemeFrontendTokenDefinitionId(
			themeFrontendTokenDefinitionId);
		styleBookTokenCategory.setName(name);
		styleBookTokenCategory.setDescription(description);

		return styleBookTokenCategoryPersistence.update(styleBookTokenCategory);
	}

	private void _validate(
			long companyId, long styleBookEntryId,
			String themeFrontendTokenDefinitionId, String name)
		throws PortalException {

		if (Validator.isNull(name)) {
			throw new StyleBookTokenCategoryNameException("Name is required");
		}

		int nameMaxLength = ModelHintsUtil.getMaxLength(
			StyleBookTokenCategory.class.getName(), "name");

		if (name.length() > nameMaxLength) {
			throw new StyleBookTokenCategoryNameException(
				"Maximum length of name exceeded");
		}

		StyleBookTokenCategory styleBookTokenCategory =
			styleBookTokenCategoryPersistence.fetchBySBEI_TFTDI_N(
				styleBookEntryId, themeFrontendTokenDefinitionId, name);

		if (styleBookTokenCategory != null) {
			throw new DuplicateStyleBookTokenCategoryNameException(
				"Style book token category name \"" + name +
					"\" already exists");
		}

		FrontendTokenDefinition frontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				companyId, themeFrontendTokenDefinitionId);

		if (frontendTokenDefinition == null) {
			return;
		}

		for (FrontendTokenCategory frontendTokenCategory :
				frontendTokenDefinition.getFrontendTokenCategories()) {

			JSONObject jsonObject = frontendTokenCategory.getJSONObject(
				LocaleUtil.getSiteDefault());

			if (name.equals(jsonObject.getString("name"))) {
				throw new DuplicateStyleBookTokenCategoryNameException(
					"Style book token category name \"" + name +
						"\" already exists");
			}
		}
	}

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private UserLocalService _userLocalService;

}