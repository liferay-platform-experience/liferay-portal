/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = {
		"panel.app.order:Integer=1300",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS
	},
	service = PanelApp.class
)
public class LaunchPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "rocket";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2241%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2041%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M7.67621%2011.1841C8.93919%209.9274%2010.771%209.41996",
			"%2012.5049%209.84646L24.7669%2012.8626C25.6188%2013.0721",
			"%2025.9182%2014.1243%2025.3031%2014.7472L16.0175",
			"%2024.1489C15.611%2024.5605%2014.9569%2024.6017%2014.491",
			"%2024.2581C13.1906%2023.2992%2010.5705%2021.477%208.45042",
			"%2020.6998C6.10091%2019.8385%202.59912%2019.6129%200.673907",
			"%2019.5556C0.165642%2019.5404%20-0.0924741%2018.9141%200.267084",
			"%2018.5563L7.67621%2011.1841Z%22%20fill%3D%22%230B5FFF%22%2F%3E",
			"%3Cpath%20d%3D%22M29.0598%2032.4611C30.3228%2031.2045%2030.8328",
			"%2029.3818%2030.4041%2027.6565L27.3729%2015.4556C27.1623",
			"%2014.6079%2026.1048%2014.31%2025.4789%2014.9221L16.03",
			"%2024.1614C15.6164%2024.5659%2015.575%2025.2167%2015.9203",
			"%2025.6803C16.8841%2026.9742%2018.7154%2029.5813%2019.4965",
			"%2031.6908C20.3621%2034.0286%2020.5888%2037.5129%2020.6464",
			"%2039.4286C20.6616%2039.9343%2021.2911%2040.1911%2021.6507",
			"%2039.8334L29.0598%2032.4611Z%22%20fill%3D%22%230053F0%22%2F%3E",
			"%3Cpath%20d%3D%22M39.5307%200.56611C40.8926%201.92128%2040.2428",
			"%2013.4093%2032.7209%2020.8937C27.5163%2026.0724%2021.5338",
			"%2029.1197%2017.0958%2029.2664L16.4975%2029.8618C15.9444",
			"%2030.4121%2015.0477%2030.4121%2014.4946%2029.8618L10.2886",
			"%2025.6767C9.6249%2025.0163%209.6249%2023.9456%2010.2886",
			"%2023.2852L10.6866%2022.8891C10.8341%2018.4733%2013.8967",
			"%2012.5207%2019.1013%207.34198C26.6232%20-0.142437%2038.1687",
			"%20-0.789064%2039.5307%200.56611Z%22%20fill%3D%22%23B3CDFF%22%2F",
			"%3E%3Cpath%20d%3D%22M39.5307%200.56611C40.4827%201.51336",
			"%2040.4511%207.41107%2037.7774%2013.432C34.9283%2013.3945",
			"%2032.0907%2012.2951%2029.9168%2010.132C27.7429%207.96896",
			"%2026.6381%205.14554%2026.6003%202.31068C32.6514%20-0.349721",
			"%2038.5787%20-0.381139%2039.5307%200.56611Z%22%20fill%3D%22",
			"%230B5FFF%22%2F%3E%3Cpath%20d%3D%22M26.7122%2018.1037C25.3848",
			"%2019.4244%2023.2327%2019.4244%2021.9053%2018.1037C20.5779",
			"%2016.7829%2020.5779%2014.6415%2021.9053%2013.3207C23.2327",
			"%2011.9999%2025.3848%2011.9999%2026.7122%2013.3207C28.0396",
			"%2014.6415%2028.0396%2016.7829%2026.7122%2018.1037Z%22%20fill%3D",
			"%22%230B5FFF%22%2F%3E%3Cpath%20d%3D%22M9.45617%2036.3989C6.90957",
			"%2038.9328%202.36715%2038.5213%201.87628%2038.0329C1.30172",
			"%2037.4612%200.971853%2033.0247%203.51845%2030.4907C6.06505",
			"%2027.9568%209.45868%2027.2253%2011.0983%2028.8568C12.738",
			"%2030.4882%2012.0028%2033.865%209.45617%2036.3989Z%22%20fill%3D",
			"%22%235791FF%22%2F%3E%3Cpath%20d%3D%22M9.26983%2033.0736C8.27171",
			"%2034.0667%206.45695%2033.8712%206.25775%2033.673C6.02457",
			"%2033.441%205.86205%2031.6691%206.86016%2030.6759C7.85828",
			"%2029.6828%209.20684%2029.4144%209.87225%2030.0765C10.5377",
			"%2030.7386%2010.2679%2032.0804%209.26983%2033.0736Z%22%20fill%3D",
			"%22%2380ACFF%22%2F%3E%3C%2Fsvg%3E");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return LaunchPortletKeys.LAUNCH;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-72278")) {

			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + LaunchPortletKeys.LAUNCH + ")"
	)
	private Portlet _portlet;

}