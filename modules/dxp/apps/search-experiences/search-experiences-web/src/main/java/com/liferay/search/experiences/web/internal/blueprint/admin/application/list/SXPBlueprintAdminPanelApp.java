/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.admin.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.web.application.list.constants.SearchPanelCategoryKeys;
import com.liferay.search.experiences.constants.SXPPortletKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	enabled = false,
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + SearchPanelCategoryKeys.CONTROL_PANEL_SEARCH
	},
	service = PanelApp.class
)
public class SXPBlueprintAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "blue-print";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M24.6154%200V12.3077C24.6154%2014.007%2025.993%2015.3846",
			"%2027.6923%2015.3846H40V33.8462C40%2037.2448%2037.2448%2040",
			"%2033.8462%2040H6.15385C2.75517%2040%200%2037.2448%200",
			"%2033.8462V6.15385C0%202.75517%202.75517%200%206.15385",
			"%200H24.6154Z%22%20fill%3D%22%23B3CDFF%22%2F%3E%3Cpath%20d%3D",
			"%22M26.9231%206.15385C28.1976%206.15385%2029.2308%207.18704",
			"%2029.2308%208.46154C29.2308%209.73604%2028.1976%2010.7692",
			"%2026.9231%2010.7692H8.46154C7.18704%2010.7692%206.15385",
			"%209.73604%206.15385%208.46154C6.15385%207.18704%207.18704",
			"%206.15385%208.46154%206.15385H26.9231Z%22%20fill%3D%22%2380ACFF",
			"%22%2F%3E%3Cpath%20d%3D%22M31.5385%2010.7692C32.813%2010.7692",
			"%2033.8462%2011.8024%2033.8462%2013.0769V31.5385C33.8462",
			"%2032.813%2032.813%2033.8462%2031.5385%2033.8462C30.264",
			"%2033.8462%2029.2308%2032.813%2029.2308",
			"%2031.5385V13.0769C29.2308%2011.8024%2030.264%2010.7692",
			"%2031.5385%2010.7692Z%22%20fill%3D%22%2380ACFF%22%2F%3E%3Cpath",
			"%20d%3D%22M24.6154%200L40%2015.3846H27.6923C25.993%2015.3846",
			"%2024.6154%2014.007%2024.6154%2012.3077V0Z%22%20fill%3D%22",
			"%23004AD7%22%2F%3E%3Cpath%20d%3D%22M6.15385%2019.2308C6.15385",
			"%2017.9563%207.18704%2016.9231%208.46154%2016.9231C9.73604",
			"%2016.9231%2010.7692%2017.9563%2010.7692",
			"%2019.2308V31.5385C10.7692%2032.813%209.73604%2033.8462",
			"%208.46154%2033.8462C7.18704%2033.8462%206.15385%2032.813",
			"%206.15385%2031.5385V19.2308Z%22%20fill%3D%22%2380ACFF%22%2F%3E",
			"%3Cpath%20d%3D%22M20%2019.2308C20%2017.9563%2021.0332%2016.9231",
			"%2022.3077%2016.9231C23.5822%2016.9231%2024.6154%2017.9563",
			"%2024.6154%2019.2308V31.5385C24.6154%2032.813%2023.5822",
			"%2033.8462%2022.3077%2033.8462C21.0332%2033.8462%2020%2032.813",
			"%2020%2031.5385V19.2308Z%22%20fill%3D%22%2380ACFF%22%2F%3E",
			"%3Cpath%20d%3D%22M22.3077%2029.2308C23.5822%2029.2308%2024.6154",
			"%2030.264%2024.6154%2031.5385C24.6154%2032.813%2023.5822",
			"%2033.8462%2022.3077%2033.8462H8.46154C7.18704%2033.8462",
			"%206.15385%2032.813%206.15385%2031.5385C6.15385%2030.264",
			"%207.18704%2029.2308%208.46154%2029.2308H22.3077Z%22%20fill%3D",
			"%22%2380ACFF%22%2F%3E%3Cpath%20d%3D%22M22.3077%2016.9231C23.5822",
			"%2016.9231%2024.6154%2017.9563%2024.6154%2019.2308C24.6154",
			"%2020.5053%2023.5822%2021.5385%2022.3077",
			"%2021.5385H8.46154C7.18704%2021.5385%206.15385%2020.5053",
			"%206.15385%2019.2308C6.15385%2017.9563%207.18704%2016.9231",
			"%208.46154%2016.9231H22.3077Z%22%20fill%3D%22%2380ACFF%22%2F%3E",
			"%3C%2Fsvg%3E");
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
				_language.get(LocaleUtil.ENGLISH, "blueprints"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/sxp_blueprint_admin/view_sxp_blueprints"
				).setTabs1(
					"sxpBlueprints"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "blueprints")),
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "elements"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/sxp_blueprint_admin/view_sxp_elements"
				).setTabs1(
					"sxpElements"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "elements")));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return SXPPortletKeys.SXP_BLUEPRINT_ADMIN;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (Objects.equals(searchEngineInformation.getVendorString(), "Solr")) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference
	protected SearchEngineInformation searchEngineInformation;

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + SXPPortletKeys.SXP_BLUEPRINT_ADMIN + ")"
	)
	private Portlet _portlet;

}