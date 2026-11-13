/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_DEVELOPER_INTEGRATION
	},
	service = PanelApp.class
)
public class ClientExtensionAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "puzzle";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D",
			"%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%3E%3Cuse%20xlink",
			"%3Ahref%3D%22%23stroke0_2204_2124%22%20fill%3D%22url%28",
			"%23paint0_linear_2204_2124%29%22%2F%3E%3Cpath%20d%3D%22M11.871",
			"%2014.4655C11.871%2013.2212%2012.9108%2012.2125%2014.1936",
			"%2012.2125C15.4763%2012.2125%2016.5161%2013.2212%2016.5161",
			"%2014.4655V28.1278C16.5161%2029.3721%2015.4763%2030.3808",
			"%2014.1936%2030.3808C12.9108%2030.3808%2011.871%2025.473",
			"%2011.871%2024.2287V14.4655Z%22%20fill%3D%22%23004AD7%22%2F%3E",
			"%3Cpath%20d%3D%22M23.4839%2014.4655C23.4839%2013.2212%2024.5237",
			"%2012.2125%2025.8065%2012.2125C27.0892%2012.2125%2028.129",
			"%2013.2212%2028.129%2014.4655V24.2287C28.129%2025.473%2027.0892",
			"%2030.3808%2025.8065%2030.3808C24.5237%2030.3808%2023.4839",
			"%2029.3721%2023.4839%2028.1278V14.4655Z%22%20fill%3D%22%23004AD7",
			"%22%2F%3E%3Cuse%20xlink%3Ahref%3D%22%23stroke0_2204_2124%22",
			"%20fill%3D%22%230B5FFF%22%2F%3E%3Cpath%20d%3D%22M14.1936",
			"%2030.3808C14.1936%2029.1365%2015.2334%2028.1278%2016.5161",
			"%2028.1278H23.4839C24.7666%2028.1278%2025.8065%2029.1365",
			"%2025.8065%2030.3808C25.8065%2031.6251%2024.7666%2032.6339",
			"%2023.4839%2032.6339H16.5161C15.2334%2032.6339%2014.1936",
			"%2031.6251%2014.1936%2030.3808Z%22%20fill%3D%22%23B3CDFF%22%2F",
			"%3E%3Cpath%20d%3D%22M27.3722%200C29.8152%200%2031.7956%201.9211",
			"%2031.7956%204.2909V5.7212C31.7956%206.51113%2031.1354%207.1515",
			"%2030.3211%207.1515H28.1339V6.18074C28.1339%204.94211%2027.0983",
			"%203.9378%2025.8215%203.93751C24.5444%203.93751%2023.509",
			"%204.94194%2023.509%206.18074V7.1515H16.5112V6.18074C16.5112",
			"%204.94199%2015.4757%203.93761%2014.1987%203.93751C12.9217",
			"%203.93751%2011.8863%204.94194%2011.8863",
			"%206.18074V7.1515H9.47444C8.66013%207.1515%208%206.51113%208",
			"%205.7212V4.2909C8%201.9211%209.98039%200%2012.4233%200H27.3722Z",
			"%22%20fill%3D%22%235791FF%22%2F%3E%3Cdefs%3E%3ClinearGradient",
			"%20id%3D%22paint0_linear_2204_2124%22%20x1%3D%228%22%20y1%3D",
			"%2226.1062%22%20x2%3D%2232%22%20y2%3D%2226.1062%22",
			"%20gradientUnits%3D%22userSpaceOnUse%22%3E%3Cstop%20stop-color",
			"%3D%22%235791FF%22%2F%3E%3Cstop%20offset%3D%221%22%20stop-color",
			"%3D%22%23004AD7%22%2F%3E%3C%2FlinearGradient%3E%3Cpath%20id%3D",
			"%22stroke0_2204_2124%22%20d%3D%22M8.00002%2022.7267C8.00002",
			"%2021.8971%208.69326%2021.2246%209.54841",
			"%2021.2246H30.4516C31.3068%2021.2246%2032%2021.8971%2032",
			"%2022.7267V30.9878C32%2035.9651%2027.8406%2040%2022.7097",
			"%2040H17.2903C12.1594%2040%208.00002%2035.9651%208.00002",
			"%2030.9878V22.7267Z%22%2F%3E%3C%2Fdefs%3E%3C%2Fsvg%3E");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return ClientExtensionAdminPortletKeys.CLIENT_EXTENSION_ADMIN;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + ClientExtensionAdminPortletKeys.CLIENT_EXTENSION_ADMIN + ")"
	)
	private Portlet _portlet;

}