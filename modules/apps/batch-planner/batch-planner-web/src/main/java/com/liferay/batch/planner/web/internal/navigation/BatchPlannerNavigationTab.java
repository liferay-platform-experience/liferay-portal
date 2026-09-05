/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.navigation;

/**
 * @author Mario Leandro
 */
public class BatchPlannerNavigationTab {

	public BatchPlannerNavigationTab(
		String labelKey, String mvcRenderCommandName, String tabs1Name) {

		_labelKey = labelKey;
		_mvcRenderCommandName = mvcRenderCommandName;
		_tabs1Name = tabs1Name;
	}

	public String getLabelKey() {
		return _labelKey;
	}

	public String getMVCRenderCommandName() {
		return _mvcRenderCommandName;
	}

	public String getTabs1Name() {
		return _tabs1Name;
	}

	private final String _labelKey;
	private final String _mvcRenderCommandName;
	private final String _tabs1Name;

}