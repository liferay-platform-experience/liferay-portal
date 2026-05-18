/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.style.book.item.selector.web.internal.scoped.Scope;
import com.liferay.style.book.item.selector.web.internal.scoped.frontend.taglib.clay.servlet.taglib.StyleBookEntryScopeVerticalCard;

import jakarta.portlet.RenderRequest;

import java.util.Locale;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryScopeItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public StyleBookEntryScopeItemDescriptor(String href, Scope scope) {
		_href = href;
		_scope = scope;
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
		return LanguageUtil.get(locale, _scope.getLabel());
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new StyleBookEntryScopeVerticalCard(
			_href, _scope, getTitle(renderRequest.getLocale()));
	}

	private final String _href;
	private final Scope _scope;

}