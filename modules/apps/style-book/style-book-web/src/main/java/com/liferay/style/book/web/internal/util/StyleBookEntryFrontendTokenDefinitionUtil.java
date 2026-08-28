/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.util;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryFrontendTokenDefinitionUtil {

	public static List<JSONObject> getFrontendTokenDefinitionsJSONObjects(
			FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry,
			StyleBookEntry styleBookEntry, Locale locale)
		throws Exception {

		List<FrontendTokenDefinition> frontendTokenDefinitions = ListUtil.sort(
			ListUtil.filter(
				frontendTokenDefinitionRegistry.getFrontendTokenDefinitions(
					styleBookEntry.getCompanyId()),
				frontendTokenDefinition ->
					Objects.equals(
						frontendTokenDefinition.getThemeId(),
						styleBookEntry.getThemeId()) ||
					Objects.equals(
						frontendTokenDefinition.getThemeType(),
						FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL)),
			(frontendTokenDefinition1, frontendTokenDefinition2) ->
				Integer.compare(
					frontendTokenDefinition2.getPriority(),
					frontendTokenDefinition1.getPriority()));

		return TransformUtil.transform(
			frontendTokenDefinitions,
			frontendTokenDefinition -> {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

				JSONObject frontendTokenDefinitionJSONObject =
					_getFrontendTokenDefinitionJSONObject(
						frontendTokenDefinition, styleBookEntry, locale);

				for (String key : frontendTokenDefinitionJSONObject.keySet()) {
					jsonObject.put(
						key, frontendTokenDefinitionJSONObject.get(key));
				}

				jsonObject.put(
					"id", frontendTokenDefinition.getThemeId()
				).put(
					"name", frontendTokenDefinition.getThemeName(locale)
				).put(
					"priority", frontendTokenDefinition.getPriority()
				);

				return jsonObject;
			});
	}

	private static JSONObject _getFrontendTokenDefinitionJSONObject(
			FrontendTokenDefinition frontendTokenDefinition,
			StyleBookEntry styleBookEntry, Locale locale)
		throws Exception {

		JSONObject frontendTokenDefinitionJSONObject =
			frontendTokenDefinition.getJSONObject(locale);

		if (!DefaultStyleBookEntryUtil.isStyleBookEntryApplicable(
				frontendTokenDefinition, styleBookEntry)) {

			return frontendTokenDefinitionJSONObject;
		}

		JSONObject overrideFrontendTokenDefinitionJSONObject =
			FrontendTokenDefinitionUtil.parseFrontendTokenDefinitionJSONObject(
				styleBookEntry.getFrontendTokenDefinition());

		if (overrideFrontendTokenDefinitionJSONObject == null) {
			return frontendTokenDefinitionJSONObject;
		}

		return FrontendTokenDefinitionUtil.
			getMergedFrontendTokenDefinitionJSONObject(
				frontendTokenDefinitionJSONObject,
				overrideFrontendTokenDefinitionJSONObject);
	}

}