/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.util;

import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mario Leandro
 */
public class SamlAdminNavigationUtil {

	public static List<String> getTabs1Names(
		SamlProviderConfigurationHelper samlProviderConfigurationHelper) {

		List<String> tabs1Names = new ArrayList<>();

		tabs1Names.add(_TABS1_GENERAL);

		if (samlProviderConfigurationHelper.isRoleIb()) {
			tabs1Names.add(_TABS1_IDENTITY_PROVIDER);
			tabs1Names.add(_TABS1_SERVICE_PROVIDER_CONNECTIONS);
			tabs1Names.add(_TABS1_SERVICE_PROVIDER);
			tabs1Names.add(_TABS1_IDENTITY_PROVIDER_CONNECTIONS);
		}
		else if (samlProviderConfigurationHelper.isRoleIdp()) {
			tabs1Names.add(_TABS1_IDENTITY_PROVIDER);
			tabs1Names.add(_TABS1_SERVICE_PROVIDER_CONNECTIONS);
		}
		else if (samlProviderConfigurationHelper.isRoleSp()) {
			tabs1Names.add(_TABS1_SERVICE_PROVIDER);
			tabs1Names.add(_TABS1_IDENTITY_PROVIDER_CONNECTIONS);
		}

		return tabs1Names;
	}

	private static final String _TABS1_GENERAL = "general";

	private static final String _TABS1_IDENTITY_PROVIDER = "identity-provider";

	private static final String _TABS1_IDENTITY_PROVIDER_CONNECTIONS =
		"identity-provider-connections";

	private static final String _TABS1_SERVICE_PROVIDER = "service-provider";

	private static final String _TABS1_SERVICE_PROVIDER_CONNECTIONS =
		"service-provider-connections";

}