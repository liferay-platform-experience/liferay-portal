/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.navigation;

/**
 * @author Mario Leandro
 */
public class OAuthClientAdminNavigationTab {

	public OAuthClientAdminNavigationTab(
		String mvcRenderCommandName, String navigation, boolean partialMatch) {

		_mvcRenderCommandName = mvcRenderCommandName;
		_navigation = navigation;
		_partialMatch = partialMatch;
	}

	public String getLabelKey() {
		return _navigation;
	}

	public String getMVCRenderCommandName() {
		return _mvcRenderCommandName;
	}

	public String getNavigation() {
		return _navigation;
	}

	public boolean isActive(String navigation) {
		if (_partialMatch) {
			return navigation.contains(_navigation);
		}

		return navigation.equals(_navigation);
	}

	private final String _mvcRenderCommandName;
	private final String _navigation;
	private final boolean _partialMatch;

}