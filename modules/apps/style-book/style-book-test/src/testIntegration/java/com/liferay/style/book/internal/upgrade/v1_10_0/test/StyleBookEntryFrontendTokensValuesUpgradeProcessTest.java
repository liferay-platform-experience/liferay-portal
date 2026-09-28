/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_10_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class StyleBookEntryFrontendTokensValuesUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgrade() throws Exception {
		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				null, TestPropsValues.getUserId(), _group.getGroupId(), false,
				null,
				JSONUtil.put(
					_THEME_ID + ":bodyColor",
					_getFrontendTokenValueJSONObject(
						"body-color", "brandColor3", "#333333")
				).put(
					_THEME_ID + ":brandColor1",
					_getFrontendTokenValueJSONObject(
						"brand-color-1", null, "#ff0000")
				).put(
					_THEME_ID + ":btnLinkColor",
					_getFrontendTokenValueJSONObject(
						"btn-link-color", _THEME_ID + ":brandColor1", "#ff0000")
				).put(
					_THEME_ID + ":btnLinkHoverColor",
					_getFrontendTokenValueJSONObject(
						"btn-link-hover-color", _THEME_ID + ":brandColor2",
						"#6b6c7e")
				).toString(),
				RandomTestUtil.randomString(), null, _THEME_ID,
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		_runUpgrade();

		_assertFrontendTokensValues(
			"select frontendTokensValues from StyleBookEntry where " +
				"styleBookEntryId = ?",
			styleBookEntry.getStyleBookEntryId());
		_assertFrontendTokensValues(
			"select frontendTokensValues from StyleBookEntryVersion where " +
				"styleBookEntryId = ?",
			styleBookEntry.getStyleBookEntryId());
	}

	private void _assertFrontendTokensValues(String sql, long styleBookEntryId)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				sql)) {

			preparedStatement.setLong(1, styleBookEntryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				JSONObject frontendTokensValuesJSONObject =
					JSONFactoryUtil.createJSONObject(
						resultSet.getString("frontendTokensValues"));

				Assert.assertEquals(
					"var(--brand-color-3)",
					_getValue(frontendTokensValuesJSONObject, "bodyColor"));
				Assert.assertEquals(
					"#ff0000",
					_getValue(frontendTokensValuesJSONObject, "brandColor1"));
				Assert.assertEquals(
					"var(--brand-color-1)",
					_getValue(frontendTokensValuesJSONObject, "btnLinkColor"));
				Assert.assertEquals(
					"var(--brand-color-2)",
					_getValue(
						frontendTokensValuesJSONObject, "btnLinkHoverColor"));

				Assert.assertFalse(resultSet.next());
			}
		}
	}

	private JSONObject _getFrontendTokenValueJSONObject(
		String cssVariableMapping, String name, String value) {

		return JSONUtil.put(
			"cssVariableMapping", cssVariableMapping
		).put(
			"name", name
		).put(
			"tokenDefinitionId", _THEME_ID
		).put(
			"value", value
		);
	}

	private String _getValue(
		JSONObject frontendTokensValuesJSONObject, String name) {

		JSONObject frontendTokenValueJSONObject =
			frontendTokensValuesJSONObject.getJSONObject(
				_THEME_ID + ":" + name);

		return frontendTokenValueJSONObject.getString("value");
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private static final String _CLASS_NAME =
		"com.liferay.style.book.internal.upgrade.v1_10_0." +
			"StyleBookEntryFrontendTokensValuesUpgradeProcess";

	private static final String _THEME_ID = "classic_WAR_classictheme";

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.style.book.internal.upgrade.registry.StyleBookServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}