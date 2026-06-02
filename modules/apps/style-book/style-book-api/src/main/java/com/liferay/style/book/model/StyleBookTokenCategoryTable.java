/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;StyleBookTokenCategory&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookTokenCategory
 * @generated
 */
public class StyleBookTokenCategoryTable
	extends BaseTable<StyleBookTokenCategoryTable> {

	public static final StyleBookTokenCategoryTable INSTANCE =
		new StyleBookTokenCategoryTable();

	public final Column<StyleBookTokenCategoryTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<StyleBookTokenCategoryTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<StyleBookTokenCategoryTable, Long>
		styleBookTokenCategoryId = createColumn(
			"styleBookTokenCategoryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<StyleBookTokenCategoryTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookTokenCategoryTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookTokenCategoryTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookTokenCategoryTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<StyleBookTokenCategoryTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<StyleBookTokenCategoryTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<StyleBookTokenCategoryTable, Long> styleBookEntryId =
		createColumn(
			"styleBookEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<StyleBookTokenCategoryTable, String>
		themeFrontendTokenDefinitionId = createColumn(
			"themeFrontendTokenDefinitionId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<StyleBookTokenCategoryTable, String> name =
		createColumn("name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<StyleBookTokenCategoryTable, String> description =
		createColumn(
			"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private StyleBookTokenCategoryTable() {
		super("StyleBookTokenCategory", StyleBookTokenCategoryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:924205137