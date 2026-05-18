/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.breadcrumb;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.style.book.item.selector.web.internal.scoped.Level;
import com.liferay.style.book.item.selector.web.internal.scoped.Scope;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryGroupsBreadcrumbEntry extends BreadcrumbEntry {

	public StyleBookEntryGroupsBreadcrumbEntry(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		Scope scope) {

		setTitle(LanguageUtil.get(httpServletRequest, scope.getLabel()));

		if (portletURL != null) {
			setURL(
				PortletURLBuilder.create(
					portletURL
				).setParameter(
					"level", Level.GROUPS.getKey()
				).setParameter(
					"scope", scope.getKey()
				).buildString());
		}
	}

}