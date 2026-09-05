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
import com.liferay.change.tracking.web.internal.util.PublicationsNavigationUtil;
import com.liferay.petra.function.transform.TransformUtil;
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
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(
	property = {
		"panel.app.order:Integer=200",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS_PUBLICATIONS
	},
	service = PanelApp.class
)
public class PublicationsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "publications";
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

		return TransformUtil.unsafeTransform(
			PublicationsNavigationUtil.getNavigationTabs(),
			publicationsNavigationTab -> new PanelAppNavigationItem(
				_language.get(
					LocaleUtil.ENGLISH,
					publicationsNavigationTab.getLabelKey()),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					publicationsNavigationTab.getMVCRenderCommandName()
				).buildString(),
				_language.get(
					themeDisplay.getLocale(),
					publicationsNavigationTab.getLabelKey())));
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

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS + ")"
	)
	private Portlet _portlet;

}