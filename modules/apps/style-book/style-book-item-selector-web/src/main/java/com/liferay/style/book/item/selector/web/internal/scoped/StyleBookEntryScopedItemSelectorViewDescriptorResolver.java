/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.StyleBookEntryEntriesItemSelectorViewDescriptor;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.StyleBookEntryGroupsItemSelectorViewDescriptor;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.StyleBookEntryScopesItemSelectorViewDescriptor;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryScopedItemSelectorViewDescriptorResolver {

	public StyleBookEntryScopedItemSelectorViewDescriptorResolver(
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		GroupLocalService groupLocalService,
		SiteConnectedGroupGroupProvider siteConnectedGroupGroupProvider) {

		_frontendTokenDefinitionRegistry = frontendTokenDefinitionRegistry;
		_groupLocalService = groupLocalService;
		_siteConnectedGroupGroupProvider = siteConnectedGroupGroupProvider;
	}

	public ItemSelectorViewDescriptor<?> resolve(
		HttpServletRequest httpServletRequest, Layout layout,
		PortletURL portletURL, String selectedStyleBookEntryERC,
		String selectedStyleBookEntryScopeERC) {

		CurrentLevel currentLevel = _getCurrentLevel(
			httpServletRequest, layout, selectedStyleBookEntryScopeERC);

		if (currentLevel.getLevel() == Level.SCOPES) {
			return new StyleBookEntryScopesItemSelectorViewDescriptor(
				httpServletRequest, portletURL);
		}

		if (currentLevel.getLevel() == Level.GROUPS) {
			return new StyleBookEntryGroupsItemSelectorViewDescriptor(
				_groupLocalService, httpServletRequest, layout.getGroupId(),
				portletURL, currentLevel.getScope(),
				_siteConnectedGroupGroupProvider);
		}

		return new StyleBookEntryEntriesItemSelectorViewDescriptor(
			_frontendTokenDefinitionRegistry, currentLevel.getGroupId(),
			_groupLocalService, httpServletRequest, portletURL,
			currentLevel.getScope(), layout, selectedStyleBookEntryERC,
			selectedStyleBookEntryScopeERC);
	}

	private CurrentLevel _getCurrentLevel(
		HttpServletRequest httpServletRequest, Layout layout,
		String selectedStyleBookEntryScopeERC) {

		CurrentLevel layoutCurrentLevel = _getCurrentLevel(
			layout, selectedStyleBookEntryScopeERC);

		Level level = Level.fromKey(
			ParamUtil.getString(
				httpServletRequest, "level",
				layoutCurrentLevel.getLevel(
				).getKey()));
		Scope scope = Scope.fromKey(
			ParamUtil.getString(
				httpServletRequest, "scope",
				layoutCurrentLevel.getScope(
				).getKey()));

		if ((level == null) || (scope == null)) {
			return layoutCurrentLevel;
		}

		long groupId = ParamUtil.getLong(
			httpServletRequest, "groupId", layoutCurrentLevel.getGroupId());

		if ((level == Level.ENTRIES) &&
			(groupId != layoutCurrentLevel.getGroupId()) &&
			!_isConnectedGroup(groupId, layout)) {

			return layoutCurrentLevel;
		}

		return new CurrentLevel(groupId, level, scope);
	}

	private CurrentLevel _getCurrentLevel(
		Layout layout, String selectedStyleBookEntryScopeERC) {

		if (!FeatureFlagManagerUtil.isEnabled(
				layout.getCompanyId(), "LPD-57283") ||
			Validator.isNull(selectedStyleBookEntryScopeERC)) {

			return CurrentLevel.siteEntries(layout.getGroupId());
		}

		Group scopeGroup = _groupLocalService.fetchGroupByExternalReferenceCode(
			selectedStyleBookEntryScopeERC, layout.getCompanyId());

		if ((scopeGroup == null) ||
			!_isConnectedGroup(scopeGroup.getGroupId(), layout)) {

			return CurrentLevel.scopes();
		}

		return CurrentLevel.designLibraryEntries(scopeGroup.getGroupId());
	}

	private boolean _isConnectedGroup(long groupId, Layout layout) {
		try {
			return ArrayUtil.contains(
				_siteConnectedGroupGroupProvider.
					getCurrentAndAncestorSiteAndDepotGroupIds(
						layout.getGroupId()),
				groupId);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookEntryScopedItemSelectorViewDescriptorResolver.class);

	private final FrontendTokenDefinitionRegistry
		_frontendTokenDefinitionRegistry;
	private final GroupLocalService _groupLocalService;
	private final SiteConnectedGroupGroupProvider
		_siteConnectedGroupGroupProvider;

	private static class CurrentLevel {

		public static CurrentLevel designLibraryEntries(long groupId) {
			return new CurrentLevel(
				groupId, Level.ENTRIES, Scope.DESIGN_LIBRARY);
		}

		public static CurrentLevel scopes() {
			return new CurrentLevel(0L, Level.SCOPES, Scope.SITE);
		}

		public static CurrentLevel siteEntries(long groupId) {
			return new CurrentLevel(groupId, Level.ENTRIES, Scope.SITE);
		}

		public long getGroupId() {
			return _groupId;
		}

		public Level getLevel() {
			return _level;
		}

		public Scope getScope() {
			return _scope;
		}

		private CurrentLevel(long groupId, Level level, Scope scope) {
			_groupId = groupId;
			_level = level;
			_scope = scope;
		}

		private final long _groupId;
		private final Level _level;
		private final Scope _scope;

	}

}