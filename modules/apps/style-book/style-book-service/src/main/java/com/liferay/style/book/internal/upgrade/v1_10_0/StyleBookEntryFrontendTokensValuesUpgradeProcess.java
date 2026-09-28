/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_10_0;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;

/**
 * @author Thiago Buarque
 */
public class StyleBookEntryFrontendTokensValuesUpgradeProcess
	extends UpgradeProcess {

	public StyleBookEntryFrontendTokensValuesUpgradeProcess(
		FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry) {

		_frontendTokenDefinitionRegistry = frontendTokenDefinitionRegistry;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeFrontendTokensValues("styleBookEntryId", "StyleBookEntry");
		_upgradeFrontendTokensValues(
			"styleBookEntryVersionId", "StyleBookEntryVersion");
	}

	private String _getCSSVariableMapping(
		long companyId, JSONObject frontendTokensValuesJSONObject, String name,
		String themeId) {

		JSONObject frontendTokenValueJSONObject =
			frontendTokensValuesJSONObject.getJSONObject(name);

		if ((frontendTokenValueJSONObject != null) &&
			Validator.isNotNull(
				frontendTokenValueJSONObject.getString("cssVariableMapping"))) {

			return frontendTokenValueJSONObject.getString("cssVariableMapping");
		}

		String frontendTokenName = name;

		int index = name.indexOf(CharPool.COLON);

		if (index != -1) {
			frontendTokenName = name.substring(index + 1);
			themeId = name.substring(0, index);
		}

		FrontendTokenDefinition frontendTokenDefinition =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(
				companyId, themeId);

		if (frontendTokenDefinition == null) {
			return null;
		}

		for (FrontendToken frontendToken :
				frontendTokenDefinition.getFrontendTokens()) {

			if (!Objects.equals(frontendToken.getName(), frontendTokenName)) {
				continue;
			}

			for (FrontendTokenMapping frontendTokenMapping :
					frontendToken.getFrontendTokenMappings(
						FrontendTokenMapping.TYPE_CSS_VARIABLE)) {

				return frontendTokenMapping.getValue();
			}
		}

		return null;
	}

	private void _upgradeFrontendTokensValues(
			String primaryKeyColumnName, String tableName)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ", primaryKeyColumnName, ", companyId, ",
					"ctCollectionId, frontendTokensValues, themeId from ",
					tableName));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update ", tableName, " set frontendTokensValues = ? ",
						"where ", primaryKeyColumnName, " = ? and ",
						"ctCollectionId = ?"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String frontendTokensValues = resultSet.getString(
					"frontendTokensValues");

				if (Validator.isNull(frontendTokensValues)) {
					continue;
				}

				boolean modified = false;

				JSONObject frontendTokensValuesJSONObject =
					JSONFactoryUtil.createJSONObject(frontendTokensValues);

				for (String key : frontendTokensValuesJSONObject.keySet()) {
					JSONObject frontendTokenValueJSONObject =
						frontendTokensValuesJSONObject.getJSONObject(key);

					String name = frontendTokenValueJSONObject.getString(
						"name");

					if (Validator.isNull(name)) {
						continue;
					}

					String cssVariableMapping = _getCSSVariableMapping(
						resultSet.getLong("companyId"),
						frontendTokensValuesJSONObject, name,
						resultSet.getString("themeId"));

					if (cssVariableMapping == null) {
						continue;
					}

					frontendTokenValueJSONObject.put(
						"value", "var(--" + cssVariableMapping + ")");

					modified = true;
				}

				if (!modified) {
					continue;
				}

				preparedStatement2.setString(
					1, frontendTokensValuesJSONObject.toString());
				preparedStatement2.setLong(
					2, resultSet.getLong(primaryKeyColumnName));
				preparedStatement2.setLong(
					3, resultSet.getLong("ctCollectionId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private final FrontendTokenDefinitionRegistry
		_frontendTokenDefinitionRegistry;

}