/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.exception.StyleBookEntryFrontendTokenDefinitionException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Gabriel Lima
 */
@RunWith(Arquillian.class)
public class AddStyleBookEntryFrontendTokenMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = new ServiceContext();

		_serviceContext.setScopeGroupId(_group.getGroupId());
		_serviceContext.setUserId(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		_themeDisplay.setLanguageId(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()));
		_themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		_themeDisplay.setRealUser(TestPropsValues.getUser());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_themeDisplay.setRequest(mockHttpServletRequest);
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testAddStyleBookEntryFrontendToken() throws Exception {
		_testAddStyleBookEntryFrontendTokenWithLabelStartingWithNumber();
		_testAddStyleBookEntryFrontendTokenWithPunctuationInLabel();
	}

	private StyleBookEntry _addStyleBookEntry(String themeId) throws Exception {
		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			StringPool.BLANK, RandomTestUtil.randomString(), StringPool.BLANK,
			themeId, _serviceContext);
	}

	private JSONObject _getFrontendTokenCategoryJSONObject(
			String frontendTokenDefinition, String categoryName)
		throws Exception {

		JSONObject frontendTokenDefinitionJSONObject =
			JSONFactoryUtil.createJSONObject(frontendTokenDefinition);

		JSONArray frontendTokenCategoriesJSONArray =
			frontendTokenDefinitionJSONObject.getJSONArray(
				"frontendTokenCategories");

		if (frontendTokenCategoriesJSONArray == null) {
			return null;
		}

		for (int i = 0; i < frontendTokenCategoriesJSONArray.length(); i++) {
			JSONObject frontendTokenCategoryJSONObject =
				frontendTokenCategoriesJSONArray.getJSONObject(i);

			if (categoryName.equals(
					frontendTokenCategoryJSONObject.getString("name"))) {

				return frontendTokenCategoryJSONObject;
			}
		}

		return null;
	}

	private JSONObject _getFrontendTokenDefinitionJSONObject(
			String frontendTokenDefinitionsResponse, String themeId)
		throws Exception {

		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			frontendTokenDefinitionsResponse);

		JSONArray frontendTokenDefinitionsJSONArray =
			responseJSONObject.getJSONArray("frontendTokenDefinitions");

		if (frontendTokenDefinitionsJSONArray == null) {
			return null;
		}

		for (int i = 0; i < frontendTokenDefinitionsJSONArray.length(); i++) {
			JSONObject frontendTokenDefinitionJSONObject =
				frontendTokenDefinitionsJSONArray.getJSONObject(i);

			if (themeId.equals(
					frontendTokenDefinitionJSONObject.getString("id"))) {

				return frontendTokenDefinitionJSONObject;
			}
		}

		return null;
	}

	private JSONObject _getFrontendTokenJSONObject(
			String frontendTokenDefinition, String categoryName,
			String tokenSetName, String name)
		throws Exception {

		JSONObject frontendTokenSetJSONObject = _getFrontendTokenSetJSONObject(
			frontendTokenDefinition, categoryName, tokenSetName);

		if (frontendTokenSetJSONObject == null) {
			return null;
		}

		JSONArray frontendTokensJSONArray =
			frontendTokenSetJSONObject.getJSONArray("frontendTokens");

		if (frontendTokensJSONArray == null) {
			return null;
		}

		for (int i = 0; i < frontendTokensJSONArray.length(); i++) {
			JSONObject frontendTokenJSONObject =
				frontendTokensJSONArray.getJSONObject(i);

			if (name.equals(frontendTokenJSONObject.getString("name"))) {
				return frontendTokenJSONObject;
			}
		}

		return null;
	}

	private JSONObject _getFrontendTokenSetJSONObject(
			String frontendTokenDefinition, String categoryName,
			String tokenSetName)
		throws Exception {

		JSONObject frontendTokenCategoryJSONObject =
			_getFrontendTokenCategoryJSONObject(
				frontendTokenDefinition, categoryName);

		if (frontendTokenCategoryJSONObject == null) {
			return null;
		}

		JSONArray frontendTokenSetsJSONArray =
			frontendTokenCategoryJSONObject.getJSONArray("frontendTokenSets");

		if (frontendTokenSetsJSONArray == null) {
			return null;
		}

		for (int i = 0; i < frontendTokenSetsJSONArray.length(); i++) {
			JSONObject frontendTokenSetJSONObject =
				frontendTokenSetsJSONArray.getJSONObject(i);

			if (tokenSetName.equals(
					frontendTokenSetJSONObject.getString("name"))) {

				return frontendTokenSetJSONObject;
			}
		}

		return null;
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
		long styleBookEntryId, String categoryName, String description,
		String editorType, String label, String tokenSetName, String value) {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.addParameter(
			"categoryName", categoryName);
		mockLiferayPortletActionRequest.addParameter(
			"description", description);
		mockLiferayPortletActionRequest.addParameter("editorType", editorType);
		mockLiferayPortletActionRequest.addParameter("label", label);
		mockLiferayPortletActionRequest.addParameter(
			"styleBookEntryId", String.valueOf(styleBookEntryId));
		mockLiferayPortletActionRequest.addParameter(
			"tokenSetName", tokenSetName);
		mockLiferayPortletActionRequest.addParameter("value", value);
		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		return mockLiferayPortletActionRequest;
	}

	private void _testAddStyleBookEntryFrontendTokenWithLabelStartingWithNumber()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			RandomTestUtil.randomString());

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				styleBookEntry.getStyleBookEntryId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				"Default", "3D Depth", RandomTestUtil.randomString(),
				RandomTestUtil.randomString());

		AssertUtils.assertFailure(
			StyleBookEntryFrontendTokenDefinitionException.class,
			"Frontend token name \"3DDepth\" is invalid",
			() -> ReflectionTestUtil.invoke(
				_addStyleBookEntryFrontendTokenMVCActionCommandTest,
				"_addStyleBookEntryFrontendToken",
				new Class<?>[] {ActionRequest.class},
				mockLiferayPortletActionRequest));

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		ReflectionTestUtil.invoke(
			_addStyleBookEntryFrontendTokenMVCActionCommandTest,
			"doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			mockHttpServletResponse.getContentAsString());

		Assert.assertEquals(
			LanguageUtil.get(
				_themeDisplay.getRequest(), "please-enter-a-valid-name"),
			jsonObject.getString("error"));
	}

	private void _testAddStyleBookEntryFrontendTokenWithPunctuationInLabel()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(_THEME_ID_CLASSIC);

		String categoryName = RandomTestUtil.randomString();
		String description = RandomTestUtil.randomString();
		String name = "backgroundredbodycolorred";
		String tokenSetName = RandomTestUtil.randomString();
		String value = RandomTestUtil.randomString();

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				styleBookEntry.getStyleBookEntryId(), categoryName, description,
				"Default", "background:red}body{color:red", tokenSetName,
				value);

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			new MockLiferayPortletActionResponse();

		ReflectionTestUtil.invoke(
			_addStyleBookEntryFrontendTokenMVCActionCommandTest,
			"doProcessAction",
			new Class<?>[] {ActionRequest.class, ActionResponse.class},
			mockLiferayPortletActionRequest, mockLiferayPortletActionResponse);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		JSONObject frontendTokenDefinitionJSONObject =
			_getFrontendTokenDefinitionJSONObject(
				mockHttpServletResponse.getContentAsString(),
				_THEME_ID_CLASSIC);

		Assert.assertNotNull(frontendTokenDefinitionJSONObject);

		JSONObject frontendTokenJSONObject = _getFrontendTokenJSONObject(
			frontendTokenDefinitionJSONObject.toString(), categoryName,
			tokenSetName, name);

		Assert.assertNotNull(frontendTokenJSONObject);

		Assert.assertEquals(
			description, frontendTokenJSONObject.getString("description"));
		Assert.assertFalse(frontendTokenJSONObject.has("editorType"));

		String cssVariableMappingValue = ReflectionTestUtil.invoke(
			_addStyleBookEntryFrontendTokenMVCActionCommandTest,
			"_getCssVariableMappingValue", new Class<?>[] {String.class}, name);

		JSONArray mappingsJSONArray = frontendTokenJSONObject.getJSONArray(
			"mappings");

		JSONObject mappingJSONObject = mappingsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			cssVariableMappingValue, mappingJSONObject.getString("value"));

		StyleBookEntry updatedStyleBookEntry =
			_styleBookEntryLocalService.getStyleBookEntry(
				styleBookEntry.getStyleBookEntryId());

		JSONObject persistedFrontendTokenJSONObject =
			_getFrontendTokenJSONObject(
				updatedStyleBookEntry.getFrontendTokenDefinition(),
				categoryName, tokenSetName, name);

		Assert.assertNotNull(persistedFrontendTokenJSONObject);

		JSONArray persistedMappingsJSONArray =
			persistedFrontendTokenJSONObject.getJSONArray("mappings");

		Assert.assertEquals(
			cssVariableMappingValue,
			persistedMappingsJSONArray.getJSONObject(
				0
			).getString(
				"value"
			));
	}

	private static final String _THEME_ID_CLASSIC = "classic_WAR_classictheme";

	@Inject(
		filter = "mvc.command.name=/style_book/add_style_book_entry_frontend_token"
	)
	private MVCActionCommand
		_addStyleBookEntryFrontendTokenMVCActionCommandTest;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	private ThemeDisplay _themeDisplay;

}