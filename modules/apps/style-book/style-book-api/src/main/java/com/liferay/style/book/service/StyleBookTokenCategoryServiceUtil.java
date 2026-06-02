/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.style.book.model.StyleBookTokenCategory;

/**
 * Provides the remote service utility for StyleBookTokenCategory. This utility wraps
 * <code>com.liferay.style.book.service.impl.StyleBookTokenCategoryServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookTokenCategoryService
 * @generated
 */
public class StyleBookTokenCategoryServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.style.book.service.impl.StyleBookTokenCategoryServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static StyleBookTokenCategory addStyleBookTokenCategory(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addStyleBookTokenCategory(
			styleBookEntryId, themeFrontendTokenDefinitionId, name, description,
			serviceContext);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static StyleBookTokenCategoryService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<StyleBookTokenCategoryService>
		_serviceSnapshot = new Snapshot<>(
			StyleBookTokenCategoryServiceUtil.class,
			StyleBookTokenCategoryService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:1846819669