/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.style.book.item.selector.web.internal.scoped.frontend.taglib.clay.servlet.taglib.StyleBookEntryGroupDesignLibraryVerticalCard;
import com.liferay.style.book.item.selector.web.internal.scoped.util.GroupNameUtil;

import jakarta.portlet.RenderRequest;

import java.util.Locale;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryGroupDesignLibraryItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public StyleBookEntryGroupDesignLibraryItemDescriptor(
		Group group, String href, ThemeDisplay themeDisplay) {

		_group = group;
		_href = href;
		_themeDisplay = themeDisplay;
	}

	@Override
	public String getIcon() {
		return null;
	}

	@Override
	public String getImageURL() {
		return null;
	}

	@Override
	public String getPayload() {
		return StringPool.BLANK;
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return GroupNameUtil.getName(_group, locale);
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new StyleBookEntryGroupDesignLibraryVerticalCard(
			_group, _href, _themeDisplay);
	}

	private final Group _group;
	private final String _href;
	private final ThemeDisplay _themeDisplay;

}