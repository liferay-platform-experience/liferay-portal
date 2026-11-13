/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(
	property = {
		"panel.app.order:Integer=200",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_IN_MAINTENANCE
	},
	service = PanelApp.class
)
public class ContentDashboardAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "display-content";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M38.0865%2019.9545C39.1935%2019.9545%2040.1014%2020.8542",
			"%2039.9909%2021.9558C39.664%2025.2133%2038.5429%2028.3522",
			"%2036.7127%2031.0912C34.5101%2034.3876%2031.3794%2036.9569",
			"%2027.7166%2038.4741C24.0537%2039.9913%2020.0233%2040.3883",
			"%2016.1348%2039.6148C12.2464%2038.8414%208.67461%2036.9322",
			"%205.8712%2034.1288C3.06779%2031.3254%201.15864%2027.7536",
			"%200.385178%2023.8652C-0.388282%2019.9767%200.00868539%2015.9462",
			"%201.52588%2012.2834C3.04308%208.62058%205.61236%205.4899",
			"%208.90882%203.28727C11.6478%201.45711%2014.7867%200.335973",
			"%2018.0442%200.00913156C19.1458%20-0.101392%2020.0455%200.806463",
			"%2020.0455%201.91354V17.9499C20.0455%2019.057%2020.943%2019.9545",
			"%2022.0501%2019.9545H38.0865Z%22%20fill%3D%22%2380ACFF%22%2F%3E",
			"%3Cpath%20d%3D%22M36.7409%2015.9454C37.5194%2015.9454%2038.1578",
			"%2015.3127%2038.08%2014.5381C37.9427%2013.1698%2037.6057",
			"%2011.8263%2037.0775%2010.5511C36.3691%208.84096%2035.3308",
			"%207.28704%2034.0219%205.97812C32.713%204.6692%2031.159",
			"%203.63091%2029.4489%202.92253C28.1737%202.39432%2026.8302",
			"%202.05726%2025.4619%201.91997C24.6873%201.84225%2024.0546",
			"%202.48064%2024.0546%203.25913V14.5358C24.0546%2015.3143",
			"%2024.6857%2015.9454%2025.4642%2015.9454H36.7409Z%22%20fill%3D",
			"%22%230B5FFF%22%2F%3E%3C%2Fsvg%3E");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN + ")"
	)
	private Portlet _portlet;

}