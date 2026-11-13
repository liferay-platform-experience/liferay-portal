/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.change.tracking.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"panel.app.order:Integer=300",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_IN_MAINTENANCE
	},
	service = PanelApp.class
)
public class PublicationsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "publications";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M10.2361%203.07692C10.2361%201.37758%2011.6166%200%2013.3194",
			"%200H34.3889C35.5241%200%2036.4444%200.91839%2036.4444",
			"%202.05128V34.8718C36.4444%2036.0047%2035.5241%2036.9231",
			"%2034.3889%2036.9231H13.3194C11.6166%2036.9231%2010.2361",
			"%2035.5455%2010.2361%2033.8462V3.07692Z%22%20fill%3D%22%230B5FFF",
			"%22%2F%3E%3Cpath%20d%3D%22M33.875%201.53846C34.4426%201.53846",
			"%2034.9028%201.99766%2034.9028%202.5641V8.20513H37.4722C38.0398",
			"%208.20513%2038.5%208.66432%2038.5%209.23077V11.7949C38.5",
			"%2012.3613%2038.0398%2012.8205%2037.4722",
			"%2012.8205H34.9028V15.3846H37.4722C38.0398%2015.3846%2038.5",
			"%2015.8438%2038.5%2016.4103V18.9744C38.5%2019.5408%2038.0398",
			"%2020%2037.4722%2020H34.9028V37.4359C34.9028%2038.0023%2034.4426",
			"%2038.4615%2033.875%2038.4615H10.2361C9.66849%2038.4615",
			"%209.20833%2038.0023%209.20833%2037.4359V2.5641C9.20833",
			"%201.99766%209.66848%201.53846%2010.2361%201.53846H33.875Z%22",
			"%20fill%3D%22%23B3CDFF%22%2F%3E%3Cpath%20d%3D%22M7.15278",
			"%206.15385C7.15278%204.45451%208.53323%203.07692%2010.2361",
			"%203.07692L31.3056%203.07692C32.4408%203.07692%2033.3611",
			"%203.99531%2033.3611%205.12821V37.9487C33.3611%2039.0816",
			"%2032.4408%2040%2031.3056%2040H10.2361C8.53323%2040%207.15278",
			"%2038.6224%207.15278%2036.9231V6.15385Z%22%20fill%3D%22%2380ACFF",
			"%22%2F%3E%3Cpath%20d%3D%22M7.15278%2011.7949C5.86079%2011.7949",
			"%204.75944%2012.154%204.02428%2012.6542C3.2813%2013.1598",
			"%203.04167%2013.6988%203.04167%2014.1026C3.04167%2014.5063",
			"%203.2813%2015.0454%204.02428%2015.5509C4.75944%2016.0511",
			"%205.86079%2016.4103%207.15278%2016.4103C8.44477%2016.4103",
			"%209.54612%2016.0511%2010.2813%2015.5509C10.3446%2015.5078",
			"%2010.4037%2015.4637%2010.4599%2015.4202C10.0091%2015.0439",
			"%209.72222%2014.4785%209.72222%2013.8462C9.72222%2012.7133",
			"%2010.6425%2011.7949%2011.7778%2011.7949C12.913%2011.7949",
			"%2013.8333%2012.7133%2013.8333%2013.8462C13.8333%2014.841",
			"%2013.1235%2015.67%2012.1818%2015.8574C11.2441%2017.0985",
			"%209.34493%2017.9487%207.15278%2017.9487C4.03083%2017.9487%201.5",
			"%2016.2267%201.5%2014.1026C1.5%2011.9784%204.03083%2010.2564",
			"%207.15278%2010.2564V11.7949Z%22%20fill%3D%22%230B5FFF%22%2F%3E",
			"%3Cpath%20d%3D%22M7.15278%2027.6923C5.86079%2027.6923%204.75944",
			"%2028.0515%204.02428%2028.5517C3.2813%2029.0572%203.04167",
			"%2029.5963%203.04167%2030C3.04167%2030.4037%203.2813%2030.9428",
			"%204.02428%2031.4483C4.75944%2031.9485%205.86079%2032.3077",
			"%207.15278%2032.3077C8.44477%2032.3077%209.54612%2031.9485",
			"%2010.2813%2031.4483C10.3446%2031.4052%2010.4037%2031.3611",
			"%2010.4599%2031.3176C10.0091%2030.9413%209.72222%2030.376",
			"%209.72222%2029.7436C9.72222%2028.6107%2010.6425%2027.6923",
			"%2011.7778%2027.6923C12.913%2027.6923%2013.8333%2028.6107",
			"%2013.8333%2029.7436C13.8333%2030.7385%2013.1235%2031.5675",
			"%2012.1818%2031.7548C11.2441%2032.9959%209.34493%2033.8462",
			"%207.15278%2033.8462C4.03083%2033.8462%201.5%2032.1242%201.5",
			"%2030C1.5%2027.8758%204.03083%2026.1538%207.15278",
			"%2026.1538V27.6923Z%22%20fill%3D%22%230B5FFF%22%2F%3E%3Cpath%20d",
			"%3D%22M17.9444%2013.5897C17.9444%2012.3152%2018.9798%2011.2821",
			"%2020.2569%2011.2821H27.9653C29.2424%2011.2821%2030.2778",
			"%2012.3152%2030.2778%2013.5897C30.2778%2014.8642%2029.2424",
			"%2015.8974%2027.9653%2015.8974H20.2569C18.9798%2015.8974",
			"%2017.9444%2014.8642%2017.9444%2013.5897Z%22%20fill%3D%22",
			"%235791FF%22%2F%3E%3Cpath%20d%3D%22M17.9444%2020.2564C17.9444",
			"%2018.9819%2018.9798%2017.9487%2020.2569",
			"%2017.9487H27.9653C29.2424%2017.9487%2030.2778%2018.9819",
			"%2030.2778%2020.2564C30.2778%2021.5309%2029.2424%2022.5641",
			"%2027.9653%2022.5641H20.2569C18.9798%2022.5641%2017.9444",
			"%2021.5309%2017.9444%2020.2564Z%22%20fill%3D%22%235791FF%22%2F",
			"%3E%3C%2Fsvg%3E");
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_ctSettingsConfigurationHelper.isEnabled(
				themeDisplay.getCompanyId())) {

			return Collections.emptyList();
		}

		List<PanelAppNavigationItem> panelAppNavigationItems =
			new ArrayList<>();

		panelAppNavigationItems.add(
			_getPanelAppNavigationItem(
				httpServletRequest, "ongoing",
				"/change_tracking/view_publications", themeDisplay));

		if (PropsValues.SCHEDULER_ENABLED) {
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "scheduled",
					"/change_tracking/view_scheduled", themeDisplay));
		}

		panelAppNavigationItems.add(
			_getPanelAppNavigationItem(
				httpServletRequest, "history[record]",
				"/change_tracking/view_history", themeDisplay));

		return panelAppNavigationItems;
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return CTPortletKeys.PUBLICATIONS;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		PortletURL portletURL = super.getPortletURL(httpServletRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_ctSettingsConfigurationHelper.isEnabled(
				themeDisplay.getCompanyId())) {

			portletURL.setParameter(
				"mvcRenderCommandName", "/change_tracking/view_settings");
		}

		return portletURL;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (PortletPermissionUtil.contains(
				permissionChecker, CTPortletKeys.PUBLICATIONS,
				ActionKeys.CONFIGURATION) ||
			(_ctSettingsConfigurationHelper.isEnabled(group.getCompanyId()) &&
			 PortletPermissionUtil.contains(
				 permissionChecker, CTPortletKeys.PUBLICATIONS,
				 ActionKeys.VIEW))) {

			return true;
		}

		return false;
	}

	private PanelAppNavigationItem _getPanelAppNavigationItem(
			HttpServletRequest httpServletRequest, String labelKey,
			String mvcRenderCommandName, ThemeDisplay themeDisplay)
		throws PortalException {

		return new PanelAppNavigationItem(
			_language.get(LocaleUtil.ENGLISH, labelKey),
			PortletURLBuilder.create(
				getPortletURL(httpServletRequest)
			).setMVCRenderCommandName(
				mvcRenderCommandName
			).buildString(),
			_language.get(themeDisplay.getLocale(), labelKey));
	}

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS + ")"
	)
	private Portlet _portlet;

}