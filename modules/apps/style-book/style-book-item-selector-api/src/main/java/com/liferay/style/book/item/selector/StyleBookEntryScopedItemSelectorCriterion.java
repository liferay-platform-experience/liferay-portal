/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector;

import com.liferay.item.selector.BaseItemSelectorCriterion;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryScopedItemSelectorCriterion
	extends BaseItemSelectorCriterion {

	public long getSelPlid() {
		return _selPlid;
	}

	public void setSelPlid(long selPlid) {
		_selPlid = selPlid;
	}

	private long _selPlid;

}