/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	property = {
		"panel.app.order:Integer=150",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_WORKFLOW
	},
	service = PanelApp.class
)
public class ControlPanelWorkflowMetricsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "polls";
	}

	@Override
	public String getIconURL() {
		return StringBundler.concat(
			"data:image/svg+xml,%3Csvg%20width%3D%2240%22%20height%3D%2240%22",
			"%20viewBox%3D%220%200%2040%2040%22%20fill%3D%22none%22%20xmlns",
			"%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%3Cpath%20d%3D",
			"%22M0%2023.2C0%2021.4327%201.43269%2020%203.2%2020H8C9.76731",
			"%2020%2011.2%2021.4327%2011.2%2023.2V36C11.2%2037.7674%209.76731",
			"%2039.2001%208%2039.2001H3.2C1.43269%2039.2001%200%2037.7674%200",
			"%2036V23.2Z%22%20fill%3D%22%23B3CDFF%22%2F%3E%3Cpath%20d%3D",
			"%22M12.8%2012C12.8%2010.2327%2014.2327%208.80005%2016",
			"%208.80005H22.4C24.1673%208.80005%2025.6%2010.2327%2025.6",
			"%2012V36C25.6%2037.7674%2024.1673%2039.2001%2022.4",
			"%2039.2001H16C14.2327%2039.2001%2012.8%2037.7674%2012.8%2036V12Z",
			"%22%20fill%3D%22%2380ACFF%22%2F%3E%3Cpath%20d%3D%22M27.2",
			"%204.00005C27.2%202.23274%2028.6327%200.800049%2030.4",
			"%200.800049H36.8C38.5673%200.800049%2040%202.23274%2040",
			"%204.00005V36C40%2037.7674%2038.5673%2039.2001%2036.8",
			"%2039.2001H30.4C28.6327%2039.2001%2027.2%2037.7674%2027.2",
			"%2036V4.00005Z%22%20fill%3D%22%230B5FFF%22%2F%3E%3C%2Fsvg%3E");
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return WorkflowPortletKeys.WORKFLOW_METRICS;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (!_searchCapabilities.isWorkflowMetricsSupported()) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference(
		target = "(jakarta.portlet.name=" + WorkflowPortletKeys.WORKFLOW_METRICS + ")"
	)
	private Portlet _portlet;

	@Reference
	private SearchCapabilities _searchCapabilities;

}