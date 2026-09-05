/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.admin.web.internal.util;

import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class SearchAdminNavigationUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetTabs1NamesWithOmniadmin() {
		Assert.assertEquals(
			Arrays.asList(
				SearchAdminNavigationUtil.TABS1_CONNECTIONS,
				SearchAdminNavigationUtil.TABS1_INDEX_ACTIONS,
				SearchAdminNavigationUtil.TABS1_FIELD_MAPPINGS),
			SearchAdminNavigationUtil.getTabs1Names(
				true, _getPermissionChecker(true)));
	}

	@Test
	public void testGetTabs1NamesWithOmniadminAndNoIndexInformation() {
		Assert.assertEquals(
			Arrays.asList(
				SearchAdminNavigationUtil.TABS1_CONNECTIONS,
				SearchAdminNavigationUtil.TABS1_INDEX_ACTIONS),
			SearchAdminNavigationUtil.getTabs1Names(
				false, _getPermissionChecker(true)));
	}

	@Test
	public void testGetTabs1NamesWithoutOmniadmin() {
		Assert.assertEquals(
			Arrays.asList(SearchAdminNavigationUtil.TABS1_INDEX_ACTIONS),
			SearchAdminNavigationUtil.getTabs1Names(
				true, _getPermissionChecker(false)));
	}

	private PermissionChecker _getPermissionChecker(boolean omniadmin) {
		PermissionChecker permissionChecker = Mockito.mock(
			PermissionChecker.class);

		Mockito.when(
			permissionChecker.isOmniadmin()
		).thenReturn(
			omniadmin
		);

		return permissionChecker;
	}

}