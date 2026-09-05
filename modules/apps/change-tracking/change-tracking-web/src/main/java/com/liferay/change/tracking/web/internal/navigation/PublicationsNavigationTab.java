/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.navigation;

/**
 * @author Mario Leandro
 */
public class PublicationsNavigationTab {

	public PublicationsNavigationTab(
		String labelKey, String mvcRenderCommandName) {

		_labelKey = labelKey;
		_mvcRenderCommandName = mvcRenderCommandName;
	}

	public String getLabelKey() {
		return _labelKey;
	}

	public String getMVCRenderCommandName() {
		return _mvcRenderCommandName;
	}

	private final String _labelKey;
	private final String _mvcRenderCommandName;

}