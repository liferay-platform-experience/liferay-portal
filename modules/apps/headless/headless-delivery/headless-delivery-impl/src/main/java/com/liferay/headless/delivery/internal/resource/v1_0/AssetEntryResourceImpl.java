/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.AssetHelper;
import com.liferay.headless.delivery.dto.v1_0.AssetEntry;
import com.liferay.headless.delivery.resource.v1_0.AssetEntryResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Arrays;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Luis Ortiz
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/asset-entry.properties",
	scope = ServiceScope.PROTOTYPE, service = AssetEntryResource.class
)
public class AssetEntryResourceImpl extends BaseAssetEntryResourceImpl {

	@Override
	public Page<AssetEntry> getAssetLibraryAssetEntriesPage(
			Long assetLibraryId, String[] className, Long classTypeId,
			Long[] groupIds, String search, Boolean showNonindexable,
			Boolean showScheduled, Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getAssetEntriesPage(
			assetLibraryId, className, classTypeId, groupIds, search,
			showNonindexable, showScheduled, pagination, sorts);
	}

	@Override
	public Page<AssetEntry> getSiteAssetEntriesPage(
			Long siteId, String[] className, Long classTypeId, Long[] groupIds,
			String search, Boolean showNonindexable, Boolean showScheduled,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		return _getAssetEntriesPage(
			siteId, className, classTypeId, groupIds, search, showNonindexable,
			showScheduled, pagination, sorts);
	}

	private Page<AssetEntry> _getAssetEntriesPage(
			Long groupId, String[] classNames, Long classTypeId,
			Long[] groupIds, String search, Boolean showNonindexable,
			Boolean showScheduled, Pagination pagination, Sort[] sorts)
		throws Exception {

		long[] effectiveGroupIds = _toEffectiveGroupIds(groupId, groupIds);

		long[] classNameIds = _toClassNameIds(classNames);

		int[] statuses = _toStatuses(showScheduled);

		Sort sort = ArrayUtil.isEmpty(sorts) ? null : sorts[0];

		Hits hits = _assetEntryLocalService.search(
			contextCompany.getCompanyId(), effectiveGroupIds,
			contextUser.getUserId(), classNameIds,
			(classTypeId == null) ? -1L : classTypeId,
			GetterUtil.getString(search),
			GetterUtil.getBoolean(showNonindexable), statuses,
			pagination.getStartPosition(), pagination.getEndPosition(), sort);

		return Page.of(
			transform(_assetHelper.getAssetEntries(hits), this::_toAssetEntry),
			pagination, hits.getLength());
	}

	private String _getAssetType(
		AssetRendererFactory<?> assetRendererFactory, Locale locale) {

		if (assetRendererFactory == null) {
			return null;
		}

		return assetRendererFactory.getTypeName(locale);
	}

	private String _getGroupDescriptiveName(Group group, Locale locale) {
		if (group == null) {
			return null;
		}

		try {
			return group.getDescriptiveName(locale);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return group.getName(locale);
		}
	}

	private AssetEntry _toAssetEntry(
		com.liferay.asset.kernel.model.AssetEntry assetEntry) {

		Locale locale = contextAcceptLanguage.getPreferredLocale();

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				assetEntry.getClassName());

		Group group = _groupLocalService.fetchGroup(assetEntry.getGroupId());

		return new AssetEntry() {
			{
				setAssetEntryId(assetEntry::getEntryId);
				setAssetType(() -> _getAssetType(assetRendererFactory, locale));
				setClassName(assetEntry::getClassName);
				setClassNameId(assetEntry::getClassNameId);
				setClassPK(assetEntry::getClassPK);
				setDescription(() -> assetEntry.getDescription(locale));
				setGroupDescriptiveName(
					() -> _getGroupDescriptiveName(group, locale));
				setTitle(() -> assetEntry.getTitle(locale));
			}
		};
	}

	private long[] _toClassNameIds(String[] classNames) {
		if (ArrayUtil.isEmpty(classNames)) {
			return AssetRendererFactoryRegistryUtil.getClassNameIds(
				contextCompany.getCompanyId());
		}

		long[] classNameIds = new long[classNames.length];

		for (int i = 0; i < classNames.length; i++) {
			classNameIds[i] = _portal.getClassNameId(classNames[i]);
		}

		return classNameIds;
	}

	private long[] _toEffectiveGroupIds(Long groupId, Long[] groupIds) {
		if (ArrayUtil.isEmpty(groupIds)) {
			return new long[] {groupId};
		}

		return ArrayUtil.toLongArray(Arrays.asList(groupIds));
	}

	private int[] _toStatuses(Boolean showScheduled) {
		if (Boolean.TRUE.equals(showScheduled)) {
			return new int[] {
				WorkflowConstants.STATUS_APPROVED,
				WorkflowConstants.STATUS_SCHEDULED
			};
		}

		return new int[] {WorkflowConstants.STATUS_APPROVED};
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetEntryResourceImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetHelper _assetHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}