/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_10_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Mario Leandro
 */
public class StyleBookEntryDefaultStyleBookEntryUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_unsetDuplicateDefaultStyleBookEntries();
		_syncDraftDefaultStyleBookEntries();
	}

	private void _syncDraftDefaultStyleBookEntries() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select ctCollectionId, styleBookEntryId, " +
					"defaultStyleBookEntry from StyleBookEntry where head = ?");
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update StyleBookEntry set defaultStyleBookEntry = ? " +
						"where ctCollectionId = ? and headId = ? and " +
							"defaultStyleBookEntry = ?")) {

			preparedStatement1.setBoolean(1, true);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					boolean defaultStyleBookEntry = resultSet.getBoolean(
						"defaultStyleBookEntry");

					preparedStatement2.setBoolean(1, defaultStyleBookEntry);

					preparedStatement2.setLong(
						2, resultSet.getLong("ctCollectionId"));
					preparedStatement2.setLong(
						3, resultSet.getLong("styleBookEntryId"));
					preparedStatement2.setBoolean(4, !defaultStyleBookEntry);

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

	private void _unsetDuplicateDefaultStyleBookEntries() throws Exception {
		Set<String> groupIdAndThemeIds = new HashSet<>();

		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				StringBundler.concat(
					"select styleBookEntryId, groupId, themeId from ",
					"StyleBookEntry where ctCollectionId = 0 and head = ? and ",
					"defaultStyleBookEntry = ? order by createDate desc, ",
					"styleBookEntryId desc"));
			PreparedStatement preparedStatement2 =
				AutoBatchPreparedStatementUtil.autoBatch(
					connection,
					"update StyleBookEntry set defaultStyleBookEntry = ? " +
						"where headId = ? or styleBookEntryId = ?")) {

			preparedStatement1.setBoolean(1, true);
			preparedStatement1.setBoolean(2, true);

			try (ResultSet resultSet = preparedStatement1.executeQuery()) {
				while (resultSet.next()) {
					if (groupIdAndThemeIds.add(
							resultSet.getLong("groupId") + StringPool.POUND +
								resultSet.getString("themeId"))) {

						continue;
					}

					long styleBookEntryId = resultSet.getLong(
						"styleBookEntryId");

					preparedStatement2.setBoolean(1, false);
					preparedStatement2.setLong(2, styleBookEntryId);
					preparedStatement2.setLong(3, styleBookEntryId);

					preparedStatement2.addBatch();
				}
			}

			preparedStatement2.executeBatch();
		}
	}

}