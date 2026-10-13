/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;
import java.util.Set;

/**
 * @author Thiago Buarque
 */
public class FrontendTokensValuesUtil {

	public static String getFrontendTokenValue(
		Map<String, String> defaultFrontendTokenValues,
		JSONObject frontendTokensValuesJSONObject, String key) {

		if (frontendTokensValuesJSONObject == null) {
			return null;
		}

		JSONObject frontendTokenValueJSONObject =
			frontendTokensValuesJSONObject.getJSONObject(key);

		if (frontendTokenValueJSONObject == null) {
			return null;
		}

		Set<String> keys = SetUtil.fromArray(key);

		while (true) {
			String name = frontendTokenValueJSONObject.getString("name");

			if (Validator.isNull(name) || !keys.add(name)) {
				break;
			}

			JSONObject linkedFrontendTokenValueJSONObject =
				frontendTokensValuesJSONObject.getJSONObject(name);

			if (linkedFrontendTokenValueJSONObject == null) {
				if (defaultFrontendTokenValues == null) {
					break;
				}

				String defaultFrontendTokenValue =
					defaultFrontendTokenValues.get(name);

				if (defaultFrontendTokenValue != null) {
					return defaultFrontendTokenValue;
				}

				break;
			}

			frontendTokenValueJSONObject = linkedFrontendTokenValueJSONObject;
		}

		return frontendTokenValueJSONObject.getString("value");
	}

}