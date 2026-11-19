/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.template;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.CMSSidebarUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mario Leandro
 */
@Component(
	property = "type=" + TemplateContextContributor.TYPE_THEME,
	service = TemplateContextContributor.class
)
public class CMSSidebarTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		if (!CMSSidebarUtil.isUnifiedSidebar(httpServletRequest) ||
			!_hasSidebar(httpServletRequest)) {

			return;
		}

		String cssClass =
			GetterUtil.getString(contextObjects.get("bodyCssClass")) +
				" cms-unified-sidebar";

		String sidebarState = SessionClicks.get(
			httpServletRequest,
			"com.liferay.site.cms.site.initializer_sidebarState", "open");

		if (Objects.equals(sidebarState, "open")) {
			cssClass += " cms-sidebar-open";
		}

		contextObjects.put("bodyCssClass", cssClass);
	}

	private boolean _hasSidebar(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout == null) {
			return false;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.
				fetchLayoutPageTemplateEntryByPlid(
					layout.getMasterLayoutPlid());

		if (layoutPageTemplateEntry == null) {
			return false;
		}

		return _sidebarMasterLayoutPageTemplateEntryKeys.contains(
			layoutPageTemplateEntry.getLayoutPageTemplateEntryKey());
	}

	private static final Set<String> _sidebarMasterLayoutPageTemplateEntryKeys =
		SetUtil.fromArray("cms-master", "cms-space-master");

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

}