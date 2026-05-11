/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalServiceUtil;
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

	public static StyleBookEntry getStyleBookEntry(Layout layout) {
		if (Validator.isNull(layout.getStyleBookEntryERC())) {
			return null;
		}

		String styleBookEntryScopeERC = layout.getStyleBookEntryScopeERC();

		if (Validator.isNull(styleBookEntryScopeERC)) {
			return StyleBookEntryLocalServiceUtil.
				fetchStyleBookEntryByExternalReferenceCode(
					layout.getStyleBookEntryERC(),
					StagingUtil.getLiveGroupId(layout.getGroupId()));
		}

		Group scopeGroup =
			GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				styleBookEntryScopeERC, layout.getCompanyId());

		if (scopeGroup == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to resolve Style Book scope group with ERC ",
						styleBookEntryScopeERC, " for Layout ",
						layout.getPlid(),
						"; falling back to site default Style Book"));
			}

			return null;
		}

		StyleBookEntry styleBookEntry =
			StyleBookEntryLocalServiceUtil.
				fetchStyleBookEntryByExternalReferenceCode(
					layout.getStyleBookEntryERC(),
					StagingUtil.getLiveGroupId(scopeGroup.getGroupId()));

		if ((styleBookEntry == null) && _log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Unable to resolve Style Book entry with ERC ",
					layout.getStyleBookEntryERC(), " in scope group ",
					scopeGroup.getGroupId(), " for Layout ", layout.getPlid(),
					"; falling back to site default Style Book"));
		}

		return styleBookEntry;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookEntryProviderUtil.class);

	private static final Snapshot<SiteConnectedGroupGroupProvider>
		_siteConnectedGroupGroupProviderSnapshot = new Snapshot<>(
			StyleBookEntryProviderUtil.class,
			SiteConnectedGroupGroupProvider.class);

}