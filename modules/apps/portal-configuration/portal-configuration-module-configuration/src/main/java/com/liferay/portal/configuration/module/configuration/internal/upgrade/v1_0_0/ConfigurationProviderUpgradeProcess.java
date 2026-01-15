/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.module.configuration.internal.upgrade.v1_0_0;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.portal.kernel.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Dictionary;

import org.apache.felix.cm.file.ConfigurationHandler;

/**
 * @author Thiago Buarque
 */
public class ConfigurationProviderUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				"select configurationId, dictionary from Configuration_ " +
					"where dictionary like '%groupId=%'");
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"update Configuration_ set dictionary = ? where " +
					"configurationId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				Dictionary<String, Object> dictionary = _toDictionary(
					resultSet.getString(2));

				dictionary.put(
					ExtendedObjectClassDefinition.Scope.COMPANY.
						getPropertyKey(),
					CompanyThreadLocal.getCompanyId());

				preparedStatement2.setString(1, _toString(dictionary));

				preparedStatement2.setString(2, resultSet.getString(1));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	@Override
	protected boolean isSkipUpgradeProcess() {
		return !PropsValues.DATABASE_PARTITION_ENABLED;
	}

	private Dictionary<String, Object> _toDictionary(String dictionaryString)
		throws IOException {

		UnsyncByteArrayInputStream unsyncByteArrayInputStream =
			new UnsyncByteArrayInputStream(
				dictionaryString.getBytes(StandardCharsets.UTF_8));

		return ConfigurationHandler.read(unsyncByteArrayInputStream);
	}

	private String _toString(Dictionary<String, Object> dictionary)
		throws IOException {

		UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
			new UnsyncByteArrayOutputStream();

		ConfigurationHandler.write(unsyncByteArrayOutputStream, dictionary);

		return new String(
			unsyncByteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
	}

}