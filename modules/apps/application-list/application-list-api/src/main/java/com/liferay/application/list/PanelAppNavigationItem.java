/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list;

/**
 * Represents a navigation item inside an application defined by a {@link
 * PanelApp} implementation, such as a tab or a screen of the application's
 * portlet.
 *
 * @author Marcos Castro
 */
public class PanelAppNavigationItem {

	public PanelAppNavigationItem(String href, String label) {
		_href = href;
		_label = label;
	}

	public String getHref() {
		return _href;
	}

	public String getLabel() {
		return _label;
	}

	private final String _href;
	private final String _label;

}