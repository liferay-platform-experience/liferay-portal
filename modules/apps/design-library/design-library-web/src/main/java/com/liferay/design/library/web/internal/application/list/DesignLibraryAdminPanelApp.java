/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.design.library.constants.DesignLibraryAdminPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mario Leandro
 */
@Component(
	property = {
		"panel.app.order:Integer=1200",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS
	},
	service = PanelApp.class
)
public class DesignLibraryAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "books-brush";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M17%203.52954C18.6568%203.52954%2020%204.89809%2020",
			"%206.58628V34.0719C20%2036.0328%2017.2134%2037.127%2015.6094",
			"%2036.0436C14.0098%2034.9632%2012.079%2034.0969%2010",
			"%2034.0969C9.30449%2034.0969%208.64538%2034.1939%208.02205",
			"%2034.3634C2.68594%2035.8143%200%2035.6486%200",
			"%2030.0213V11.6808C0%207.179%203.58172%203.52954%208",
			"%203.52954H17Z%22%20fill%3D%22%23B3CDFF%22%2F%3E%3Cpath%20d%3D",
			"%22M23%203.52954C21.3432%203.52954%2020%204.89809%2020",
			"%206.58628V34.0719C20%2036.0328%2022.7866%2037.127%2024.3906",
			"%2036.0436C25.9902%2034.9632%2027.921%2034.0969%2030",
			"%2034.0969C30.6955%2034.0969%2031.3546%2034.1939%2031.978",
			"%2034.3634C37.3141%2035.8143%2040%2035.6486%2040",
			"%2030.0213V11.6808C40%207.179%2036.4183%203.52954%2032",
			"%203.52954H23Z%22%20fill%3D%22%2380ACFF%22%2F%3E%3Cpath%20d%3D",
			"%22M18.5%207.09573C18.5%206.25164%2019.1716%205.56737%2020",
			"%205.56737C20.8284%205.56737%2021.5%206.25164%2021.5",
			"%207.09574V33.078C21.5%2033.9221%2020.8284%2034.6064%2020",
			"%2034.6064C19.1716%2034.6064%2018.5%2033.9221%2018.5",
			"%2033.078V7.09573Z%22%20fill%3D%22%230B5FFF%22%2F%3E%3Cpath%20d",
			"%3D%22M15.5%2020.5963C15.5%2021.7218%2014.6781%2022.297%2013.681",
			"%2021.8127C12.718%2021.3448%2011.4211%2020.8511%209.75",
			"%2020.8511C8.02755%2020.8511%206.77618%2021.3756%205.83203",
			"%2021.8557C4.8437%2022.3583%204%2021.7218%204",
			"%2020.5963V10.6619C4%208.97374%205.34315%207.60519%207",
			"%207.60519H12.5C14.1569%207.60519%2015.5%208.97374%2015.5",
			"%2010.6619V20.5963Z%22%20fill%3D%22%230B5FFF%22%2F%3E%3Cpath%20d",
			"%3D%22M24%2018.743C24%2018.5537%2024.1046%2018.3786%2024.2702",
			"%2018.2928C28.6404%2016.0265%2031.2478%2016.5239%2035.684",
			"%2018.3073C35.8743%2018.3838%2036%2018.5718%2036",
			"%2018.7802V21.1027C36%2021.467%2035.6341%2021.7129%2035.3026",
			"%2021.5761C31.2685%2019.9108%2028.7299%2019.5314%2024.7414",
			"%2021.4884C24.4023%2021.6548%2024%2021.4088%2024",
			"%2021.0252V18.743Z%22%20fill%3D%22%23004AD7%22%2F%3E%3Cpath%20d",
			"%3D%22M24%2024.8564C24%2024.6672%2024.1046%2024.4921%2024.2702",
			"%2024.4063C28.6404%2022.14%2031.2478%2022.6374%2035.684",
			"%2024.4208C35.8743%2024.4973%2036%2024.6853%2036",
			"%2024.8937V27.2162C36%2027.5805%2035.6341%2027.8264%2035.3026",
			"%2027.6896C31.2685%2026.0243%2028.7299%2025.6448%2024.7414",
			"%2027.6018C24.4023%2027.7683%2024%2027.5223%2024",
			"%2027.1387V24.8564Z%22%20fill%3D%22%23004AD7%22%2F%3E%3C%2Fsvg",
			"%3E");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return DesignLibraryAdminPortletKeys.DESIGN_LIBRARY_ADMIN;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-57283")) {

			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + DesignLibraryAdminPortletKeys.DESIGN_LIBRARY_ADMIN + ")"
	)
	private Portlet _portlet;

}