/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.application.list;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"panel.app.order:Integer=1100",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS
	},
	service = PanelApp.class
)
public class AnnouncementsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "megaphone";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M7%2021.0026C8.65685%2021.0026%2010%2022.3491%2010",
			"%2024.0101V36.0401C10%2037.7011%208.65685%2039.0476%207",
			"%2039.0476C5.34315%2039.0476%204%2037.7011%204",
			"%2036.0401V35.0376C5.10457%2035.0376%206%2034.14%206%2033.0326C6",
			"%2031.9253%205.10457%2031.0276%204%2031.0276V24.0101C4%2022.3491",
			"%205.34315%2021.0026%207%2021.0026Z%22%20fill%3D%22%230B5FFF%22",
			"%2F%3E%3Cpath%20d%3D%22M40%2035.6598C40%2036.4234%2039.3825",
			"%2037.0424%2038.6209%2037.0424C38.226%2037.0424%2037.8512",
			"%2036.874%2037.5825%2036.584C35.7163%2034.5695%2027.936",
			"%2026.5358%2021.1992%2025.0124C19.2394%2024.5694%2016.7264",
			"%2024.532%2014.5776%2024.6218C12.1508%2024.7231%2010%2022.8392",
			"%2010%2020.4042V15.5204C10%2013.1082%2012.112%2011.2325",
			"%2014.5164%2011.3248C16.6751%2011.4076%2019.2144%2011.377",
			"%2021.1992%2010.9774C27.6564%209.67695%2035.6823%202.97176",
			"%2037.5829%201.32027C37.8533%201.08533%2038.1981%200.952393",
			"%2038.5559%200.952393C39.3535%200.952393%2040%201.60054%2040",
			"%202.40007V35.6598Z%22%20fill%3D%22%235791FF%22%2F%3E%3Cpath%20d",
			"%3D%22M0%2012.9826C0%2010.768%201.79086%208.97264%204",
			"%208.97264H12C13.1046%208.97264%2014%209.87031%2014",
			"%2010.9776V25.0126C14%2026.12%2013.1046%2027.0176%2012",
			"%2027.0176H4C1.79086%2027.0176%200%2025.2223%200",
			"%2023.0076V12.9826Z%22%20fill%3D%22%23B3CDFF%22%2F%3E%3C%2Fsvg",
			"%3E");
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return List.of(
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "announcements"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setNavigation(
					"announcements"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "announcements")),
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "alerts"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setNavigation(
					"alerts"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "alerts")));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN + ")"
	)
	private Portlet _portlet;

}