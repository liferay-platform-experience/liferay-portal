/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.admin.constants;

import com.liferay.search.experiences.web.internal.blueprint.admin.navigation.SXPBlueprintAdminNavigationTab;

import java.util.List;

/**
 * @author Mario Leandro
 */
public class SXPBlueprintAdminNavigationConstants {

	public static final List<SXPBlueprintAdminNavigationTab>
		sxpBlueprintAdminNavigationTabs = List.of(
			new SXPBlueprintAdminNavigationTab(
				null, "blueprints", "/sxp_blueprint_admin/view_sxp_blueprints",
				"sxpBlueprints"),
			new SXPBlueprintAdminNavigationTab(
				Boolean.FALSE, "elements",
				"/sxp_blueprint_admin/view_sxp_elements", "sxpElements"));

}