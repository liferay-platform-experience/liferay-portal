/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_9_0.util;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;

/**
 * @author Brian Wing Shun Chan
 * @generated
 * @see com.liferay.portal.tools.upgrade.table.builder.UpgradeTableBuilder
 */
public class StyleBookTokenCategoryTable {

	public static UpgradeProcess create() {
		return new UpgradeProcess() {

			@Override
			protected void doUpgrade() throws Exception {
				if (!hasTable(_TABLE_NAME)) {
					runSQL(_TABLE_SQL_CREATE);
				}
			}

		};
	}

	private static final String _TABLE_NAME = "StyleBookTokenCategory";

	private static final String _TABLE_SQL_CREATE =
		"create table StyleBookTokenCategory (mvccVersion LONG default 0 not null,ctCollectionId LONG default 0 not null,styleBookTokenCategoryId LONG not null,groupId LONG,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,styleBookEntryId LONG,themeFrontendTokenDefinitionId VARCHAR(75) null,name VARCHAR(75) null,description STRING null,primary key (styleBookTokenCategoryId, ctCollectionId))";

}