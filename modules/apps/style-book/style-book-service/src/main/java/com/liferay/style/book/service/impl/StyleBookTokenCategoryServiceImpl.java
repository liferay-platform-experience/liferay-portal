/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.impl;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.model.StyleBookTokenCategory;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.base.StyleBookTokenCategoryServiceBaseImpl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=stylebook",
		"json.web.service.context.path=StyleBookTokenCategory"
	},
	service = AopService.class
)
public class StyleBookTokenCategoryServiceImpl
	extends StyleBookTokenCategoryServiceBaseImpl {

	@Override
	public StyleBookTokenCategory addStyleBookTokenCategory(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name, String description, ServiceContext serviceContext)
		throws PortalException {

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.getStyleBookEntry(styleBookEntryId);

		_portletResourcePermission.check(
			getPermissionChecker(), styleBookEntry.getGroupId(),
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);

		return styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
			styleBookEntryId, themeFrontendTokenDefinitionId, name, description,
			serviceContext);
	}

	@Reference(
		target = "(resource.name=" + StyleBookConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}