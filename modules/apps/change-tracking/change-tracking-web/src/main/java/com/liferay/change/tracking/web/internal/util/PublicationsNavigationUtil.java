/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.util;

import com.liferay.change.tracking.web.internal.navigation.PublicationsNavigationTab;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemBuilder;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.PropsValues;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Mario Leandro
 */
public class PublicationsNavigationUtil {

	public static List<NavigationItem> getNavigationItems(
		HttpServletRequest httpServletRequest, Language language,
		RenderResponse renderResponse, String selectedMVCRenderCommandName) {

		return TransformUtil.transform(
			getNavigationTabs(),
			publicationsNavigationTab -> NavigationItemBuilder.setActive(
				Objects.equals(
					selectedMVCRenderCommandName,
					publicationsNavigationTab.getMVCRenderCommandName())
			).setHref(
				PortletURLBuilder.createRenderURL(
					renderResponse
				).setMVCRenderCommandName(
					publicationsNavigationTab.getMVCRenderCommandName()
				).buildString()
			).setLabel(
				language.get(
					httpServletRequest, publicationsNavigationTab.getLabelKey())
			).build());
	}

	public static List<PublicationsNavigationTab> getNavigationTabs() {
		List<PublicationsNavigationTab> publicationsNavigationTabs =
			new ArrayList<>();

		publicationsNavigationTabs.add(
			new PublicationsNavigationTab(
				"ongoing", "/change_tracking/view_publications"));

		if (PropsValues.SCHEDULER_ENABLED) {
			publicationsNavigationTabs.add(
				new PublicationsNavigationTab(
					"scheduled", "/change_tracking/view_scheduled"));
		}

		publicationsNavigationTabs.add(
			new PublicationsNavigationTab(
				"history", "/change_tracking/view_history"));

		return publicationsNavigationTabs;
	}

}