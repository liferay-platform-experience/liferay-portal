/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.admin.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.admin.web.internal.util.SearchAdminNavigationUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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
public class SearchAdminPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_searchAdminPanelApp, "_language", _language);

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
		try (MockedStatic<SearchAdminNavigationUtil> mockedStatic =
				Mockito.mockStatic(SearchAdminNavigationUtil.class)) {

			mockedStatic.when(
				() -> SearchAdminNavigationUtil.getTabs1Names(
					Mockito.anyBoolean(), Mockito.any(PermissionChecker.class))
			).thenReturn(
				List.of("connections", "index-actions")
			);

			List<PanelAppNavigationItem> panelAppNavigationItems =
				_searchAdminPanelApp.getPanelAppNavigationItems(
					_httpServletRequest);

			Assert.assertEquals(
				panelAppNavigationItems.toString(), 2,
				panelAppNavigationItems.size());

			PanelAppNavigationItem panelAppNavigationItem =
				panelAppNavigationItems.get(0);

			Assert.assertEquals(
				"connections", panelAppNavigationItem.getCanonicalName());
			Assert.assertEquals(
				"connections-es", panelAppNavigationItem.getLabel());

			String href = panelAppNavigationItem.getHref();

			Assert.assertTrue(href, href.contains("tabs1=connections"));

			panelAppNavigationItem = panelAppNavigationItems.get(1);

			Assert.assertEquals(
				"index-actions", panelAppNavigationItem.getCanonicalName());
			Assert.assertEquals(
				"index-actions-es", panelAppNavigationItem.getLabel());

			href = panelAppNavigationItem.getHref();

			Assert.assertTrue(href, href.contains("tabs1=index-actions"));
		}
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);

	private final SearchAdminPanelApp _searchAdminPanelApp =
		new SearchAdminPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}