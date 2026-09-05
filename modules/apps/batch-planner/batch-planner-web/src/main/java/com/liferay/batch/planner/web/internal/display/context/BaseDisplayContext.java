/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.web.internal.display.context;

import com.liferay.batch.planner.batch.engine.task.TaskItemUtil;
import com.liferay.batch.planner.web.internal.constants.BatchPlannerNavigationConstants;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Matija Petanjek
 */
public abstract class BaseDisplayContext {

	public BaseDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		this.renderRequest = renderRequest;
		this.renderResponse = renderResponse;
		httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	public List<NavigationItem> getNavigationItems() {
		String tabs1 = ParamUtil.getString(
			renderRequest, "tabs1", "batch-planner-plans");

		return TransformUtil.transform(
			BatchPlannerNavigationConstants.batchPlannerNavigationTabs,
			batchPlannerNavigationTab -> NavigationItemBuilder.setActive(
				tabs1.equals(batchPlannerNavigationTab.getTabs1Name())
			).setHref(
				() -> PortletURLBuilder.createRenderURL(
					renderResponse
				).setMVCRenderCommandName(
					batchPlannerNavigationTab.getMVCRenderCommandName(), false
				).setTabs1(
					batchPlannerNavigationTab.getTabs1Name()
				).buildString()
			).setLabel(
				LanguageUtil.get(
					httpServletRequest, batchPlannerNavigationTab.getLabelKey())
			).build());
	}

	public String getSimpleClassName(String internalClassNameKey) {
		return TaskItemUtil.getSimpleClassName(internalClassNameKey);
	}

	protected boolean isExport(String navigation) {
		return Objects.equals(navigation, "export");
	}

	protected HttpServletRequest httpServletRequest;
	protected RenderRequest renderRequest;
	protected RenderResponse renderResponse;

}