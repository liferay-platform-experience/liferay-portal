/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.descriptor;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.item.selector.web.internal.scoped.Level;
import com.liferay.style.book.item.selector.web.internal.scoped.Scope;
import com.liferay.style.book.item.selector.web.internal.scoped.breadcrumb.StyleBookEntryGroupsBreadcrumbEntry;
import com.liferay.style.book.item.selector.web.internal.scoped.breadcrumb.StyleBookEntryScopesBreadcrumbEntry;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item.StyleBookEntryGroupDesignLibraryItemDescriptor;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item.StyleBookEntryGroupSiteItemDescriptor;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryGroupsItemSelectorViewDescriptor
	extends BaseStyleBookEntryItemSelectorViewDescriptor<Group> {

	public StyleBookEntryGroupsItemSelectorViewDescriptor(
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, long layoutGroupId,
		PortletURL portletURL, Scope scope,
		SiteConnectedGroupGroupProvider siteConnectedGroupGroupProvider) {

		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_layoutGroupId = layoutGroupId;
		_portletURL = portletURL;
		_scope = scope;
		_siteConnectedGroupGroupProvider = siteConnectedGroupGroupProvider;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<BreadcrumbEntry> getBreadcrumbEntries(PortletURL portletURL) {
		return Arrays.asList(
			new StyleBookEntryScopesBreadcrumbEntry(
				_httpServletRequest, _portletURL),
			new StyleBookEntryGroupsBreadcrumbEntry(
				_httpServletRequest, null, _scope));
	}

	@Override
	public ItemDescriptor getItemDescriptor(Group group) {
		String href = PortletURLBuilder.create(
			_portletURL
		).setParameter(
			"groupId", group.getGroupId()
		).setParameter(
			"level", Level.ENTRIES.getKey()
		).setParameter(
			"scope", _scope.getKey()
		).buildString();

		if (group.isDepot()) {
			return new StyleBookEntryGroupDesignLibraryItemDescriptor(
				group, href, _themeDisplay);
		}

		return new StyleBookEntryGroupSiteItemDescriptor(
			group, href, _themeDisplay);
	}

	@Override
	public SearchContainer<Group> getSearchContainer() throws PortalException {
		SearchContainer<Group> searchContainer = new SearchContainer<>(
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST),
			_portletURL, null, "there-are-no-groups");

		long[] groupIds =
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(_layoutGroupId);

		List<Group> groups = new ArrayList<>(groupIds.length);

		String keywords = StringUtil.toLowerCase(
			ParamUtil.getString(_httpServletRequest, "keywords"));

		for (long groupId : groupIds) {
			Group group = _groupLocalService.fetchGroup(groupId);

			if ((group == null) ||
				((_scope == Scope.SITE) && !group.isSite()) ||
				((_scope == Scope.DESIGN_LIBRARY) && !group.isDepot())) {

				continue;
			}

			if (!keywords.isEmpty()) {
				String name = StringUtil.toLowerCase(
					group.getDescriptiveName(_themeDisplay.getLocale()));

				if (!name.contains(keywords)) {
					continue;
				}
			}

			groups.add(group);
		}

		searchContainer.setResultsAndTotal(() -> groups, groups.size());

		return searchContainer;
	}

	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final long _layoutGroupId;
	private final PortletURL _portletURL;
	private final Scope _scope;
	private final SiteConnectedGroupGroupProvider
		_siteConnectedGroupGroupProvider;
	private final ThemeDisplay _themeDisplay;

}