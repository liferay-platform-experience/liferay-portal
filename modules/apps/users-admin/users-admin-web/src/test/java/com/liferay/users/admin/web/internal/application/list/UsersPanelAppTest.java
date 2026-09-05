/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationRegistryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Mario Leandro
 */
public class UsersPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Bundle bundle = Mockito.mock(Bundle.class);

		Mockito.when(
			bundle.getBundleContext()
		).thenReturn(
			Mockito.mock(BundleContext.class)
		);

		_frameworkUtilMockedStatic.when(
			() -> FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundle
		);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_screenNavigationCategory.getCategoryKey()
		).thenReturn(
			"organizations"
		);

		Mockito.when(
			_screenNavigationCategory.getLabel(LocaleUtil.ENGLISH)
		).thenReturn(
			"Organizations"
		);

		Mockito.when(
			_screenNavigationCategory.getLabel(LocaleUtil.SPAIN)
		).thenReturn(
			"Organizations-es"
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.SPAIN
		);

		Mockito.when(
			_themeDisplay.getUser()
		).thenReturn(
			_user
		);
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		try (MockedStatic<ScreenNavigationRegistryUtil> mockedStatic =
				Mockito.mockStatic(ScreenNavigationRegistryUtil.class)) {

			mockedStatic.when(
				() ->
					ScreenNavigationRegistryUtil.getScreenNavigationCategories(
						Mockito.anyString(), Mockito.any(), Mockito.any())
			).thenReturn(
				List.of(_screenNavigationCategory)
			);

			List<PanelAppNavigationItem> panelAppNavigationItems =
				_usersPanelApp.getPanelAppNavigationItems(_httpServletRequest);

			Assert.assertEquals(
				panelAppNavigationItems.toString(), 1,
				panelAppNavigationItems.size());

			PanelAppNavigationItem panelAppNavigationItem =
				panelAppNavigationItems.get(0);

			Assert.assertEquals(
				"Organizations", panelAppNavigationItem.getCanonicalName());
			Assert.assertEquals(
				"Organizations-es", panelAppNavigationItem.getLabel());

			String href = panelAppNavigationItem.getHref();

			Assert.assertTrue(
				href,
				href.contains("screenNavigationCategoryKey=organizations"));
		}
	}

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final ScreenNavigationCategory _screenNavigationCategory =
		Mockito.mock(ScreenNavigationCategory.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);
	private final User _user = Mockito.mock(User.class);

	private final UsersPanelApp _usersPanelApp = new UsersPanelApp() {

		@Override
		public PortletURL getPortletURL(HttpServletRequest httpServletRequest) {
			return _portletURL;
		}

		private final PortletURL _portletURL = new MockLiferayPortletURL();

	};

}