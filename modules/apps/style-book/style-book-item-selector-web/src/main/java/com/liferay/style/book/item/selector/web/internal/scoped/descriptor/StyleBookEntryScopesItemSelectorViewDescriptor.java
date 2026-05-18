/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.descriptor;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.item.selector.web.internal.scoped.Level;
import com.liferay.style.book.item.selector.web.internal.scoped.Scope;
import com.liferay.style.book.item.selector.web.internal.scoped.breadcrumb.StyleBookEntryScopesBreadcrumbEntry;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item.StyleBookEntryScopeItemDescriptor;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryScopesItemSelectorViewDescriptor
	extends BaseStyleBookEntryItemSelectorViewDescriptor<Scope> {

	public StyleBookEntryScopesItemSelectorViewDescriptor(
		HttpServletRequest httpServletRequest, PortletURL portletURL) {

		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
	}

	@Override
	public List<BreadcrumbEntry> getBreadcrumbEntries(PortletURL portletURL) {
		return Collections.singletonList(
			new StyleBookEntryScopesBreadcrumbEntry(_httpServletRequest, null));
	}

	@Override
	public ItemDescriptor getItemDescriptor(Scope scope) {
		return new StyleBookEntryScopeItemDescriptor(
			_buildLevelURL(scope), scope);
	}

	@Override
	public SearchContainer<Scope> getSearchContainer() {
		SearchContainer<Scope> searchContainer = new SearchContainer<>(
			(PortletRequest)_httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST),
			_portletURL, null, "no-scopes-available");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<Scope> scopes = new ArrayList<>();

		scopes.add(Scope.SITE);

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-57283")) {

			scopes.add(Scope.DESIGN_LIBRARY);
		}

		String keywords = StringUtil.toLowerCase(
			ParamUtil.getString(_httpServletRequest, "keywords"));

		if (!keywords.isEmpty()) {
			scopes.removeIf(
				scope -> {
					String label = StringUtil.toLowerCase(
						LanguageUtil.get(
							_httpServletRequest, scope.getLabel()));

					return !label.contains(keywords);
				});
		}

		searchContainer.setResultsAndTotal(() -> scopes, scopes.size());

		return searchContainer;
	}

	private String _buildLevelURL(Scope scope) {
		return PortletURLBuilder.create(
			_portletURL
		).setParameter(
			"level", Level.GROUPS.getKey()
		).setParameter(
			"scope", scope.getKey()
		).buildString();
	}

	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;

}