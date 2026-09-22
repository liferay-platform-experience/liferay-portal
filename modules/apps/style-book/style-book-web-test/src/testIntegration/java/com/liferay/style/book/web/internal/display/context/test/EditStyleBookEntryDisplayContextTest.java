/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Ambrín Chaudhary
 */
@RunWith(Arquillian.class)
public class EditStyleBookEntryDisplayContextTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypePortletLayout(_group.getGroupId());

		_themeDisplay = new ThemeDisplay();

		_themeDisplay.setCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));
		_themeDisplay.setLayout(_layout);
		_themeDisplay.setLocale(LocaleUtil.getDefault());
		_themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		_themeDisplay.setRealUser(TestPropsValues.getUser());
		_themeDisplay.setScopeGroupId(_group.getGroupId());
		_themeDisplay.setSiteGroupId(_group.getGroupId());
		_themeDisplay.setUser(TestPropsValues.getUser());
	}

	@Test
	public void testGetStyleBookEditorData() throws Exception {
		_testGetStyleBookEditorDataWithBareNameTokenKey();
		_testGetStyleBookEditorDataWithBothBareAndNamespacedTokenKeys();
		_testGetStyleBookEditorDataWithNamespacedTokenKey();
	}

	private StyleBookEntry _addStyleBookEntry(
			String frontendTokensValues, String themeId)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false,
			StringPool.BLANK, frontendTokensValues,
			RandomTestUtil.randomString(), StringPool.BLANK, themeId,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private JSONObject _getFrontendTokensValuesJSONObject(
			StyleBookEntry styleBookEntry)
		throws Exception {

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest();

		mockLiferayPortletRenderRequest.addParameter(
			"styleBookEntryId",
			String.valueOf(styleBookEntry.getStyleBookEntryId()));
		mockLiferayPortletRenderRequest.addParameter("redirect", "/");
		mockLiferayPortletRenderRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _themeDisplay);

		_editStyleBookEntryMVCRenderCommand.render(
			mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());

		Map<String, Object> editorData = ReflectionTestUtil.invoke(
			mockLiferayPortletRenderRequest.getAttribute(
				"com.liferay.style.book.web.internal.display.context." +
					"EditStyleBookEntryDisplayContext"),
			"getStyleBookEditorData", new Class<?>[0]);

		return (JSONObject)editorData.get("frontendTokensValues");
	}

	private void _testGetStyleBookEditorDataWithBareNameTokenKey()
		throws Exception {

		String themeId = RandomTestUtil.randomString();
		String tokenName = RandomTestUtil.randomString();
		String tokenValue = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			JSONUtil.put(
				tokenName, JSONUtil.put("value", tokenValue)
			).toString(),
			themeId);

		JSONObject frontendTokensValuesJSONObject =
			_getFrontendTokensValuesJSONObject(styleBookEntry);

		Assert.assertFalse(frontendTokensValuesJSONObject.has(tokenName));
		Assert.assertTrue(
			frontendTokensValuesJSONObject.has(
				themeId + StringPool.COLON + tokenName));
		Assert.assertEquals(
			tokenValue,
			frontendTokensValuesJSONObject.getJSONObject(
				themeId + StringPool.COLON + tokenName
			).getString(
				"value"
			));
	}

	private void _testGetStyleBookEditorDataWithBothBareAndNamespacedTokenKeys()
		throws Exception {

		String themeId = RandomTestUtil.randomString();
		String tokenName = RandomTestUtil.randomString();

		String namespacedKey = themeId + StringPool.COLON + tokenName;

		String bareValue = RandomTestUtil.randomString();
		String namespacedValue = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			JSONUtil.put(
				namespacedKey, JSONUtil.put("value", namespacedValue)
			).put(
				tokenName, JSONUtil.put("value", bareValue)
			).toString(),
			themeId);

		JSONObject frontendTokensValuesJSONObject =
			_getFrontendTokensValuesJSONObject(styleBookEntry);

		Assert.assertFalse(frontendTokensValuesJSONObject.has(tokenName));
		Assert.assertTrue(frontendTokensValuesJSONObject.has(namespacedKey));
		Assert.assertEquals(
			namespacedValue,
			frontendTokensValuesJSONObject.getJSONObject(
				namespacedKey
			).getString(
				"value"
			));
	}

	private void _testGetStyleBookEditorDataWithNamespacedTokenKey()
		throws Exception {

		String themeId = RandomTestUtil.randomString();
		String tokenName = RandomTestUtil.randomString();

		String namespacedKey = themeId + StringPool.COLON + tokenName;

		String tokenValue = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			JSONUtil.put(
				namespacedKey, JSONUtil.put("value", tokenValue)
			).toString(),
			themeId);

		JSONObject frontendTokensValuesJSONObject =
			_getFrontendTokensValuesJSONObject(styleBookEntry);

		Assert.assertFalse(frontendTokensValuesJSONObject.has(tokenName));
		Assert.assertTrue(frontendTokensValuesJSONObject.has(namespacedKey));
		Assert.assertEquals(
			tokenValue,
			frontendTokensValuesJSONObject.getJSONObject(
				namespacedKey
			).getString(
				"value"
			));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(filter = "mvc.command.name=/style_book/edit_style_book_entry")
	private MVCRenderCommand _editStyleBookEntryMVCRenderCommand;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	private ThemeDisplay _themeDisplay;

}
