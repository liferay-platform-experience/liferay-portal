/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.descriptor;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.item.selector.web.internal.scoped.Scope;
import com.liferay.style.book.item.selector.web.internal.scoped.breadcrumb.StyleBookEntryEntriesBreadcrumbEntry;
import com.liferay.style.book.item.selector.web.internal.scoped.breadcrumb.StyleBookEntryGroupsBreadcrumbEntry;
import com.liferay.style.book.item.selector.web.internal.scoped.breadcrumb.StyleBookEntryScopesBreadcrumbEntry;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item.StyleBookEntryDesignLibraryScopedItemDescriptor;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item.StyleBookEntrySiteScopedItemDescriptor;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;
import com.liferay.style.book.util.StyleBookUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryEntriesItemSelectorViewDescriptor
	extends BaseStyleBookEntryItemSelectorViewDescriptor<StyleBookEntry> {

	public StyleBookEntryEntriesItemSelectorViewDescriptor(
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
		long groupId, GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		Scope scope, Layout selLayout) {

		_frontendTokenDefinitionRegistry = frontendTokenDefinitionRegistry;
		_groupId = groupId;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_scope = scope;
		_selLayout = selLayout;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public List<BreadcrumbEntry> getBreadcrumbEntries(PortletURL portletURL) {
		return Arrays.asList(
			new StyleBookEntryScopesBreadcrumbEntry(
				_httpServletRequest, _portletURL),
			new StyleBookEntryGroupsBreadcrumbEntry(
				_httpServletRequest, _portletURL, _scope),
			new StyleBookEntryEntriesBreadcrumbEntry(
				_groupLocalService.fetchGroup(_groupId), _themeDisplay));
	}

	@Override
	public ItemDescriptor getItemDescriptor(StyleBookEntry styleBookEntry) {
		if (_scope == Scope.DESIGN_LIBRARY) {
			return new StyleBookEntryDesignLibraryScopedItemDescriptor(
				_groupLocalService.fetchGroup(_groupId), _selLayout,
				styleBookEntry);
		}

		return new StyleBookEntrySiteScopedItemDescriptor(
			_selLayout, styleBookEntry);
	}

	@Override
	public String[] getOrderByKeys() {
		return new String[] {"name", "create-date"};
	}

	@Override
	public SearchContainer<StyleBookEntry> getSearchContainer() {
		SearchContainer<StyleBookEntry> searchContainer = new SearchContainer<>(
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST),
			_portletURL, null, "there-are-no-style-books");

		FrontendTokenDefinition frontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				_selLayout);

		if (frontendTokenDefinition == null) {
			searchContainer.setResultsAndTotal(Collections::emptyList, 0);

			return searchContainer;
		}

		List<StyleBookEntry> styleBookEntries = new ArrayList<>();

		if (_scope == Scope.SITE) {
			Collections.addAll(
				styleBookEntries,
				StyleBookUtil.getStyleFromThemeStyleBookEntry(
					_selLayout, _themeDisplay.getLocale()));
		}

		styleBookEntries.addAll(
			StyleBookEntryProviderUtil.getStyleBookEntries(
				_groupId, frontendTokenDefinition.getThemeId()));

		String keywords = StringUtil.toLowerCase(
			ParamUtil.getString(_httpServletRequest, "keywords"));

		if (!keywords.isEmpty()) {
			styleBookEntries.removeIf(
				styleBookEntry -> {
					String name = StringUtil.toLowerCase(
						styleBookEntry.getName());

					return !name.contains(keywords);
				});
		}

		searchContainer.setResultsAndTotal(
			() -> styleBookEntries, styleBookEntries.size());

		return searchContainer;
	}

	private final FrontendTokenDefinitionRegistry
		_frontendTokenDefinitionRegistry;
	private final long _groupId;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final Scope _scope;
	private final Layout _selLayout;
	private final ThemeDisplay _themeDisplay;

}