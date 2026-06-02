/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service;

import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.style.book.model.StyleBookTokenCategory;

/**
 * Provides a wrapper for {@link StyleBookTokenCategoryService}.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookTokenCategoryService
 * @generated
 */
public class StyleBookTokenCategoryServiceWrapper
	implements ServiceWrapper<StyleBookTokenCategoryService>,
			   StyleBookTokenCategoryService {

	public StyleBookTokenCategoryServiceWrapper() {
		this(null);
	}

	public StyleBookTokenCategoryServiceWrapper(
		StyleBookTokenCategoryService styleBookTokenCategoryService) {

		_styleBookTokenCategoryService = styleBookTokenCategoryService;
	}

	@Override
	public StyleBookTokenCategory addStyleBookTokenCategory(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookTokenCategoryService.addStyleBookTokenCategory(
			styleBookEntryId, themeFrontendTokenDefinitionId, name, description,
			serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _styleBookTokenCategoryService.getOSGiServiceIdentifier();
	}

	@Override
	public StyleBookTokenCategoryService getWrappedService() {
		return _styleBookTokenCategoryService;
	}

	@Override
	public void setWrappedService(
		StyleBookTokenCategoryService styleBookTokenCategoryService) {

		_styleBookTokenCategoryService = styleBookTokenCategoryService;
	}

	private StyleBookTokenCategoryService _styleBookTokenCategoryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1848535737