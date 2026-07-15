/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.oauth.client.constants.OAuthClientAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = {
		"panel.app.order:Integer=300",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_SECURITY
	},
	service = PanelApp.class
)
public class OAuthClientAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "shield-check";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return Arrays.asList(
			_getPanelAppNavigationItem(
				httpServletRequest,
				"/oauth_client_admin/view_oauth_client_entries",
				"oauth-clients"),
			_getPanelAppNavigationItem(
				httpServletRequest,
				"/oauth_client_admin/view_oauth_client_as_local_metadata",
				"oauth-client-as-local-metadata"),
			_getPanelAppNavigationItem(
				httpServletRequest,
				"/oauth_client_admin/view_oauth_client_pr_local_metadata",
				"oauth-client-pr-local-metadata"));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN;
	}

	private PanelAppNavigationItem _getPanelAppNavigationItem(
			HttpServletRequest httpServletRequest, String mvcRenderCommandName,
			String navigation)
		throws PortalException {

		return new PanelAppNavigationItem(
			PortletURLBuilder.create(
				getPortletURL(httpServletRequest)
			).setMVCRenderCommandName(
				mvcRenderCommandName
			).setNavigation(
				navigation
			).buildString(),
			_language.get(httpServletRequest, navigation));
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN + ")"
	)
	private Portlet _portlet;

}