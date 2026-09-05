/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.application.list;

import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mario Leandro
 */
public class SamlAdminPanelAppTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_samlAdminPanelApp, "_language", _language);
		ReflectionTestUtil.setFieldValue(
			_samlAdminPanelApp, "_samlProviderConfigurationHelper",
			_samlProviderConfigurationHelper);

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
	}

	@Test
	public void testGetPanelAppNavigationItems() throws Exception {
		Mockito.when(
			_samlProviderConfigurationHelper.isRoleIb()
		).thenReturn(
			true
		);

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_samlAdminPanelApp.getPanelAppNavigationItems(_httpServletRequest);

		Assert.assertEquals(
			panelAppNavigationItems.toString(), 5,
			panelAppNavigationItems.size());

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(0);

		Assert.assertEquals(
			"general", panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals("general-es", panelAppNavigationItem.getLabel());

		String href = panelAppNavigationItem.getHref();

		Assert.assertTrue(href, href.contains("tabs1=general"));

		panelAppNavigationItem = panelAppNavigationItems.get(4);

		Assert.assertEquals(
			"identity-provider-connections",
			panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals(
			"identity-provider-connections-es",
			panelAppNavigationItem.getLabel());

		href = panelAppNavigationItem.getHref();

		Assert.assertTrue(
			href, href.contains("tabs1=identity-provider-connections"));
	}

	@Test
	public void testGetPanelAppNavigationItemsForServiceProviderRole()
		throws Exception {

		Mockito.when(
			_samlProviderConfigurationHelper.isRoleSp()
		).thenReturn(
			true
		);

		List<PanelAppNavigationItem> panelAppNavigationItems =
			_samlAdminPanelApp.getPanelAppNavigationItems(_httpServletRequest);

		Assert.assertEquals(
			panelAppNavigationItems.toString(), 3,
			panelAppNavigationItems.size());

		PanelAppNavigationItem panelAppNavigationItem =
			panelAppNavigationItems.get(1);

		Assert.assertEquals(
			"service-provider", panelAppNavigationItem.getCanonicalName());
		Assert.assertEquals(
			"service-provider-es", panelAppNavigationItem.getLabel());

		String href = panelAppNavigationItem.getHref();

		Assert.assertTrue(href, href.contains("tabs1=service-provider"));
	}

	@Test
	public void testGetPanelAppNavigationItemsSkipsTheLoneGeneralTab()
		throws Exception {

		Assert.assertTrue(
			_samlAdminPanelApp.getPanelAppNavigationItems(
				_httpServletRequest
			).isEmpty());
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);

	private final SamlAdminPanelApp _samlAdminPanelApp =
		new SamlAdminPanelApp() {

			@Override
			public PortletURL getPortletURL(
				HttpServletRequest httpServletRequest) {

				return _portletURL;
			}

			private final PortletURL _portletURL = new MockLiferayPortletURL();

		};

	private final SamlProviderConfigurationHelper
		_samlProviderConfigurationHelper = Mockito.mock(
			SamlProviderConfigurationHelper.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}