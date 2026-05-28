/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.descriptor;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.item.selector.web.internal.scoped.Scope;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item.StyleBookEntryGroupDesignLibraryItemDescriptor;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.item.StyleBookEntryGroupSiteItemDescriptor;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.Arrays;
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
public class StyleBookEntryGroupsItemSelectorViewDescriptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_groupLocalService = Mockito.mock(GroupLocalService.class);
		_siteConnectedGroupGroupProvider = Mockito.mock(
			SiteConnectedGroupGroupProvider.class);

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
	public void testGetBreadcrumbEntriesSiteScope() throws Exception {
		StyleBookEntryGroupsItemSelectorViewDescriptor
			styleBookEntryGroupsItemSelectorViewDescriptor =
				new StyleBookEntryGroupsItemSelectorViewDescriptor(
					_groupLocalService, _mockHttpServletRequest, 99L,
					_portletURL, Scope.SITE, _siteConnectedGroupGroupProvider);

		List<BreadcrumbEntry> breadcrumbEntries =
			styleBookEntryGroupsItemSelectorViewDescriptor.getBreadcrumbEntries(
				null);

		Assert.assertEquals(
			breadcrumbEntries.toString(), 2, breadcrumbEntries.size());
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
		Assert.assertNull(
			breadcrumbEntries.get(
				1
			).getURL());
	}

	@Test
	public void testGetItemDescriptor() throws Exception {
		StyleBookEntryGroupsItemSelectorViewDescriptor
			styleBookEntryGroupsItemSelectorViewDescriptor =
				new StyleBookEntryGroupsItemSelectorViewDescriptor(
					_groupLocalService, _mockHttpServletRequest, 99L,
					_portletURL, Scope.SITE, _siteConnectedGroupGroupProvider);

		Group depotGroup = _mockGroup(22L, true, false, "Gerardo DL");

		ItemSelectorViewDescriptor.ItemDescriptor depotItemDescriptor =
			styleBookEntryGroupsItemSelectorViewDescriptor.getItemDescriptor(
				depotGroup);

		Assert.assertTrue(
			depotItemDescriptor instanceof
				StyleBookEntryGroupDesignLibraryItemDescriptor);

		Group siteGroup = _mockGroup(11L, false, true, "Liferay DXP");

		ItemSelectorViewDescriptor.ItemDescriptor siteItemDescriptor =
			styleBookEntryGroupsItemSelectorViewDescriptor.getItemDescriptor(
				siteGroup);

		Assert.assertTrue(
			siteItemDescriptor instanceof
				StyleBookEntryGroupSiteItemDescriptor);
	}

	@Test
	public void testGetSearchContainerFiltersByScopeDesignLibrary()
		throws Exception {

		Group depotGroup = _mockGroup(22L, true, false, "Gerardo DL");
		Group siteGroup = _mockGroup(11L, false, true, "Liferay DXP");

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(99L)
		).thenReturn(
			new long[] {11L, 22L}
		);

		Mockito.when(
			_groupLocalService.getGroups(new long[] {11L, 22L})
		).thenReturn(
			Arrays.asList(siteGroup, depotGroup)
		);

		StyleBookEntryGroupsItemSelectorViewDescriptor
			styleBookEntryGroupsItemSelectorViewDescriptor =
				new StyleBookEntryGroupsItemSelectorViewDescriptor(
					_groupLocalService, _mockHttpServletRequest, 99L,
					_portletURL, Scope.DESIGN_LIBRARY,
					_siteConnectedGroupGroupProvider);

		List<Group> results =
			styleBookEntryGroupsItemSelectorViewDescriptor.getSearchContainer(
			).getResults();

		Assert.assertEquals(results.toString(), 1, results.size());
		Assert.assertSame(depotGroup, results.get(0));
	}

	@Test
	public void testGetSearchContainerFiltersByScopeSite() throws Exception {
		Group depotGroup = _mockGroup(22L, true, false, "Gerardo DL");
		Group siteGroup = _mockGroup(11L, false, true, "Liferay DXP");

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(99L)
		).thenReturn(
			new long[] {11L, 22L}
		);

		Mockito.when(
			_groupLocalService.getGroups(new long[] {11L, 22L})
		).thenReturn(
			Arrays.asList(siteGroup, depotGroup)
		);

		StyleBookEntryGroupsItemSelectorViewDescriptor
			styleBookEntryGroupsItemSelectorViewDescriptor =
				new StyleBookEntryGroupsItemSelectorViewDescriptor(
					_groupLocalService, _mockHttpServletRequest, 99L,
					_portletURL, Scope.SITE, _siteConnectedGroupGroupProvider);

		List<Group> results =
			styleBookEntryGroupsItemSelectorViewDescriptor.getSearchContainer(
			).getResults();

		Assert.assertEquals(results.toString(), 1, results.size());
		Assert.assertSame(siteGroup, results.get(0));
	}

	private Group _mockGroup(
			long groupId, boolean depot, boolean site, String name)
		throws Exception {

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getDescriptiveName(Mockito.any())
		).thenReturn(
			name
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			group.isDepot()
		).thenReturn(
			depot
		);

		Mockito.when(
			group.isSite()
		).thenReturn(
			site
		);

		return group;
	}

	private GroupLocalService _groupLocalService;
	private MockHttpServletRequest _mockHttpServletRequest;
	private PortletURL _portletURL;
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;

}