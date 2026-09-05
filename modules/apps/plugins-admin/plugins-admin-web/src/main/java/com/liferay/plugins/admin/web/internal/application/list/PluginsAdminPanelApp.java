/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.plugins.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.plugins.admin.web.internal.constants.PluginsAdminNavigationConstants;
import com.liferay.plugins.admin.web.internal.constants.PluginsAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Peter Fellwock
 */
@Component(
	property = {
		"panel.app.order:Integer=500",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_CONFIGURATION
	},
	service = PanelApp.class
)
public class PluginsAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "box-container";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return TransformUtil.unsafeTransformToList(
			PluginsAdminNavigationConstants.TABS2_NAMES,
			tabs2Name -> new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, tabs2Name),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setTabs2(
					tabs2Name
				).buildString(),
				_language.get(themeDisplay.getLocale(), tabs2Name)));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return PluginsAdminPortletKeys.PLUGINS_ADMIN;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + PluginsAdminPortletKeys.PLUGINS_ADMIN + ")"
	)
	private Portlet _portlet;

}