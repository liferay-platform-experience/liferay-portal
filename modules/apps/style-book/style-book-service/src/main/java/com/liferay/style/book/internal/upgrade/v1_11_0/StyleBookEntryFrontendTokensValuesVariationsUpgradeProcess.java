/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_11_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Marcos Castro
 */
public class StyleBookEntryFrontendTokensValuesVariationsUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_doUpgrade("styleBookEntryId", "StyleBookEntry");
		_doUpgrade("styleBookEntryVersionId", "StyleBookEntryVersion");
	}

	private void _doUpgrade(String primaryKeyColumnName, String tableName)
		throws Exception {

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select ctCollectionId, ", primaryKeyColumnName,
					", frontendTokensValues from ", tableName,
					" where frontendTokensValues is not null"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					StringBundler.concat(
						"update ", tableName,
						" set frontendTokensValues = ? where ctCollectionId = ",
						"? and ", primaryKeyColumnName, " = ?"));
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				String frontendTokensValues = resultSet.getString(
					"frontendTokensValues");

				if (Validator.isBlank(frontendTokensValues)) {
					continue;
				}

				JSONObject frontendTokensValuesJSONObject = null;

				try {
					frontendTokensValuesJSONObject =
						JSONFactoryUtil.createJSONObject(frontendTokensValues);
				}
				catch (JSONException jsonException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Unable to parse frontend tokens values of ",
								tableName, " with primary key ",
								resultSet.getLong(primaryKeyColumnName)),
							jsonException);
					}

					continue;
				}

				JSONObject defaultVariationJSONObject =
					frontendTokensValuesJSONObject.getJSONObject("default");

				if ((defaultVariationJSONObject != null) &&
					(defaultVariationJSONObject.getJSONObject("tokens") !=
						null)) {

					continue;
				}

				preparedStatement2.setString(
					1,
					JSONUtil.put(
						"default",
						JSONUtil.put(
							"name", "default"
						).put(
							"tokens", frontendTokensValuesJSONObject
						)
					).toString());
				preparedStatement2.setLong(
					2, resultSet.getLong("ctCollectionId"));
				preparedStatement2.setLong(
					3, resultSet.getLong(primaryKeyColumnName));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookEntryFrontendTokensValuesVariationsUpgradeProcess.class);

}