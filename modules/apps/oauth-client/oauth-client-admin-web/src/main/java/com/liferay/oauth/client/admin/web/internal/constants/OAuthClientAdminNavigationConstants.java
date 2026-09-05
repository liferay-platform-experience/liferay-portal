/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.constants;

import com.liferay.oauth.client.admin.web.internal.navigation.OAuthClientAdminNavigationTab;

import java.util.List;

/**
 * @author Mario Leandro
 */
public class OAuthClientAdminNavigationConstants {

	public static final List<OAuthClientAdminNavigationTab>
		oAuthClientAdminNavigationTabs = List.of(
			new OAuthClientAdminNavigationTab(
				"/oauth_client_admin/view_oauth_client_entries",
				"oauth-clients", false),
			new OAuthClientAdminNavigationTab(
				"/oauth_client_admin/view_oauth_client_as_local_metadata",
				"oauth-client-as-local-metadata", true),
			new OAuthClientAdminNavigationTab(
				"/oauth_client_admin/view_oauth_client_pr_local_metadata",
				"oauth-client-pr-local-metadata", false));

}