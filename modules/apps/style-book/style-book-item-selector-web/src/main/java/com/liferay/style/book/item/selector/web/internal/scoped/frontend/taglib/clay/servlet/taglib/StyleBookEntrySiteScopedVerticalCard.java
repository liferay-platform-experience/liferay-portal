/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.frontend.taglib.clay.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;

import jakarta.portlet.RenderRequest;

import java.util.Objects;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntrySiteScopedVerticalCard
	extends BaseStyleBookEntryVerticalCard {

	public StyleBookEntrySiteScopedVerticalCard(
		RenderRequest renderRequest, Layout selLayout,
		StyleBookEntry styleBookEntry) {

		super(renderRequest, styleBookEntry);

		_selLayout = selLayout;
	}

	@Override
	public String getIcon() {
		return "magic";
	}

	@Override
	public String getTitle() {
		return DefaultStyleBookEntryUtil.getStyleBookEntryName(
			_selLayout, themeDisplay.getLocale(), styleBookEntry);
	}

	@Override
	public boolean isSelected() {
		if (Validator.isNotNull(_selLayout.getStyleBookEntryScopeERC())) {
			return false;
		}

		String layoutStyleBookEntryERC = _selLayout.getStyleBookEntryERC();

		if (Validator.isNull(layoutStyleBookEntryERC)) {
			if (styleBookEntry.getStyleBookEntryId() == 0) {
				return true;
			}

			return false;
		}

		return Objects.equals(
			layoutStyleBookEntryERC, styleBookEntry.getExternalReferenceCode());
	}

	private final Layout _selLayout;

}