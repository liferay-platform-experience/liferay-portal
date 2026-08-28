/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.util;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Gabriel Lima
 */
public class FrontendTokenDefinitionUtil {

	public static JSONObject createFrontendTokenDefinitionJSONObject(
		String categoryName, String tokenSetName,
		JSONObject frontendTokenJSONObject) {

		return JSONUtil.put(
			"frontendTokenCategories",
			JSONUtil.putAll(
				JSONUtil.put(
					"frontendTokenSets",
					JSONUtil.putAll(
						JSONUtil.put(
							"frontendTokens",
							JSONUtil.putAll(frontendTokenJSONObject)
						).put(
							"label", tokenSetName
						).put(
							"name", tokenSetName
						))
				).put(
					"name", categoryName
				)));
	}

	public static JSONObject createFrontendTokenJSONObject(
		String cssVariableMappingValue, String description, String editorType,
		String label, String name) {

		JSONObject frontendTokenJSONObject = JSONUtil.put(
			"label", label
		).put(
			"mappings",
			JSONUtil.putAll(
				JSONUtil.put(
					"type", "cssVariable"
				).put(
					"value", cssVariableMappingValue
				))
		).put(
			"name", name
		).put(
			"type", "String"
		);

		if (Validator.isNotNull(description)) {
			frontendTokenJSONObject.put("description", description);
		}

		if (!Objects.equals(editorType, "Default")) {
			frontendTokenJSONObject.put("editorType", editorType);
		}

		return frontendTokenJSONObject;
	}

	public static List<String> getFrontendTokenNames(
		String frontendTokenDefinition) {

		List<String> frontendTokenNames = new ArrayList<>();

		for (JSONObject frontendTokenJSONObject :
				getFrontendTokens(frontendTokenDefinition)) {

			frontendTokenNames.add(frontendTokenJSONObject.getString("name"));
		}

		return frontendTokenNames;
	}

	public static List<JSONObject> getFrontendTokens(
		String frontendTokenDefinition) {

		JSONObject frontendTokenDefinitionJSONObject =
			parseFrontendTokenDefinitionJSONObject(frontendTokenDefinition);

		if (frontendTokenDefinitionJSONObject == null) {
			return Collections.emptyList();
		}

		JSONArray frontendTokenCategoriesJSONArray =
			frontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		if (frontendTokenCategoriesJSONArray == null) {
			return Collections.emptyList();
		}

		List<JSONObject> frontendTokenJSONObjects = new ArrayList<>();

		for (int i = 0; i < frontendTokenCategoriesJSONArray.length(); i++) {
			JSONObject frontendTokenCategoryJSONObject =
				frontendTokenCategoriesJSONArray.getJSONObject(i);

			if (frontendTokenCategoryJSONObject == null) {
				continue;
			}

			_collectFrontendTokens(
				frontendTokenCategoryJSONObject, frontendTokenJSONObjects);
		}

		return frontendTokenJSONObjects;
	}

	public static JSONObject getMergedFrontendTokenDefinitionJSONObject(
		JSONObject frontendTokenDefinitionJSONObject,
		JSONObject overrideFrontendTokenDefinitionJSONObject) {

		JSONArray overrideFrontendTokenCategoriesJSONArray =
			_getFrontendTokenCategoriesJSONArray(
				overrideFrontendTokenDefinitionJSONObject);

		if (JSONUtil.isEmpty(overrideFrontendTokenCategoriesJSONArray)) {
			return frontendTokenDefinitionJSONObject;
		}

		JSONObject mergedFrontendTokenDefinitionJSONObject = _clone(
			frontendTokenDefinitionJSONObject);

		JSONArray frontendTokenCategoriesJSONArray =
			_getFrontendTokenCategoriesJSONArray(
				mergedFrontendTokenDefinitionJSONObject);

		if (frontendTokenCategoriesJSONArray == null) {
			frontendTokenCategoriesJSONArray =
				JSONFactoryUtil.createJSONArray();

			mergedFrontendTokenDefinitionJSONObject.put(
				"frontendTokenCategories", frontendTokenCategoriesJSONArray);
		}

		_mergeNamedJSONObjects(
			frontendTokenCategoriesJSONArray,
			_getFrontendTokenCategoriesJSONArray(
				_clone(overrideFrontendTokenDefinitionJSONObject)));

		return mergedFrontendTokenDefinitionJSONObject;
	}

