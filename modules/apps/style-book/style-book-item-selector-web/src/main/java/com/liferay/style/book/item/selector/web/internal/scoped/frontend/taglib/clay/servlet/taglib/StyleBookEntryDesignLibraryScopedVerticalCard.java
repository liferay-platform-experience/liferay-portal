/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.frontend.taglib.clay.servlet.taglib;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.style.book.model.StyleBookEntry;

import jakarta.portlet.RenderRequest;

import java.util.Objects;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryDesignLibraryScopedVerticalCard
	extends BaseStyleBookEntryVerticalCard {

	public StyleBookEntryDesignLibraryScopedVerticalCard(
		RenderRequest renderRequest, Group scopeGroup, Layout selLayout,
		StyleBookEntry styleBookEntry) {

		super(renderRequest, styleBookEntry);

		_scopeGroup = scopeGroup;
		_selLayout = selLayout;
	}

	@Override
	public String getCssClass() {
		return super.getCssClass() +
			" style-book-scoped-card-entry-design-library";
	}

	@Override
	public String getIcon() {
		return "book";
	}

	@Override
	public String getTitle() {
		return styleBookEntry.getName();
	}

	@Override
	public boolean isSelected() {
		if (!Objects.equals(
				_selLayout.getStyleBookEntryScopeERC(),
				_scopeGroup.getExternalReferenceCode())) {

			return false;
		}

		return Objects.equals(
			_selLayout.getStyleBookEntryERC(),
			styleBookEntry.getExternalReferenceCode());
	}

	private final Group _scopeGroup;
	private final Layout _selLayout;

}