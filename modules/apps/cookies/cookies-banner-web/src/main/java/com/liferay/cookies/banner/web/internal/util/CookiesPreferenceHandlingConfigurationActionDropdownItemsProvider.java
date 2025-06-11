/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.util;

import com.liferay.configuration.admin.constants.ConfigurationScreenConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Rachael Koestartyo
 */
public class CookiesPreferenceHandlingConfigurationActionDropdownItemsProvider {

	public CookiesPreferenceHandlingConfigurationActionDropdownItemsProvider(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public List<DropdownItem> getActionDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> dropdownGroupItem.setDropdownItems(
				DropdownItemListBuilder.add(
					dropdownItem -> {
						dropdownItem.setHref(
							_httpServletRequest.getAttribute(
								ConfigurationScreenConstants.
									DELETE_CONFIGURATION_URL));
						dropdownItem.setLabel(
							LanguageUtil.get(
								_httpServletRequest, "reset-default-values"));
					}
				).add(
					dropdownItem -> {
						dropdownItem.setHref(
							_httpServletRequest.getAttribute(
								ConfigurationScreenConstants.
									EXPORT_CONFIGURATION_URL));
						dropdownItem.setLabel(
							LanguageUtil.get(_httpServletRequest, "export"));
					}
				).build())
		).build();
	}

	private final HttpServletRequest _httpServletRequest;

}