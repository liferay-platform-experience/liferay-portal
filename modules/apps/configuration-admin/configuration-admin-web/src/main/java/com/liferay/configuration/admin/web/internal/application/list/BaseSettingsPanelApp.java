/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategoryDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategoryMenuDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationCategorySectionDisplay;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationModelConfigurationEntry;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcos Castro
 */
public abstract class BaseSettingsPanelApp extends BasePanelApp {

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		List<PanelAppNavigationItem> panelAppNavigationItems =
			new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		for (ConfigurationCategorySectionDisplay
				configurationCategorySectionDisplay :
					configurationEntryRetriever.
						getConfigurationCategorySectionDisplays(
							getScope(), getScopePK(themeDisplay))) {

			for (ConfigurationCategoryDisplay configurationCategoryDisplay :
					configurationCategorySectionDisplay.
						getConfigurationCategoryDisplays()) {

				ConfigurationCategoryMenuDisplay
					configurationCategoryMenuDisplay =
						configurationEntryRetriever.
							getConfigurationCategoryMenuDisplay(
								configurationCategoryDisplay.getCategoryKey(),
								themeDisplay.getLanguageId(), getScope(),
								getScopePK(themeDisplay));

				if (configurationCategoryMenuDisplay.isEmpty()) {
					continue;
				}

				panelAppNavigationItems.add(
					new PanelAppNavigationItem(
						_getHREF(
							httpServletRequest,
							configurationCategoryMenuDisplay.
								getFirstConfigurationEntry()),
						configurationCategoryDisplay.getCategoryLabel(
							themeDisplay.getLocale())));
			}
		}

		return panelAppNavigationItems;
	}

	protected abstract ExtendedObjectClassDefinition.Scope getScope();

	protected abstract Serializable getScopePK(ThemeDisplay themeDisplay);

	@Reference
	protected ConfigurationEntryRetriever configurationEntryRetriever;

	private String _getHREF(
			HttpServletRequest httpServletRequest,
			ConfigurationEntry configurationEntry)
		throws PortalException {

		if (configurationEntry instanceof
				ConfigurationModelConfigurationEntry) {

			ConfigurationModelConfigurationEntry
				configurationModelConfigurationEntry =
					(ConfigurationModelConfigurationEntry)configurationEntry;

			ConfigurationModel configurationModel =
				configurationModelConfigurationEntry.getConfigurationModel();

			if (configurationModel.isFactory()) {
				return PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/configuration_admin/view_factory_instances"
				).setParameter(
					"factoryPid", configurationModel.getFactoryPid()
				).buildString();
			}

			return PortletURLBuilder.create(
				getPortletURL(httpServletRequest)
			).setMVCRenderCommandName(
				"/configuration_admin/edit_configuration"
			).setParameter(
				"factoryPid", configurationModel.getFactoryPid()
			).setParameter(
				"pid", configurationModel.getID()
			).buildString();
		}

		return PortletURLBuilder.create(
			getPortletURL(httpServletRequest)
		).setMVCRenderCommandName(
			"/configuration_admin/view_configuration_screen"
		).setParameter(
			"configurationScreenKey", configurationEntry.getKey()
		).buildString();
	}

}