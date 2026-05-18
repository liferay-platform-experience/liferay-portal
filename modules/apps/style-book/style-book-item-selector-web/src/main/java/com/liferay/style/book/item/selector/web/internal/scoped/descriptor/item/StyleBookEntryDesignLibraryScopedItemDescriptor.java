/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item;

import com.liferay.frontend.taglib.clay.servlet.taglib.VerticalCard;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.style.book.item.selector.web.internal.scoped.frontend.taglib.clay.servlet.taglib.StyleBookEntryDesignLibraryScopedVerticalCard;
import com.liferay.style.book.model.StyleBookEntry;

import jakarta.portlet.RenderRequest;

import java.util.Locale;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryDesignLibraryScopedItemDescriptor
	implements ItemSelectorViewDescriptor.ItemDescriptor {

	public StyleBookEntryDesignLibraryScopedItemDescriptor(
		Group scopeGroup, Layout selLayout, StyleBookEntry styleBookEntry) {

		_scopeGroup = scopeGroup;
		_selLayout = selLayout;
		_styleBookEntry = styleBookEntry;
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
		return JSONUtil.put(
			"externalReferenceCode", _styleBookEntry.getExternalReferenceCode()
		).put(
			"name", _styleBookEntry.getName()
		).put(
			"styleBookEntryId", _styleBookEntry.getStyleBookEntryId()
		).put(
			"styleBookEntryScopeERC", _scopeGroup.getExternalReferenceCode()
		).toString();
	}

	@Override
	public String getSubtitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		return StringPool.BLANK;
	}

	@Override
	public VerticalCard getVerticalCard(
		RenderRequest renderRequest, RowChecker rowChecker) {

		return new StyleBookEntryDesignLibraryScopedVerticalCard(
			renderRequest, _scopeGroup, _selLayout, _styleBookEntry);
	}

	private final Group _scopeGroup;
	private final Layout _selLayout;
	private final StyleBookEntry _styleBookEntry;

}