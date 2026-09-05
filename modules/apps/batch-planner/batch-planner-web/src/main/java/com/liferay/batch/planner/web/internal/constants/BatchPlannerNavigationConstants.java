/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.constants;

import com.liferay.batch.planner.web.internal.navigation.BatchPlannerNavigationTab;

import java.util.List;

/**
 * @author Mario Leandro
 */
public class BatchPlannerNavigationConstants {

	public static final List<BatchPlannerNavigationTab>
		batchPlannerNavigationTabs = List.of(
			new BatchPlannerNavigationTab(
				"import-and-export", null, "batch-planner-plans"),
			new BatchPlannerNavigationTab(
				"templates", "/batch_planner/view_batch_planner_plan_templates",
				"batch-planner-plan-templates"));

}