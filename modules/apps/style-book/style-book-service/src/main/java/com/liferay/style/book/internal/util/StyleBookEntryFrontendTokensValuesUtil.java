/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.util;

import com.liferay.frontend.token.definition.util.FrontendTokenDefinitionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.exception.StyleBookEntryFrontendTokensValuesException;
import com.liferay.style.book.util.FrontendTokensValuesUtil;
import com.liferay.style.book.util.StyleBookFrontendTokensValuesUtil;

import java.util.List;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryFrontendTokensValuesUtil {

	public static String normalizeFrontendTokensValues(
			String customFrontendTokenDefinition, String frontendTokensValues,
			String themeId)
		throws StyleBookEntryFrontendTokensValuesException {

		return normalizeFrontendTokensValues(
			null, customFrontendTokenDefinition, frontendTokensValues, themeId);
	}

	/**
	 * Returns the frontend tokens values as color scheme variations. A flat map
	 * of tokens, the format used before variations existed, replaces the
	 * default variation and keeps the other variations of the current values.
	 */
	public static String normalizeFrontendTokensValues(
			String currentFrontendTokensValues,
			String customFrontendTokenDefinition, String frontendTokensValues,
			String themeId)
		throws StyleBookEntryFrontendTokensValuesException {

		if (Validator.isBlank(frontendTokensValues) ||
			Validator.isBlank(themeId)) {

			return frontendTokensValues;
		}

		JSONObject variationsJSONObject = null;

		try {
			JSONObject frontendTokensValuesJSONObject =
				JSONFactoryUtil.createJSONObject(frontendTokensValues);

			if (StyleBookFrontendTokensValuesUtil.isVariationsJSONObject(
					frontendTokensValuesJSONObject)) {

				variationsJSONObject = frontendTokensValuesJSONObject;
			}
			else {
				variationsJSONObject =
					StyleBookFrontendTokensValuesUtil.getVariationsJSONObject(
						currentFrontendTokensValues);

				variationsJSONObject.put(
					StyleBookConstants.VARIATION_KEY_DEFAULT,
					JSONUtil.put(
						"name", StyleBookConstants.VARIATION_KEY_DEFAULT
					).put(
						"tokens", frontendTokensValuesJSONObject
					));
			}
		}
		catch (JSONException jsonException) {
			throw new StyleBookEntryFrontendTokensValuesException.
				MustBeValidJSON(jsonException);
		}

		List<String> customFrontendTokenNames =
			FrontendTokenDefinitionUtil.getFrontendTokenNames(
				customFrontendTokenDefinition);

		for (String variationKey : variationsJSONObject.keySet()) {
			JSONObject variationJSONObject = variationsJSONObject.getJSONObject(
				variationKey);

			if (variationJSONObject == null) {
				continue;
			}

			if (Validator.isNull(variationJSONObject.getString("name"))) {
				variationJSONObject.put("name", variationKey);
			}

			JSONObject tokensJSONObject = variationJSONObject.getJSONObject(
				"tokens");

			if (tokensJSONObject == null) {
				variationJSONObject.put(
					"tokens", JSONFactoryUtil.createJSONObject());

				continue;
			}

			_normalizeTokensJSONObject(
				customFrontendTokenNames, themeId, tokensJSONObject);
		}

		return variationsJSONObject.toString();
	}

	private static String _getFrontendTokenDefinitionId(
		List<String> customFrontendTokenNames, String frontendTokenName,
		String themeId) {

		if (customFrontendTokenNames.contains(frontendTokenName)) {
			return StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_CUSTOM;
		}

		return themeId;
	}

	private static boolean _isNamespaced(String frontendTokenName) {
		if (frontendTokenName.indexOf(CharPool.COLON) == -1) {
			return false;
		}

		return true;
	}

	private static void _normalizeTokensJSONObject(
		List<String> customFrontendTokenNames, String themeId,
		JSONObject tokensJSONObject) {

		for (String key : ListUtil.fromCollection(tokensJSONObject.keySet())) {
			JSONObject frontendTokenValueJSONObject =
				tokensJSONObject.getJSONObject(key);

			if (frontendTokenValueJSONObject == null) {
				continue;
			}

			if (!_isNamespaced(key)) {
				tokensJSONObject.remove(key);

				String tokenDefinitionId =
					frontendTokenValueJSONObject.getString("tokenDefinitionId");

				if (Validator.isNull(tokenDefinitionId)) {
					tokenDefinitionId = _getFrontendTokenDefinitionId(
						customFrontendTokenNames, key, themeId);
				}

				String namespacedKey =
					FrontendTokensValuesUtil.getFrontendTokensValuesKey(
						tokenDefinitionId, key);

				if (tokensJSONObject.has(namespacedKey)) {
					continue;
				}

				frontendTokenValueJSONObject.put(
					"tokenDefinitionId", tokenDefinitionId);

				tokensJSONObject.put(
					namespacedKey, frontendTokenValueJSONObject);
			}

			String name = frontendTokenValueJSONObject.getString("name");

			if (Validator.isNull(name) || _isNamespaced(name)) {
				continue;
			}

			frontendTokenValueJSONObject.put(
				"name",
				FrontendTokensValuesUtil.getFrontendTokensValuesKey(
					_getFrontendTokenDefinitionId(
						customFrontendTokenNames, name, themeId),
					name));
		}
	}

}