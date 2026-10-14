/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_11_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
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

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Marcos Castro
 */
@RunWith(Arquillian.class)
public class StyleBookEntryFrontendTokensValuesVariationsUpgradeProcessTest {

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
		String themeId = RandomTestUtil.randomString();

		String flatFrontendTokensValues = JSONUtil.put(
			themeId + ":token1",
			JSONUtil.put(
				"cssVariableMapping", "token-1"
			).put(
				"tokenDefinitionId", themeId
			).put(
				"value", "#000"
			)
		).toString();

		StyleBookEntry flatStyleBookEntry = _addStyleBookEntry(themeId);

		_updateFrontendTokensValues(
			flatFrontendTokensValues, flatStyleBookEntry.getStyleBookEntryId(),
			"StyleBookEntry");
		_updateFrontendTokensValues(
			flatFrontendTokensValues, flatStyleBookEntry.getStyleBookEntryId(),
			"StyleBookEntryVersion");

		String variationsFrontendTokensValues = JSONUtil.put(
			"dark",
			JSONUtil.put(
				"name", "Dark"
			).put(
				"tokens",
				JSONUtil.put(themeId + ":token1", JSONUtil.put("value", "#fff"))
			)
		).put(
			"default",
			JSONUtil.put(
				"name", "default"
			).put(
				"tokens",
				JSONUtil.put(themeId + ":token1", JSONUtil.put("value", "#000"))
			)
		).toString();

		StyleBookEntry variationsStyleBookEntry = _addStyleBookEntry(themeId);

		_updateFrontendTokensValues(
			variationsFrontendTokensValues,
			variationsStyleBookEntry.getStyleBookEntryId(), "StyleBookEntry");
		_updateFrontendTokensValues(
			variationsFrontendTokensValues,
			variationsStyleBookEntry.getStyleBookEntryId(),
			"StyleBookEntryVersion");

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.style.book.internal.upgrade.v1_11_0." +
				"StyleBookEntryFrontendTokensValuesVariationsUpgradeProcess");

		upgradeProcess.upgrade();

		String expectedFrontendTokensValues = JSONUtil.put(
			"default",
			JSONUtil.put(
				"name", "default"
			).put(
				"tokens",
				JSONUtil.put(
					themeId + ":token1",
					JSONUtil.put(
						"cssVariableMapping", "token-1"
					).put(
						"tokenDefinitionId", themeId
					).put(
						"value", "#000"
					))
			)
		).toString();

		JSONAssert.assertEquals(
			expectedFrontendTokensValues,
			_getFrontendTokensValues(
				flatStyleBookEntry.getStyleBookEntryId(), "StyleBookEntry"),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			expectedFrontendTokensValues,
			_getFrontendTokensValues(
				flatStyleBookEntry.getStyleBookEntryId(),
				"StyleBookEntryVersion"),
			JSONCompareMode.STRICT);

		JSONAssert.assertEquals(
			variationsFrontendTokensValues,
			_getFrontendTokensValues(
				variationsStyleBookEntry.getStyleBookEntryId(),
				"StyleBookEntry"),
			JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			variationsFrontendTokensValues,
			_getFrontendTokensValues(
				variationsStyleBookEntry.getStyleBookEntryId(),
				"StyleBookEntryVersion"),
			JSONCompareMode.STRICT);
	}

	private StyleBookEntry _addStyleBookEntry(String themeId) throws Exception {
		return _styleBookEntryLocalService.addStyleBookEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(), false, null,
			null, RandomTestUtil.randomString(), null, themeId,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	private String _getFrontendTokensValues(
			long styleBookEntryId, String tableName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"select frontendTokensValues from ", tableName,
					" where styleBookEntryId = ?"))) {

			preparedStatement.setLong(1, styleBookEntryId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				return resultSet.getString("frontendTokensValues");
			}
		}
	}

	private void _updateFrontendTokensValues(
			String frontendTokensValues, long styleBookEntryId,
			String tableName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update ", tableName,
					" set frontendTokensValues = ? where styleBookEntryId = ",
					"?"))) {

			preparedStatement.setString(1, frontendTokensValues);
			preparedStatement.setLong(2, styleBookEntryId);

			preparedStatement.executeUpdate();
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.style.book.internal.upgrade.registry.StyleBookServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}