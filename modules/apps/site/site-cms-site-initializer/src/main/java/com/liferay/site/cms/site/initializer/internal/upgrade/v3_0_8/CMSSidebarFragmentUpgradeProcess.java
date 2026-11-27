/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_8;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

/**
 * @author Mario Leandro
 */
public class CMSSidebarFragmentUpgradeProcess extends UpgradeProcess {

	public CMSSidebarFragmentUpgradeProcess(
		CompanyLocalService companyLocalService,
		FragmentEntryLinkLocalService fragmentEntryLinkLocalService,
		FragmentEntryLocalService fragmentEntryLocalService,
		GroupLocalService groupLocalService,
		UserLocalService userLocalService) {

		_companyLocalService = companyLocalService;
		_fragmentEntryLinkLocalService = fragmentEntryLinkLocalService;
		_fragmentEntryLocalService = fragmentEntryLocalService;
		_groupLocalService = groupLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(this::_upgradeCompany);
	}

	private String _read(String fragmentEntryKey, String fileName) {
		return StringUtil.read(
			CMSSidebarFragmentUpgradeProcess.class,
			StringBundler.concat(
				"dependencies/", fragmentEntryKey, "/", fileName));
	}

	private void _upgradeCompany(long companyId) throws Exception {
		Group group = _groupLocalService.fetchGroup(
			companyId, GroupConstants.CMS);

		if (group == null) {
			return;
		}

		_upgradeFragmentEntry(group, "sidebar");
		_upgradeFragmentEntry(group, "vertical-navigation");
	}

	private void _upgradeFragmentEntry(Group group, String fragmentEntryKey)
		throws Exception {

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				group.getGroupId(), fragmentEntryKey);

		if (fragmentEntry == null) {
			return;
		}

		long userId = fragmentEntry.getUserId();

		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			userId = _userLocalService.getGuestUserId(group.getCompanyId());
		}

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			userId, fragmentEntry.getFragmentEntryId(),
			fragmentEntry.getFragmentCollectionId(), fragmentEntry.getName(),
			_read(fragmentEntryKey, "index.css"),
			_read(fragmentEntryKey, "index.html"),
			_read(fragmentEntryKey, "index.js"), fragmentEntry.isCacheable(),
			fragmentEntry.getConfiguration(), fragmentEntry.getIcon(),
			fragmentEntry.getPreviewFileEntryId(), fragmentEntry.isReadOnly(),
			fragmentEntry.getTypeOptions(), WorkflowConstants.STATUS_APPROVED);

		String fragmentEntryERC = fragmentEntry.getExternalReferenceCode();

		ActionableDynamicQuery actionableDynamicQuery =
			_fragmentEntryLinkLocalService.getActionableDynamicQuery();

		actionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq(
						"fragmentEntryERC", fragmentEntryERC));
				dynamicQuery.add(
					RestrictionsFactoryUtil.eq("groupId", group.getGroupId()));
			});
		actionableDynamicQuery.setPerformActionMethod(
			(FragmentEntryLink fragmentEntryLink) ->
				_fragmentEntryLinkLocalService.updateLatestChanges(
					fragmentEntryLink.getFragmentEntryLinkId()));

		actionableDynamicQuery.performActions();
	}

	private final CompanyLocalService _companyLocalService;
	private final FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;
	private final FragmentEntryLocalService _fragmentEntryLocalService;
	private final GroupLocalService _groupLocalService;
	private final UserLocalService _userLocalService;

}