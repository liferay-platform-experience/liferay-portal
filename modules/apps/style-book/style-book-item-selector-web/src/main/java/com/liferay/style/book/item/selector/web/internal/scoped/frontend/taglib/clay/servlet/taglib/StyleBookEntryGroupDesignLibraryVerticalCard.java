/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.style.book.item.selector.web.internal.scoped.util.GroupNameUtil;

import java.util.Map;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryGroupDesignLibraryVerticalCard
	implements VerticalCard {

	public StyleBookEntryGroupDesignLibraryVerticalCard(
		Group group, String href, ThemeDisplay themeDisplay) {

		_group = group;
		_href = href;
		_themeDisplay = themeDisplay;
	}

	@Override
	public String getCssClass() {
		return "card-interactive card-interactive-secondary " +
			"style-book-scoped-card-group-design-library";
	}

	@Override
	public Map<String, String> getDynamicAttributes() {
		return HashMapBuilder.put(
			"data-href", _href
		).put(
			"role", "button"
		).put(
			"tabIndex", "0"
		).build();
	}

	@Override
	public String getIcon() {
		return "books";
	}

	@Override
	public String getTitle() {
		return GroupNameUtil.getName(_group, _themeDisplay.getLocale());
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final Group _group;
	private final String _href;
	private final ThemeDisplay _themeDisplay;

}