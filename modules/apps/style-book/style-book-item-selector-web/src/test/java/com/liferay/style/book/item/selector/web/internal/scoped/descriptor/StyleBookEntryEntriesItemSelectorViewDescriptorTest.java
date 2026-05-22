/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.descriptor;

import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.item.selector.web.internal.scoped.Scope;
import com.liferay.style.book.model.StyleBookEntry;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryEntriesItemSelectorViewDescriptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_frontendTokenDefinition = Mockito.mock(FrontendTokenDefinition.class);

		Mockito.when(
			_frontendTokenDefinition.getThemeId()
		).thenReturn(
			"classic"
		);

		_layout = Mockito.mock(Layout.class);

		Mockito.when(
			_layout.getGroupId()
		).thenReturn(
			11L
		);

		_frontendTokenDefinitionRegistry = Mockito.mock(
			FrontendTokenDefinitionRegistry.class);

		Mockito.when(
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinition(_layout)
		).thenReturn(
			_frontendTokenDefinition
		);

		_siteGroup = Mockito.mock(Group.class);

		Mockito.when(
			_siteGroup.getGroupId()
		).thenReturn(
			11L
		);

		Mockito.when(
			_siteGroup.isDepot()
		).thenReturn(
			false
		);

		Mockito.when(
			_siteGroup.isSite()
		).thenReturn(
			true
		);

		_groupLocalService = Mockito.mock(GroupLocalService.class);

		Mockito.when(
			_groupLocalService.fetchGroup(11L)
		).thenReturn(
			_siteGroup
		);

		_styleBookEntry = Mockito.mock(StyleBookEntry.class);

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(
				Mockito.any(MockHttpServletRequest.class), Mockito.anyString())
		).thenAnswer(
			invocation -> invocation.getArgument(1)
		);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(language);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.when(
			themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		_mockHttpServletRequest = new MockHttpServletRequest();

		_mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_REQUEST,
			Mockito.mock(PortletRequest.class));
		_mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		_portletURL = Mockito.mock(PortletURL.class);
	}

	@Test
	public void testGetBreadcrumbEntriesDLScope() throws Exception {
		Group depotGroup = Mockito.mock(Group.class);

		Mockito.when(
			depotGroup.getDescriptiveName(Mockito.any())
		).thenReturn(
			"Gerardo DL"
		);

		Mockito.when(
			_groupLocalService.fetchGroup(99L)
		).thenReturn(
			depotGroup
		);

		StyleBookEntryEntriesItemSelectorViewDescriptor
			styleBookEntryEntriesItemSelectorViewDescriptor =
				new StyleBookEntryEntriesItemSelectorViewDescriptor(
					_frontendTokenDefinitionRegistry, 99L, _groupLocalService,
					_mockHttpServletRequest, _portletURL, Scope.DESIGN_LIBRARY,
					_layout, null, null);

		List<BreadcrumbEntry> breadcrumbEntries =
			styleBookEntryEntriesItemSelectorViewDescriptor.
				getBreadcrumbEntries(null);

		Assert.assertEquals(
			breadcrumbEntries.toString(), 3, breadcrumbEntries.size());
		Assert.assertEquals(
			"design-libraries",
			breadcrumbEntries.get(
				1
			).getTitle());
		Assert.assertEquals(
			"Gerardo DL",
			breadcrumbEntries.get(
				2
			).getTitle());
	}

	@Test
	public void testGetBreadcrumbEntriesSiteScope() throws Exception {
		Mockito.when(
			_siteGroup.getDescriptiveName(Mockito.any())
		).thenReturn(
			"Liferay DXP"
		);

		StyleBookEntryEntriesItemSelectorViewDescriptor
			styleBookEntryEntriesItemSelectorViewDescriptor =
				new StyleBookEntryEntriesItemSelectorViewDescriptor(
					_frontendTokenDefinitionRegistry, 11L, _groupLocalService,
					_mockHttpServletRequest, _portletURL, Scope.SITE, _layout,
					null, null);

		List<BreadcrumbEntry> breadcrumbEntries =
			styleBookEntryEntriesItemSelectorViewDescriptor.
				getBreadcrumbEntries(null);

		Assert.assertEquals(
			breadcrumbEntries.toString(), 3, breadcrumbEntries.size());
		Assert.assertEquals(
			"sites-and-libraries",
			breadcrumbEntries.get(
				0
			).getTitle());
		Assert.assertEquals(
			"sites",
			breadcrumbEntries.get(
				1
			).getTitle());
		Assert.assertEquals(
			"Liferay DXP",
			breadcrumbEntries.get(
				2
			).getTitle());
		Assert.assertNull(
			breadcrumbEntries.get(
				2
			).getURL());
	}

	@Test
	public void testGetItemDescriptorPayloadEmitsScopeERCForDL()
		throws Exception {

		Group depotGroup = Mockito.mock(Group.class);

		Mockito.when(
			depotGroup.getExternalReferenceCode()
		).thenReturn(
			"GroupERC-DL"
		);

		Mockito.when(
			_groupLocalService.fetchGroup(99L)
		).thenReturn(
			depotGroup
		);

		Mockito.when(
			_styleBookEntry.getExternalReferenceCode()
		).thenReturn(
			"ERC-2"
		);

		Mockito.when(
			_styleBookEntry.getName()
		).thenReturn(
			"Sb-DL"
		);

		Mockito.when(
			_styleBookEntry.getStyleBookEntryId()
		).thenReturn(
			99L
		);

		StyleBookEntryEntriesItemSelectorViewDescriptor
			styleBookEntryEntriesItemSelectorViewDescriptor =
				new StyleBookEntryEntriesItemSelectorViewDescriptor(
					_frontendTokenDefinitionRegistry, 99L, _groupLocalService,
					_mockHttpServletRequest, _portletURL, Scope.DESIGN_LIBRARY,
					_layout, null, null);

		ItemSelectorViewDescriptor.ItemDescriptor itemDescriptor =
			styleBookEntryEntriesItemSelectorViewDescriptor.getItemDescriptor(
				_styleBookEntry);

		String payload = itemDescriptor.getPayload();

		Assert.assertTrue(
			payload, payload.contains("\"externalReferenceCode\":\"ERC-2\""));
		Assert.assertTrue(
			payload,
			payload.contains("\"styleBookEntryScopeERC\":\"GroupERC-DL\""));
	}

	@Test
	public void testGetItemDescriptorPayloadHasEmptyScopeERCForSite()
		throws Exception {

		Mockito.when(
			_styleBookEntry.getExternalReferenceCode()
		).thenReturn(
			"ERC-1"
		);

		Mockito.when(
			_styleBookEntry.getName()
		).thenReturn(
			"Sb-1"
		);

		Mockito.when(
			_styleBookEntry.getStyleBookEntryId()
		).thenReturn(
			42L
		);

		StyleBookEntryEntriesItemSelectorViewDescriptor
			styleBookEntryEntriesItemSelectorViewDescriptor =
				new StyleBookEntryEntriesItemSelectorViewDescriptor(
					_frontendTokenDefinitionRegistry, 11L, _groupLocalService,
					_mockHttpServletRequest, _portletURL, Scope.SITE, _layout,
					null, null);

		ItemSelectorViewDescriptor.ItemDescriptor itemDescriptor =
			styleBookEntryEntriesItemSelectorViewDescriptor.getItemDescriptor(
				_styleBookEntry);

		String payload = itemDescriptor.getPayload();

		Assert.assertTrue(
			payload, payload.contains("\"externalReferenceCode\":\"ERC-1\""));
		Assert.assertTrue(
			payload, payload.contains("\"styleBookEntryId\":\"42\""));
		Assert.assertTrue(
			payload, payload.contains("\"styleBookEntryScopeERC\":\"\""));
	}

	private FrontendTokenDefinition _frontendTokenDefinition;
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;
	private GroupLocalService _groupLocalService;
	private Layout _layout;
	private MockHttpServletRequest _mockHttpServletRequest;
	private PortletURL _portletURL;
	private Group _siteGroup;
	private StyleBookEntry _styleBookEntry;

}