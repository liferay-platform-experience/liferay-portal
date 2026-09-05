/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.server.admin.web.internal.constants.ServerAdminNavigationEntryConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_SYSTEM
	},
	service = PanelApp.class
)
public class ServerAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "database";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return TransformUtil.unsafeTransformToList(
			ServerAdminNavigationEntryConstants.TABS1_NAMES,
			tabs1Name -> new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, tabs1Name),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/server_admin/view"
				).setTabs1(
					tabs1Name
				).buildString(),
				_language.get(themeDisplay.getLocale(), tabs1Name)));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return PortletKeys.SERVER_ADMIN;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN + ")"
	)
	private Portlet _portlet;

}