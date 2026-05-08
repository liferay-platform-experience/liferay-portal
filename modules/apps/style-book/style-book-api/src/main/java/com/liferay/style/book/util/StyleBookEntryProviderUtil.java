/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryServiceUtil;

import java.util.List;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryProviderUtil {

	public static List<StyleBookEntry> getStyleBookEntries(
			long companyId, long groupId)
		throws PortalException {

		SiteConnectedGroupGroupProvider siteConnectedGroupGroupProvider =
			_siteConnectedGroupGroupProviderSnapshot.get();

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283") ||
			(siteConnectedGroupGroupProvider == null)) {

			return StyleBookEntryServiceUtil.getStyleBookEntries(
				groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
		}

		return StyleBookEntryServiceUtil.getStyleBookEntries(
			siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(groupId),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	private static final Snapshot<SiteConnectedGroupGroupProvider>
		_siteConnectedGroupGroupProviderSnapshot = new Snapshot<>(
			StyleBookEntryProviderUtil.class,
			SiteConnectedGroupGroupProvider.class);

}