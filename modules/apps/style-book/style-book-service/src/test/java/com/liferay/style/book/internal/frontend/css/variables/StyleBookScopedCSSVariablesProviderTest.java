/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.frontend.css.variables;

import com.liferay.frontend.css.variables.ScopedCSSVariables;
import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.constants.StyleBookConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gabriel Lima
 */
public class StyleBookScopedCSSVariablesProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testGetScopedCSSVariablesCollectionWithNamespacedKeys()
		throws Exception {

		_testGetScopedCSSVariablesCollection(
			FrontendTokenDefinitionConstants.PRIORITY_GLOBAL,
			JSONUtil.put(
				"clay:primaryColor",
				_createTokenValueJSONObject("--clay-primary", "clay", "#fff")
			).put(
				"theme:primaryColor",
				_createTokenValueJSONObject("--theme-primary", "theme", "#000")
			),
			FrontendTokenDefinitionConstants.PRIORITY_THEME,
			cssVariables -> {
				Assert.assertEquals(
					cssVariables.toString(), 2, cssVariables.size());
				Assert.assertEquals(
					"#000", cssVariables.get("--theme-primary"));
				Assert.assertEquals("#fff", cssVariables.get("--clay-primary"));
			});
	}

	@Test
	public void testGetScopedCSSVariablesCollectionWithPriority()
		throws Exception {

		JSONObject frontendTokensValuesJSONObject = JSONUtil.put(
			"clay:primaryColor",
			_createTokenValueJSONObject("--primary-color", "clay", "#fff")
		).put(
			"custom:secondaryColor",
			_createTokenValueJSONObject(
				"--secondary-color",
				StyleBookConstants.FRONTEND_TOKEN_DEFINITION_ID_CUSTOM, "#0f0")
		).put(
			"theme:primaryColor",
			_createTokenValueJSONObject("--primary-color", "theme", "#000")
		).put(
			"theme:secondaryColor",
			_createTokenValueJSONObject("--secondary-color", "theme", "#000")
		);

		_testGetScopedCSSVariablesCollection(
			FrontendTokenDefinitionConstants.PRIORITY_GLOBAL,
			frontendTokensValuesJSONObject,
			FrontendTokenDefinitionConstants.PRIORITY_THEME,
			cssVariables -> {
				Assert.assertEquals(
					cssVariables.toString(), 2, cssVariables.size());
				Assert.assertEquals(
					"#000", cssVariables.get("--primary-color"));
				Assert.assertEquals(
					"#0f0", cssVariables.get("--secondary-color"));
			});
		_testGetScopedCSSVariablesCollection(
			FrontendTokenDefinitionConstants.PRIORITY_THEME,
			frontendTokensValuesJSONObject,
			FrontendTokenDefinitionConstants.PRIORITY_GLOBAL,
			cssVariables -> {
				Assert.assertEquals(
					cssVariables.toString(), 2, cssVariables.size());
				Assert.assertEquals(
					"#fff", cssVariables.get("--primary-color"));
				Assert.assertEquals(
					"#0f0", cssVariables.get("--secondary-color"));
			});
	}

	@Test
	public void testGetScopedCSSVariablesCollectionWithVariations()
		throws Exception {

		_testGetScopedCSSVariablesCollectionWithVariations(
			":root [data-color-scheme='calm']",
			":root [data-color-scheme='dark']", "#fff");
	}

	@Test
	public void testGetScopedCSSVariablesCollectionWithVariationsWhenThemeFollowsColorScheme()
		throws Exception {

		_testGetScopedCSSVariablesCollectionWithVariations(
			"[data-color-scheme='calm']:root, :root [data-color-scheme='calm']",
			"[data-color-scheme='dark']:root, :root [data-color-scheme='dark']",
			"light-dark(#fff, #000)");
	}

	private FrontendTokenDefinition _createFrontendTokenDefinition(
		int priority, String themeId, Object tokenDefaultValue) {

		FrontendToken frontendToken = (FrontendToken)ProxyUtil.newProxyInstance(
			FrontendToken.class.getClassLoader(),
			new Class<?>[] {FrontendToken.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "getDefaultValue")) {
					return tokenDefaultValue;
				}

				return null;
			});

		return (FrontendTokenDefinition)ProxyUtil.newProxyInstance(
			FrontendTokenDefinition.class.getClassLoader(),
			new Class<?>[] {FrontendTokenDefinition.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "getFrontendTokens")) {
					return Collections.singletonList(frontendToken);
				}

				if (Objects.equals(method.getName(), "getPriority")) {
					return priority;
				}

				if (Objects.equals(method.getName(), "getThemeId")) {
					return themeId;
				}

				return null;
			});
	}

	private JSONObject _createTokenValueJSONObject(
		String cssVariableMapping, String tokenDefinitionId, String value) {

		return JSONUtil.put(
			"cssVariableMapping", cssVariableMapping
		).put(
			"tokenDefinitionId", tokenDefinitionId
		).put(
			"value", value
		);
	}

	private Collection<ScopedCSSVariables> _getScopedCSSVariablesCollection(
			int clayPriority, JSONObject frontendTokensValuesJSONObject,
			int themePriority, String themeTokenDefaultValue)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = (Company)ProxyUtil.newProxyInstance(
			Company.class.getClassLoader(), new Class<?>[] {Company.class},
			(proxy, method, args) -> {
				if (Objects.equals(method.getName(), "getCompanyId")) {
					return 12345L;
				}

				if (Objects.equals(method.getName(), "getGroupId")) {
					return 67890L;
				}

				return null;
			});

		themeDisplay.setCompany(company);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		FrontendTokenDefinitionRegistry mockedRegistry =
			(FrontendTokenDefinitionRegistry)ProxyUtil.newProxyInstance(
				FrontendTokenDefinitionRegistry.class.getClassLoader(),
				new Class<?>[] {FrontendTokenDefinitionRegistry.class},
				(proxy, method, args) -> {
					if (Objects.equals(
							method.getName(), "getFrontendTokenDefinition")) {

						return _createFrontendTokenDefinition(
							themePriority, "theme", themeTokenDefaultValue);
					}

					if (!Objects.equals(
							method.getName(), "getFrontendTokenDefinitions")) {

						return null;
					}

					return ListUtil.fromArray(
						_createFrontendTokenDefinition(
							clayPriority, "clay", "#fff"),
						_createFrontendTokenDefinition(
							themePriority, "theme", themeTokenDefaultValue));
				});

		StyleBookScopedCSSVariablesProvider
			styleBookScopedCSSVariablesProvider =
				new TestStyleBookScopedCSSVariablesProvider(
					frontendTokensValuesJSONObject.toString());

		ReflectionTestUtil.setFieldValue(
			styleBookScopedCSSVariablesProvider,
			"_frontendTokenDefinitionRegistry", mockedRegistry);

		return styleBookScopedCSSVariablesProvider.
			getScopedCSSVariablesCollection(mockHttpServletRequest);
	}

	private void _testGetScopedCSSVariablesCollection(
			int clayPriority, JSONObject frontendTokensValuesJSONObject,
			int themePriority,
			UnsafeConsumer<Map<String, String>, Exception> unsafeConsumer)
		throws Exception {

		Collection<ScopedCSSVariables> scopedCSSVariablesCollection =
			_getScopedCSSVariablesCollection(
				clayPriority, frontendTokensValuesJSONObject, themePriority,
				"#fff");

		Assert.assertEquals(
			scopedCSSVariablesCollection.toString(), 1,
			scopedCSSVariablesCollection.size());

		for (ScopedCSSVariables scopedCSSVariables :
				scopedCSSVariablesCollection) {

			unsafeConsumer.accept(scopedCSSVariables.getCSSVariables());
		}
	}

	private void _testGetScopedCSSVariablesCollectionWithVariations(
			String calmScope, String darkScope, String themeTokenDefaultValue)
		throws Exception {

		List<ScopedCSSVariables> scopedCSSVariablesList =
			ListUtil.fromCollection(
				_getScopedCSSVariablesCollection(
					FrontendTokenDefinitionConstants.PRIORITY_GLOBAL,
					JSONUtil.put(
						StyleBookConstants.VARIATION_KEY_DEFAULT,
						JSONUtil.put(
							"name", StyleBookConstants.VARIATION_KEY_DEFAULT
						).put(
							"tokens",
							JSONUtil.put(
								"theme:primaryColor",
								_createTokenValueJSONObject(
									"--primary", "theme", "#000")
							).put(
								"theme:secondaryColor",
								_createTokenValueJSONObject(
									"--secondary", "theme", "#111")
							)
						)
					).put(
						"calm",
						JSONUtil.put(
							"name", "Calm"
						).put(
							"tokens",
							JSONUtil.put(
								"theme:primaryColor",
								_createTokenValueJSONObject(
									"--primary", "theme", "#ccc"))
						)
					).put(
						"dark",
						JSONUtil.put(
							"name", "dark"
						).put(
							"tokens",
							JSONUtil.put(
								"theme:primaryColor",
								_createTokenValueJSONObject(
									"--primary", "theme", "#fff"))
						)
					),
					FrontendTokenDefinitionConstants.PRIORITY_THEME,
					themeTokenDefaultValue));

		Assert.assertEquals(
			scopedCSSVariablesList.toString(), 4,
			scopedCSSVariablesList.size());

		ScopedCSSVariables rootScopedCSSVariables = scopedCSSVariablesList.get(
			0);

		Assert.assertNull(rootScopedCSSVariables.getColorScheme());
		Assert.assertNull(rootScopedCSSVariables.getMediaQuery());
		Assert.assertEquals(":root", rootScopedCSSVariables.getScope());

		Map<String, String> rootCSSVariables =
			rootScopedCSSVariables.getCSSVariables();

		Assert.assertEquals(
			rootCSSVariables.toString(), 2, rootCSSVariables.size());
		Assert.assertEquals("#000", rootCSSVariables.get("--primary"));
		Assert.assertEquals("#111", rootCSSVariables.get("--secondary"));

		ScopedCSSVariables calmScopedCSSVariables = scopedCSSVariablesList.get(
			1);

		Assert.assertNull(calmScopedCSSVariables.getColorScheme());
		Assert.assertNull(calmScopedCSSVariables.getMediaQuery());
		Assert.assertEquals(calmScope, calmScopedCSSVariables.getScope());

		Map<String, String> calmCSSVariables =
			calmScopedCSSVariables.getCSSVariables();

		Assert.assertEquals("#ccc", calmCSSVariables.get("--primary"));
		Assert.assertEquals("#111", calmCSSVariables.get("--secondary"));

		ScopedCSSVariables darkMediaScopedCSSVariables =
			scopedCSSVariablesList.get(2);

		Assert.assertEquals(
			"dark", darkMediaScopedCSSVariables.getColorScheme());
		Assert.assertEquals(
			"(prefers-color-scheme: dark)",
			darkMediaScopedCSSVariables.getMediaQuery());
		Assert.assertEquals(
			":root:not([data-color-scheme])",
			darkMediaScopedCSSVariables.getScope());

		ScopedCSSVariables darkScopedCSSVariables = scopedCSSVariablesList.get(
			3);

		Assert.assertEquals("dark", darkScopedCSSVariables.getColorScheme());
		Assert.assertNull(darkScopedCSSVariables.getMediaQuery());
		Assert.assertEquals(darkScope, darkScopedCSSVariables.getScope());

		Map<String, String> darkCSSVariables =
			darkScopedCSSVariables.getCSSVariables();

		Assert.assertEquals("#fff", darkCSSVariables.get("--primary"));
		Assert.assertEquals("#111", darkCSSVariables.get("--secondary"));
	}

	private class TestStyleBookScopedCSSVariablesProvider
		extends StyleBookScopedCSSVariablesProvider {

		public TestStyleBookScopedCSSVariablesProvider(
			String frontendTokensValues) {

			_frontendTokensValues = frontendTokensValues;
		}

		@Override
		protected String getFrontendTokensValues(
			HttpServletRequest httpServletRequest) {

			return _frontendTokensValues;
		}

		private final String _frontendTokensValues;

	}

}