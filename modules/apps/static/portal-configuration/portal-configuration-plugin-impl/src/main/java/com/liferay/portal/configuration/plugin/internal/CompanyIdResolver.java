/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.plugin.internal;

import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Objects;

import javax.sql.DataSource;

/**
 * @author Mario Leandro
 */
public class CompanyIdResolver {

	public static Long getCompanyId(DataSource dataSource, String webId)
		throws Exception {

		if (Objects.equals(webId, "default")) {
			webId = PropsValues.COMPANY_DEFAULT_WEB_ID;
		}

		try (Connection connection = dataSource.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				_db.buildSQL(
					"select companyId from Company where webId = ?"))) {

			preparedStatement.setString(1, webId);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getLong("companyId");
				}
			}
		}

		return null;
	}

	private static final DB _db = DBManagerUtil.getDB();

}