	public static JSONObject parseFrontendTokenDefinitionJSONObject(
		String frontendTokenDefinition) {

		if (Validator.isNull(frontendTokenDefinition)) {
			return null;
		}

		try {
			return JSONFactoryUtil.createJSONObject(frontendTokenDefinition);
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to parse frontend token definition", jsonException);
			}

			return null;
		}
	}

	public static JSONObject removeFrontendToken(
		JSONObject frontendTokenDefinitionJSONObject, String name) {

		JSONArray frontendTokenCategoriesJSONArray =
			_getFrontendTokenCategoriesJSONArray(
				frontendTokenDefinitionJSONObject);

		if (frontendTokenCategoriesJSONArray == null) {
			return frontendTokenDefinitionJSONObject;
		}

		for (int i = 0; i < frontendTokenCategoriesJSONArray.length(); i++) {
			JSONObject frontendTokenCategoryJSONObject =
				frontendTokenCategoriesJSONArray.getJSONObject(i);

			JSONArray frontendTokenSetsJSONArray =
				frontendTokenCategoryJSONObject.getJSONArray(
					"frontendTokenSets");

			if (frontendTokenSetsJSONArray == null) {
				continue;
			}

			for (int j = 0; j < frontendTokenSetsJSONArray.length(); j++) {
				JSONObject frontendTokenSetJSONObject =
					frontendTokenSetsJSONArray.getJSONObject(j);

				JSONArray frontendTokensJSONArray =
					frontendTokenSetJSONObject.getJSONArray("frontendTokens");

				if (frontendTokensJSONArray == null) {
					continue;
				}

				frontendTokenSetJSONObject.put(
					"frontendTokens",
					_getNamedFilteredFrontendTokensJSONArray(
						frontendTokensJSONArray, name));
			}
		}

		return frontendTokenDefinitionJSONObject;
	}

	private static JSONObject _clone(JSONObject jsonObject) {
		if (jsonObject == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		return JSONFactoryUtil.createJSONObject(jsonObject.toMap());
	}

	private static void _collectFrontendTokens(
		JSONObject frontendTokenCategoryJSONObject,
		List<JSONObject> frontendTokenJSONObjects) {

		JSONArray frontendTokenSetsJSONArray =
			frontendTokenCategoryJSONObject.getJSONArray("frontendTokenSets");

		if (frontendTokenSetsJSONArray == null) {
			return;
		}

		for (int i = 0; i < frontendTokenSetsJSONArray.length(); i++) {
			JSONObject frontendTokenSetJSONObject =
				frontendTokenSetsJSONArray.getJSONObject(i);

			if (frontendTokenSetJSONObject == null) {
				continue;
			}

			JSONArray frontendTokensJSONArray =
				frontendTokenSetJSONObject.getJSONArray("frontendTokens");

			if (frontendTokensJSONArray == null) {
				continue;
			}

			for (int j = 0; j < frontendTokensJSONArray.length(); j++) {
				JSONObject frontendTokenJSONObject =
					frontendTokensJSONArray.getJSONObject(j);

				if (frontendTokenJSONObject == null) {
					continue;
				}

				frontendTokenJSONObjects.add(frontendTokenJSONObject);
			}
		}
	}

	private static JSONArray _getFrontendTokenCategoriesJSONArray(
		JSONObject frontendTokenDefinitionJSONObject) {

		if (frontendTokenDefinitionJSONObject == null) {
			return null;
		}

		return frontendTokenDefinitionJSONObject.getJSONArray(
			"frontendTokenCategories");
	}

	private static JSONArray _getNamedFilteredFrontendTokensJSONArray(
		JSONArray frontendTokensJSONArray, String name) {

		JSONArray filteredFrontendTokensJSONArray =
			JSONFactoryUtil.createJSONArray();

		for (int i = 0; i < frontendTokensJSONArray.length(); i++) {
			JSONObject frontendTokenJSONObject =
				frontendTokensJSONArray.getJSONObject(i);

			if (!Objects.equals(
					name, frontendTokenJSONObject.getString("name"))) {

				filteredFrontendTokensJSONArray.put(frontendTokenJSONObject);
			}
		}

		return filteredFrontendTokensJSONArray;
	}

	private static JSONObject _getNamedJSONObject(
		JSONArray jsonArray, String name) {

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			if (jsonObject == null) {
				continue;
			}

			if (Objects.equals(name, jsonObject.getString("name"))) {
				return jsonObject;
			}
		}

		return null;
	}

	private static JSONArray _mergeNamedJSONObject(
		JSONArray jsonArray, JSONObject overrideJSONObject,
		JSONObject jsonObject, int depth) {

		if (depth == _CHILD_ARRAY_KEYS.length) {
			return JSONUtil.replace(jsonArray, "name", overrideJSONObject);
		}

		String childArrayKey = _CHILD_ARRAY_KEYS[depth];

		JSONArray overrideChildJSONArray = overrideJSONObject.getJSONArray(
			childArrayKey);

		if (overrideChildJSONArray == null) {
			return jsonArray;
		}

		JSONArray childJSONArray = jsonObject.getJSONArray(childArrayKey);

		if (childJSONArray == null) {
			childJSONArray = JSONFactoryUtil.createJSONArray();

			jsonObject.put(childArrayKey, childJSONArray);
		}

		jsonObject.put(
			childArrayKey,
			_mergeNamedJSONObjects(
				childJSONArray, overrideChildJSONArray, depth + 1));

		return jsonArray;
	}

	private static JSONArray _mergeNamedJSONObjects(
		JSONArray jsonArray, JSONArray overrideJSONArray) {

		return _mergeNamedJSONObjects(jsonArray, overrideJSONArray, 0);
	}

	private static JSONArray _mergeNamedJSONObjects(
		JSONArray jsonArray, JSONArray overrideJSONArray, int depth) {

		for (int i = 0; i < overrideJSONArray.length(); i++) {
			JSONObject overrideJSONObject = overrideJSONArray.getJSONObject(i);

			if (overrideJSONObject == null) {
				continue;
			}

			JSONObject jsonObject = _getNamedJSONObject(
				jsonArray, overrideJSONObject.getString("name"));

			if (jsonObject == null) {
				jsonArray.put(overrideJSONObject);

				continue;
			}

			jsonArray = _mergeNamedJSONObject(
				jsonArray, overrideJSONObject, jsonObject, depth);
		}

		return jsonArray;
	}

	private static final String[] _CHILD_ARRAY_KEYS = {
		"frontendTokenSets", "frontendTokens"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendTokenDefinitionUtil.class);

}