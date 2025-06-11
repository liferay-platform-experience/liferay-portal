/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.constants.ConfigurationScreenConstants;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationScreenConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContextFactory;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/configuration_admin/view_configuration_screen",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = MVCRenderCommand.class
)
public class ViewConfigurationScreenMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		String configurationScreenKey = ParamUtil.getString(
			renderRequest, "configurationScreenKey");

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ConfigurationScreen configurationScreen =
			_configurationEntryRetriever.getConfigurationScreen(
				configurationScreenKey);

		if (!configurationScreen.isVisible()) {
			throw new PortletException(
				StringBundler.concat(
					"The ", configurationScreen.getScope(), " configuration \"",
					configurationScreen.getName(themeDisplay.getLocale()),
					"\" is not accessible"));
		}

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			ConfigurationScopeDisplayContextFactory.create(renderRequest);

		renderRequest.setAttribute(
			ConfigurationAdminWebKeys.CONFIGURATION_CATEGORY_MENU_DISPLAY,
			_configurationEntryRetriever.getConfigurationCategoryMenuDisplay(
				configurationScreen.getCategoryKey(),
				themeDisplay.getLanguageId(),
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK()));

		renderRequest.setAttribute(
			ConfigurationAdminWebKeys.CONFIGURATION_SCREEN,
			configurationScreen);

		ConfigurationEntry configurationEntry =
			new ConfigurationScreenConfigurationEntry(
				configurationScreen, _portal.getLocale(renderRequest));

		renderRequest.setAttribute(
			ConfigurationAdminWebKeys.CONFIGURATION_ENTRY, configurationEntry);

		String pid = configurationScreen.getPID();

		if (Validator.isNull(pid)) {
			return "/view_configuration_screen.jsp";
		}

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				themeDisplay.getLanguageId(),
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK());

		ConfigurationModel configurationModel = configurationModels.get(pid);

		if (configurationModel != null) {
			configurationModel = new ConfigurationModel(
				_configurationModelRetriever.getConfiguration(
					pid, configurationScopeDisplayContext.getScope(),
					configurationScopeDisplayContext.getScopePK()),
				configurationModel);

			renderRequest.setAttribute(
				ConfigurationScreenConstants.DELETE_CONFIGURATION_URL,
				_getDeleteConfigurationActionCommand(
					configurationModel, renderRequest, renderResponse));

			renderRequest.setAttribute(
				ConfigurationScreenConstants.EXPORT_CONFIGURATION_URL,
				_getExportConfigurationActionCommand(
					configurationModel, renderResponse));
		}

		return "/view_configuration_screen.jsp";
	}

	private String _getDeleteConfigurationActionCommand(
		ConfigurationModel configurationModel, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		PortletURL currentURL = PortletURLUtil.getCurrent(
			renderRequest, renderResponse);

		return PortletURLBuilder.createActionURL(
			renderResponse
		).setActionName(
			"/configuration_admin/delete_configuration"
		).setRedirect(
			currentURL
		).setParameter(
			"factoryPid", configurationModel.getFactoryPid()
		).setParameter(
			"pid", configurationModel.getID()
		).buildString();
	}

	private String _getExportConfigurationActionCommand(
		ConfigurationModel configurationModel, RenderResponse renderResponse) {

		return ResourceURLBuilder.createResourceURL(
			renderResponse
		).setParameter(
			"factoryPid", configurationModel.getFactoryPid()
		).setParameter(
			"pid", configurationModel.getID()
		).setResourceID(
			"/configuration_admin/export_configuration"
		).buildString();
	}

	@Reference
	private ConfigurationEntryRetriever _configurationEntryRetriever;

	@Reference(target = "(filter.visibility=*)")
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Reference
	private Portal _portal;

}