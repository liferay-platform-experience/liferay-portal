/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.breadcrumb;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.style.book.item.selector.web.internal.scoped.util.GroupNameUtil;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryEntriesBreadcrumbEntry extends BreadcrumbEntry {

	public StyleBookEntryEntriesBreadcrumbEntry(
		Group group, ThemeDisplay themeDisplay) {

		if (group != null) {
			setTitle(GroupNameUtil.getName(group, themeDisplay.getLocale()));
		}
	}

}