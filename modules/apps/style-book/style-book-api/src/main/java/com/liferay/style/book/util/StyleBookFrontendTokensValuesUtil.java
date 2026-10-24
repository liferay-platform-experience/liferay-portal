/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.style.book.constants.StyleBookConstants;

/**
 * Reads the frontend tokens values of a style book, which hold one or more
 * color scheme variations:
 *
 * <pre>
 * {
 *   "default": {"name": "default", "tokens": {...}},
 *   "dark": {"name": "dark", "tokens": {...}}
 * }
 * </pre>
 *
 * <p>
 * Values stored before variations existed are a flat map of tokens, which is
 * read as the default variation.
 * </p>
 *
 * @author Marcos Castro
 */
public class StyleBookFrontendTokensValuesUtil {

	public static String getDefaultFrontendTokensValues(
			String frontendTokensValues)
		throws JSONException {

		JSONObject jsonObject = getDefaultFrontendTokensValuesJSONObject(
			frontendTokensValues);

		return jsonObject.toString();
	}

	public static JSONObject getDefaultFrontendTokensValuesJSONObject(
			String frontendTokensValues)
		throws JSONException {

		JSONObject variationsJSONObject = getVariationsJSONObject(
			frontendTokensValues);

		return getTokensJSONObject(
			variationsJSONObject.getJSONObject(
				StyleBookConstants.VARIATION_KEY_DEFAULT));
	}

	public static JSONObject getTokensJSONObject(
		JSONObject variationJSONObject) {

		if (variationJSONObject == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		JSONObject tokensJSONObject = variationJSONObject.getJSONObject(
			"tokens");

		if (tokensJSONObject == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		return tokensJSONObject;
	}

	public static JSONObject getVariationsJSONObject(
			String frontendTokensValues)
		throws JSONException {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (Validator.isNotNull(frontendTokensValues)) {
			jsonObject = JSONFactoryUtil.createJSONObject(frontendTokensValues);
		}

		if (isVariationsJSONObject(jsonObject)) {
			return jsonObject;
		}

		return JSONUtil.put(
			StyleBookConstants.VARIATION_KEY_DEFAULT,
			JSONUtil.put(
				"name", StyleBookConstants.VARIATION_KEY_DEFAULT
			).put(
				"tokens", jsonObject
			));
	}

	public static boolean isVariationsJSONObject(JSONObject jsonObject) {
		JSONObject defaultVariationJSONObject = jsonObject.getJSONObject(
			StyleBookConstants.VARIATION_KEY_DEFAULT);

		if ((defaultVariationJSONObject != null) &&
			(defaultVariationJSONObject.getJSONObject("tokens") != null)) {

			return true;
		}

		return false;
	}

}