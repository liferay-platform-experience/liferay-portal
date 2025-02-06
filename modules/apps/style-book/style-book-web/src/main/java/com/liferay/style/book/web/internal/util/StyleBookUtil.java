/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.util;

import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Evan Thibodeau
 */
public class StyleBookUtil {

	public static String getThemeName(
		CETManager cetManager, long companyId,
		HttpServletRequest httpServletRequest, String themeId) {

		String name = themeId;

		Theme theme = ThemeLocalServiceUtil.fetchTheme(companyId, themeId);

		if (theme != null) {
			return LanguageUtil.format(
				httpServletRequest, "x-theme", theme.getName());
		}

		if (cetManager.isCETAvailable(companyId, themeId)) {
			CET cet = cetManager.getCET(companyId, themeId);

			return LanguageUtil.format(
				httpServletRequest, "x-theme-css-client-extension",
				cet.getName());
		}

		return name;
	}

	public static boolean isThemeInactive(
		CETManager cetManager, long companyId, String themeId) {

		if (cetManager.isCETAvailable(companyId, themeId) ||
			(ThemeLocalServiceUtil.fetchTheme(companyId, themeId) != null)) {

			return false;
		}

		return true;
	}

}