/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped;

import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.item.selector.ItemSelectorReturnType;
import com.liferay.item.selector.ItemSelectorView;
import com.liferay.item.selector.ItemSelectorViewDescriptorRenderer;
import com.liferay.item.selector.criteria.AssetEntryItemSelectorReturnType;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.style.book.item.selector.StyleBookEntryScopedItemSelectorCriterion;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Lima
 */
@Component(service = ItemSelectorView.class)
public class StyleBookEntryScopedItemSelectorView
	implements ItemSelectorView<StyleBookEntryScopedItemSelectorCriterion> {

	@Override
	public Class<StyleBookEntryScopedItemSelectorCriterion>
		getItemSelectorCriterionClass() {

		return StyleBookEntryScopedItemSelectorCriterion.class;
	}

	@Override
	public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
		return _supportedItemSelectorReturnTypes;
	}

	@Override
	public String getTitle(Locale locale) {
		return _language.get(locale, "style-books");
	}

	@Override
	public void renderHTML(
			ServletRequest servletRequest, ServletResponse servletResponse,
			StyleBookEntryScopedItemSelectorCriterion
				styleBookEntryScopedItemSelectorCriterion,
			PortletURL portletURL, String itemSelectedEventName, boolean search)
		throws IOException, ServletException {

		Layout layout = _layoutLocalService.fetchLayout(
			styleBookEntryScopedItemSelectorCriterion.getSelPlid());

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		HttpServletRequest originalHttpServletRequest =
			PortalUtil.getOriginalServletRequest(httpServletRequest);

		String selectedStyleBookEntryERC = ParamUtil.getString(
			httpServletRequest, "selectedStyleBookEntryERC",
			ParamUtil.getString(
				originalHttpServletRequest, "selectedStyleBookEntryERC",
				layout.getStyleBookEntryERC()));

		String selectedStyleBookEntryScopeERC = ParamUtil.getString(
			httpServletRequest, "selectedStyleBookEntryScopeERC",
			ParamUtil.getString(
				originalHttpServletRequest, "selectedStyleBookEntryScopeERC",
				layout.getStyleBookEntryScopeERC()));

		portletURL.setParameter(
			"selectedStyleBookEntryERC", selectedStyleBookEntryERC);
		portletURL.setParameter(
			"selectedStyleBookEntryScopeERC", selectedStyleBookEntryScopeERC);

		StyleBookEntryScopedItemSelectorViewDescriptorResolver
			styleBookEntryScopedItemSelectorViewDescriptorResolver =
				new StyleBookEntryScopedItemSelectorViewDescriptorResolver(
					_frontendTokenDefinitionRegistry, _groupLocalService);

		_itemSelectorViewDescriptorRenderer.renderHTML(
			servletRequest, servletResponse,
			styleBookEntryScopedItemSelectorCriterion, portletURL,
			itemSelectedEventName, search,
			styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL,
				selectedStyleBookEntryERC, selectedStyleBookEntryScopeERC));
	}

	private static final List<ItemSelectorReturnType>
		_supportedItemSelectorReturnTypes = Collections.singletonList(
			new AssetEntryItemSelectorReturnType());

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelectorViewDescriptorRenderer
		<StyleBookEntryScopedItemSelectorCriterion>
			_itemSelectorViewDescriptorRenderer;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

}