/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.frontend.taglib.clay.servlet.taglib;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.model.StyleBookEntry;

import jakarta.portlet.RenderRequest;

import java.util.Map;

/**
 * @author Gabriel Lima
 */
public abstract class BaseStyleBookEntryVerticalCard implements VerticalCard {

	public BaseStyleBookEntryVerticalCard(
		RenderRequest renderRequest, StyleBookEntry styleBookEntry) {

		this.styleBookEntry = styleBookEntry;

		themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	@Override
	public String getCssClass() {
		return "select-style-book-option card-interactive " +
			"card-interactive-secondary";
	}

	@Override
	public Map<String, String> getDynamicAttributes() {
		return HashMapBuilder.put(
			"role", "button"
		).put(
			"tabIndex", "0"
		).build();
	}

	@Override
	public String getImageSrc() {
		return styleBookEntry.getImagePreviewURL(themeDisplay);
	}

	@Override
	public String getStickerCssClass() {
		return "sticker-primary";
	}

	@Override
	public String getStickerIcon() {
		if (isSelected()) {
			return "check-circle";
		}

		return null;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	public abstract boolean isSelected();

	protected final StyleBookEntry styleBookEntry;
	protected final ThemeDisplay themeDisplay;

}