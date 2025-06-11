/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.display.context;

import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Rachael Koestartyo
 */
public class CookiesPreferenceHandlingConfigurationDisplayContext {

	public CookiesPreferenceHandlingConfigurationDisplayContext(
		CookiesConfigurationProvider cookiesConfigurationProvider,
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse,
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		_cookiesConfigurationProvider = cookiesConfigurationProvider;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_scope = scope;
		_scopePK = scopePK;
	}

	public boolean getCookiesPreferenceHandlingEnabled() {
		return _cookiesConfigurationProvider.isCookiesPreferenceHandlingEnabled(
			_scope, _scopePK);
	}

	public boolean getCookiesPreferenceHandlingExplicitConsentMode() {
		return _cookiesConfigurationProvider.
			isCookiesPreferenceHandlingExplicitConsentMode(_scope, _scopePK);
	}

	public String getEditCookiesPreferenceHandlingConfigurationURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/instance_settings/edit_cookies_preference_handling_configuration"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).setParameter(
			"scope", _scope
		).setParameter(
			"scopePK", _scopePK
		).buildString();
	}

	public boolean isCookiesPreferenceHandlingConfigurationDefined()
		throws Exception {

		return _cookiesConfigurationProvider.
			isCookiesPreferenceHandlingConfigurationDefined(_scope, _scopePK);
	}

	private final CookiesConfigurationProvider _cookiesConfigurationProvider;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ExtendedObjectClassDefinition.Scope _scope;
	private final long _scopePK;

}