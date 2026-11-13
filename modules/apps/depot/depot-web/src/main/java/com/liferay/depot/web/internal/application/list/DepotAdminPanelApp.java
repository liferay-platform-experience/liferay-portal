/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.depot.constants.DepotPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_IN_MAINTENANCE
	},
	service = PanelApp.class
)
public class DepotAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "books";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M17%203.52942C18.6568%203.52942%2020%204.89797%2020",
			"%206.58616V34.0718C20%2036.0327%2017.2134%2037.1269%2015.6094",
			"%2036.0435C14.0098%2034.9631%2012.079%2034.0968%2010",
			"%2034.0968C9.30449%2034.0968%208.64538%2034.1938%208.02205",
			"%2034.3633C2.68594%2035.8142%200%2035.6485%200",
			"%2030.0212V11.6807C0%207.17888%203.58172%203.52942%208",
			"%203.52942H17Z%22%20fill%3D%22%23B3CDFF%22%2F%3E%3Cpath%20d%3D",
			"%22M23%203.52942C21.3432%203.52942%2020%204.89797%2020",
			"%206.58616V34.0718C20%2036.0327%2022.7866%2037.1269%2024.3906",
			"%2036.0435C25.9902%2034.9631%2027.921%2034.0968%2030",
			"%2034.0968C30.6955%2034.0968%2031.3546%2034.1938%2031.978",
			"%2034.3633C37.3141%2035.8142%2040%2035.6485%2040",
			"%2030.0212V11.6807C40%207.17888%2036.4183%203.52942%2032",
			"%203.52942H23Z%22%20fill%3D%22%2380ACFF%22%2F%3E%3Cpath%20d%3D",
			"%22M18.5%207.09561C18.5%206.25152%2019.1716%205.56724%2020",
			"%205.56724C20.8284%205.56724%2021.5%206.25152%2021.5",
			"%207.09561V33.0779C21.5%2033.922%2020.8284%2034.6063%2020",
			"%2034.6063C19.1716%2034.6063%2018.5%2033.922%2018.5",
			"%2033.0779V7.09561Z%22%20fill%3D%22%230B5FFF%22%2F%3E%3Cpath%20d",
			"%3D%22M15.5%2020.5962C15.5%2021.7217%2014.6781%2022.2969",
			"%2013.681%2021.8125C12.718%2021.3447%2011.4211%2020.8509%209.75",
			"%2020.8509C8.02755%2020.8509%206.77618%2021.3755%205.83203",
			"%2021.8556C4.8437%2022.3582%204%2021.7217%204",
			"%2020.5962V10.6618C4%208.97362%205.34315%207.60507%207",
			"%207.60507H12.5C14.1569%207.60507%2015.5%208.97362%2015.5",
			"%2010.6618V20.5962Z%22%20fill%3D%22%230B5FFF%22%2F%3E%3Cpath%20d",
			"%3D%22M24%2018.7428C24%2018.5536%2024.1046%2018.3785%2024.2702",
			"%2018.2927C28.6404%2016.0264%2031.2478%2016.5238%2035.684",
			"%2018.3072C35.8743%2018.3837%2036%2018.5717%2036",
			"%2018.7801V21.1026C36%2021.4669%2035.6341%2021.7128%2035.3026",
			"%2021.576C31.2685%2019.9107%2028.7299%2019.5312%2024.7414",
			"%2021.4882C24.4023%2021.6547%2024%2021.4087%2024",
			"%2021.0251V18.7428Z%22%20fill%3D%22%23004AD7%22%2F%3E%3Cpath%20d",
			"%3D%22M24%2024.8563C24%2024.6671%2024.1046%2024.492%2024.2702",
			"%2024.4062C28.6404%2022.1399%2031.2478%2022.6372%2035.684",
			"%2024.4207C35.8743%2024.4971%2036%2024.6852%2036",
			"%2024.8935V27.2161C36%2027.5804%2035.6341%2027.8263%2035.3026",
			"%2027.6895C31.2685%2026.0242%2028.7299%2025.6447%2024.7414",
			"%2027.6017C24.4023%2027.7681%2024%2027.5222%2024",
			"%2027.1386V24.8563Z%22%20fill%3D%22%23004AD7%22%2F%3E%3C%2Fsvg",
			"%3E");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return DepotPortletKeys.DEPOT_ADMIN;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest) {
		return _portal.getControlPanelPortletURL(
			httpServletRequest, getGroup(httpServletRequest), getPortletId(), 0,
			0, PortletRequest.RENDER_PHASE);
	}

	@Override
	protected Group getGroup(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return themeDisplay.getControlPanelGroup();
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(jakarta.portlet.name=" + DepotPortletKeys.DEPOT_ADMIN + ")"
	)
	private Portlet _portlet;

}