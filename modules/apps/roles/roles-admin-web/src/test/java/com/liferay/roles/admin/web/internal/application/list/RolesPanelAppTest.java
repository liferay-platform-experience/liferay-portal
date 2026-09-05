/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.web.internal.role.type.contributor.util.RoleTypeContributorRetrieverUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class RolesPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_rolesPanelApp, "_language", _language);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_language.get(Mockito.eq(LocaleUtil.ENGLISH), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		Mockito.when(
			_language.get(Mockito.eq(LocaleUtil.SPAIN), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1) + "-es"
		);

		Mockito.when(
			_regularRoleTypeContributor.getTabTitle(Mockito.any(Locale.class))
		).thenReturn(
			"regular-roles"
		);

		Mockito.when(
			_regularRoleTypeContributor.getType()
		).thenReturn(
			1
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.SPAIN
		);

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			_permissionChecker
		);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		try (MockedStatic<RoleTypeContributorRetrieverUtil> mockedStatic =
				Mockito.mockStatic(RoleTypeContributorRetrieverUtil.class)) {

			mockedStatic.when(
				() -> RoleTypeContributorRetrieverUtil.getRoleTypeContributors(
					Mockito.any(PermissionChecker.class), Mockito.any())
			).thenReturn(
				List.of(_regularRoleTypeContributor)
			);

			List<PanelAppNavigationItem> panelAppNavigationItems =
				_rolesPanelApp.getPanelAppNavigationItems(_httpServletRequest);

			Assert.assertEquals(
				panelAppNavigationItems.toString(), 1,
				panelAppNavigationItems.size());

			PanelAppNavigationItem panelAppNavigationItem =
				panelAppNavigationItems.get(0);

			Assert.assertEquals(
				"regular-roles", panelAppNavigationItem.getCanonicalName());
			Assert.assertEquals(
				"regular-roles-es", panelAppNavigationItem.getLabel());

			String href = panelAppNavigationItem.getHref();

			Assert.assertTrue(href, href.contains("roleType=1"));
		}
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final RoleTypeContributor _regularRoleTypeContributor =
		Mockito.mock(RoleTypeContributor.class);

	private final RolesPanelApp _rolesPanelApp = new RolesPanelApp() {

		@Override
		public PortletURL getPortletURL(HttpServletRequest httpServletRequest) {
			return _portletURL;
		}

		private final PortletURL _portletURL = new MockLiferayPortletURL();

	};

	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}