/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.style.book.item.selector.web.internal.scoped.Scope;

import java.util.Map;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryScopeVerticalCard implements VerticalCard {

	public StyleBookEntryScopeVerticalCard(
		String href, Scope scope, String title) {

		_href = href;
		_scope = scope;
		_title = title;
	}

	@Override
	public String getCssClass() {
		return "card-interactive card-interactive-secondary";
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
		if (_scope == Scope.DESIGN_LIBRARY) {
			return "books";
		}

		return "sites";
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	private final String _href;
	private final Scope _scope;
	private final String _title;

}