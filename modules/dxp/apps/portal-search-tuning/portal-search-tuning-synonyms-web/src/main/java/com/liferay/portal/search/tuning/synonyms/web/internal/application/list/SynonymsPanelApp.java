/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.tuning.synonyms.web.internal.constants.SynonymsPortletKeys;
import com.liferay.portal.search.web.application.list.constants.SearchPanelCategoryKeys;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Filipe Oshiro
 */
@Component(
	property = {
		"panel.app.order:Integer=200",
		"panel.category.key=" + SearchPanelCategoryKeys.CONTROL_PANEL_SEARCH
	},
	service = PanelApp.class
)
public class SynonymsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "synonyms";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M16.438%2018.4692C14.3889%2018.7974%2012.462%2019.6619",
			"%2010.6616%2021.062C8.85856%2022.4665%207.88266%2024.3093",
			"%207.74414%2026.5918C8.30757%2026.2657%209.05279%2026.1035",
			"%209.9707%2026.1035C12.372%2026.1035%2014.2295%2026.707",
			"%2015.5371%2027.9126C16.8448%2029.1207%2017.5%2030.7684%2017.5",
			"%2032.8564C17.4998%2034.7498%2016.7385%2036.4128%2015.2197",
			"%2037.8467C13.6984%2039.2825%2011.7005%2040%209.22607",
			"%2040C6.39739%2039.9999%204.15241%2039.0872%202.49268",
			"%2037.2607C0.830629%2035.4341%200.00014775%2033.2157%200",
			"%2030.6055C0%2027.3447%200.546675%2024.5852%201.64307",
			"%2022.334C2.73684%2020.0837%204.09636%2018.2597%205.7251",
			"%2016.8555C7.34973%2015.453%209.11913%2014.4068%2011.0278",
			"%2013.7231C12.9407%2013.0379%2014.741%2012.6316%2016.438",
			"%2012.5V18.4692Z%22%20fill%3D%22%230B5FFF%22%2F%3E%3Cpath%20d%3D",
			"%22M30.7739%200C33.6026%200.000110848%2035.8476%200.912815",
			"%2037.5073%202.73926C39.1694%204.5659%2039.9999%206.78433%2040",
			"%209.39453C40%2012.6553%2039.4533%2015.4148%2038.3569",
			"%2017.666C37.2632%2019.9163%2035.9036%2021.7403%2034.2749",
			"%2023.1445C32.6503%2024.547%2030.8809%2025.5932%2028.9722",
			"%2026.2769C27.0593%2026.9621%2025.259%2027.3684%2023.562",
			"%2027.5V21.5308C25.6111%2021.2026%2027.538%2020.3381%2029.3384",
			"%2018.938C31.1414%2017.5335%2032.1173%2015.6907%2032.2559",
			"%2013.4082C31.6924%2013.7343%2030.9472%2013.8965%2030.0293",
			"%2013.8965C27.628%2013.8965%2025.7705%2013.293%2024.4629",
			"%2012.0874C23.1552%2010.8793%2022.5%209.23155%2022.5",
			"%207.14355C22.5002%205.25022%2023.2615%203.58723%2024.7803",
			"%202.15332C26.3016%200.717546%2028.2995%200%2030.7739%200Z%22",
			"%20fill%3D%22%2380ACFF%22%2F%3E%3C%2Fsvg%3E");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return SynonymsPortletKeys.SYNONYMS;
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
		target = "(jakarta.portlet.name=" + SynonymsPortletKeys.SYNONYMS + ")"
	)
	private Portlet _portlet;

}