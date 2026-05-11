/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.util;

import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Lima
 */
public class PageEditorStyleBookEntriesUtil {

	public static List<Map<String, Object>> getStyleBookEntries(
			Layout layout, ThemeDisplay themeDisplay,
			FrontendTokenDefinition frontendTokenDefinition,
			boolean includeTokenValues)
		throws Exception {

		long liveGroupId = StagingUtil.getLiveGroupId(layout.getGroupId());

		List<StyleBookEntry> styleBookEntries =
			StyleBookEntryProviderUtil.getStyleBookEntries(
				layout.getCompanyId(), liveGroupId,
				frontendTokenDefinition.getThemeId());

		List<Map<String, Object>> entries = new ArrayList<>(
			styleBookEntries.size());

		Map<Long, Group> scopeGroups = new HashMap<>();

		for (StyleBookEntry styleBookEntry : styleBookEntries) {
			entries.add(
				_buildEntry(
					styleBookEntry, liveGroupId, themeDisplay,
					frontendTokenDefinition, includeTokenValues, scopeGroups));
		}

		return entries;
	}

	public static JSONArray getStyleBookEntriesJSONArray(
			Layout layout, ThemeDisplay themeDisplay,
			FrontendTokenDefinition frontendTokenDefinition,
			boolean includeTokenValues)
		throws Exception {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Map<String, Object> entry :
				getStyleBookEntries(
					layout, themeDisplay, frontendTokenDefinition,
					includeTokenValues)) {

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			for (Map.Entry<String, Object> mapEntry : entry.entrySet()) {
				jsonObject.put(mapEntry.getKey(), mapEntry.getValue());
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray;
	}

	private static Map<String, Object> _buildEntry(
			StyleBookEntry styleBookEntry, long liveGroupId,
			ThemeDisplay themeDisplay,
			FrontendTokenDefinition frontendTokenDefinition,
			boolean includeTokenValues, Map<Long, Group> scopeGroups)
		throws Exception {

		Map<String, Object> entry = HashMapBuilder.<String, Object>put(
			"imagePreviewURL", styleBookEntry.getImagePreviewURL(themeDisplay)
		).put(
			"name", styleBookEntry.getName()
		).put(
			"styleBookEntryERC", styleBookEntry.getExternalReferenceCode()
		).build();

		long entryGroupId = styleBookEntry.getGroupId();

		if (entryGroupId != liveGroupId) {
			Group scopeGroup = scopeGroups.computeIfAbsent(
				entryGroupId, GroupLocalServiceUtil::fetchGroup);

			if (scopeGroup != null) {
				entry.put(
					"styleBookEntryScopeERC",
					scopeGroup.getExternalReferenceCode());
				entry.put(
					"subtitle",
					scopeGroup.getDescriptiveName(themeDisplay.getLocale()));
			}
		}

		if (includeTokenValues) {
			entry.put(
				"tokenValues",
				StyleBookEntryUtil.getFrontendTokensValues(
					frontendTokenDefinition, themeDisplay.getLocale(),
					styleBookEntry));
		}

		return entry;
	}

}