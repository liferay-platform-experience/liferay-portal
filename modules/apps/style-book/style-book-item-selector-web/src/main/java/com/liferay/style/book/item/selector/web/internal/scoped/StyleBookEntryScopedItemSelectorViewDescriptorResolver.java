/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.GroupLocalService;
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
		GroupLocalService groupLocalService) {

		_frontendTokenDefinitionRegistry = frontendTokenDefinitionRegistry;
		_groupLocalService = groupLocalService;
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
				_siteConnectedGroupGroupProviderSnapshot.get());
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

		return new CurrentLevel(
			ParamUtil.getLong(
				httpServletRequest, "groupId", layoutCurrentLevel.getGroupId()),
			Level.fromKey(
				ParamUtil.getString(
					httpServletRequest, "level",
					layoutCurrentLevel.getLevel(
					).getKey())),
			Scope.fromKey(
				ParamUtil.getString(
					httpServletRequest, "scope",
					layoutCurrentLevel.getScope(
					).getKey())));
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

		if (scopeGroup == null) {
			return CurrentLevel.scopes();
		}

		return CurrentLevel.designLibraryEntries(scopeGroup.getGroupId());
	}

	private static final Snapshot<SiteConnectedGroupGroupProvider>
		_siteConnectedGroupGroupProviderSnapshot = new Snapshot<>(
			StyleBookEntryScopedItemSelectorViewDescriptorResolver.class,
			SiteConnectedGroupGroupProvider.class);

	private final FrontendTokenDefinitionRegistry
		_frontendTokenDefinitionRegistry;
	private final GroupLocalService _groupLocalService;

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