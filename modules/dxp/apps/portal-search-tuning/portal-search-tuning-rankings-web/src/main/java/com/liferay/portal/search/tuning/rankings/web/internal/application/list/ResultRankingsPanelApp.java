/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.search.web.application.list.constants.SearchPanelCategoryKeys;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Filipe Oshiro
 */
@Component(
	property = {
		"panel.app.order:Integer=300",
		"panel.category.key=" + SearchPanelCategoryKeys.CONTROL_PANEL_SEARCH
	},
	service = PanelApp.class
)
public class ResultRankingsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "medal";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M5%205.67175L9.20222%201.48149L18.6572%2010.9096L14.455",
			"%2015.0998L5%205.67175Z%22%20fill%3D%22%230B5FFF%22%2F%3E%3Cpath",
			"%20d%3D%22M35%205.67175L30.7978%201.48149L21.3428",
			"%2010.9096L25.545%2015.0998L35%205.67175Z%22%20fill%3D%22",
			"%230B5FFF%22%2F%3E%3Cpath%20d%3D%22M5%200H35V5.67175H5V0Z%22",
			"%20fill%3D%22%235791FF%22%2F%3E%3Cpath%20d%3D%22M34.7142",
			"%2025.1852C34.7142%2033.3672%2028.0624%2040%2019.8571",
			"%2040C11.6517%2040%205%2033.3672%205%2025.1852C5%2017.0032",
			"%2011.6517%2010.3704%2019.8571%2010.3704C28.0624%2010.3704",
			"%2034.7142%2017.0032%2034.7142%2025.1852Z%22%20fill%3D%22",
			"%23B3CDFF%22%2F%3E%3Cpath%20d%3D%22M18.9425%2016.6192C19.2753",
			"%2015.5977%2020.7247%2015.5977%2021.0575%2016.6192L22.6046",
			"%2021.3671C22.7535%2021.8239%2023.1804%2022.1332%2023.6621",
			"%2022.1332H28.6685C29.7457%2022.1332%2030.1936%2023.5077",
			"%2029.3221%2024.139L25.2718%2027.0734C24.8821%2027.3557%2024.719",
			"%2027.8562%2024.8679%2028.313L26.415%2033.0609C26.7478%2034.0824",
			"%2025.5753%2034.9319%2024.7039%2034.3005L20.6536",
			"%2031.3662C20.2639%2031.0838%2019.7361%2031.0838%2019.3464",
			"%2031.3662L15.2961%2034.3005C14.4247%2034.9319%2013.2522",
			"%2034.0824%2013.585%2033.0609L15.1321%2028.313C15.281%2027.8562",
			"%2015.1179%2027.3557%2014.7282%2027.0734L10.6779",
			"%2024.139C9.80644%2023.5077%2010.2543%2022.1332%2011.3315",
			"%2022.1332H16.3379C16.8196%2022.1332%2017.2465%2021.8239",
			"%2017.3954%2021.3671L18.9425%2016.6192Z%22%20fill%3D%22%230B5FFF",
			"%22%2F%3E%3C%2Fsvg%3E");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ResultRankingsPortletKeys.RESULT_RANKINGS;
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

	@Reference(
		target = "(jakarta.portlet.name=" + ResultRankingsPortletKeys.RESULT_RANKINGS + ")"
	)
	private Portlet _portlet;

}