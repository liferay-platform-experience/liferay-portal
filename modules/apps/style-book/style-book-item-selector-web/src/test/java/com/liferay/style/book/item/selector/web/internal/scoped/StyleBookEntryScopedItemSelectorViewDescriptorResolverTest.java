/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped;

import com.liferay.depot.group.provider.SiteConnectedGroupGroupProvider;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.item.selector.ItemSelectorViewDescriptor;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.StyleBookEntryEntriesItemSelectorViewDescriptor;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.StyleBookEntryGroupsItemSelectorViewDescriptor;
import com.liferay.style.book.item.selector.web.internal.scoped.descriptor.StyleBookEntryScopesItemSelectorViewDescriptor;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Gabriel Lima
 */
public class StyleBookEntryScopedItemSelectorViewDescriptorResolverTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testResolve() throws Exception {
		_withResolver(
			false, this::_testResolveReturnsSiteEntriesWhenFeatureFlagDisabled);
		_withResolver(
			true, this::_testResolveDispatchesToGroupsDescriptorOnLevelParam);
		_withResolver(
			true, this::_testResolveDispatchesToRootDescriptorOnLevelParam);
		_withResolver(
			true,
			this::_testResolveReturnsDesignLibraryEntriesWhenScopeResolves);
		_withResolver(
			true,
			this::_testResolveReturnsRootWhenDesignLibraryScopeNotConnected);
		_withResolver(
			true,
			this::_testResolveReturnsRootWhenDesignLibraryScopeUnresolvable);
		_withResolver(
			true, this::_testResolveReturnsSiteEntriesWhenScopeERCMissing);
		_withResolver(
			true, this::_testResolveReturnsLayoutDefaultWhenLevelKeyUnknown);
		_withResolver(
			true, this::_testResolveReturnsLayoutDefaultWhenScopeKeyUnknown);
		_withResolver(
			true,
			this::_testResolveReturnsLayoutDefaultWhenEntriesGroupNotConnected);
		_withResolver(true, this::_testResolveReturnsEntriesWhenGroupConnected);
	}

	private void _testResolveDispatchesToGroupsDescriptorOnLevelParam(
		HttpServletRequest httpServletRequest, Layout layout,
		PortletURL portletURL) {

		Mockito.when(
			httpServletRequest.getParameter("level")
		).thenReturn(
			Level.GROUPS.getKey()
		);

		Mockito.when(
			httpServletRequest.getParameter("scope")
		).thenReturn(
			Scope.DESIGN_LIBRARY.getKey()
		);

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, null, null);

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryGroupsItemSelectorViewDescriptor);
		Assert.assertEquals(
			Scope.DESIGN_LIBRARY,
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_scope"));
	}

	private void _testResolveDispatchesToRootDescriptorOnLevelParam(
		HttpServletRequest httpServletRequest, Layout layout,
		PortletURL portletURL) {

		Mockito.when(
			httpServletRequest.getParameter("level")
		).thenReturn(
			Level.SCOPES.getKey()
		);

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", null);

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryScopesItemSelectorViewDescriptor);
	}

	private void _testResolveReturnsDesignLibraryEntriesWhenScopeResolves(
			HttpServletRequest httpServletRequest, Layout layout,
			PortletURL portletURL)
		throws Exception {

		Group depotGroup = Mockito.mock(Group.class);

		Mockito.when(
			depotGroup.getGroupId()
		).thenReturn(
			22L
		);

		Mockito.when(
			_groupLocalService.fetchGroupByExternalReferenceCode("DepotERC", 7L)
		).thenReturn(
			depotGroup
		);

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(11L)
		).thenReturn(
			new long[] {11L, 22L}
		);

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", "DepotERC");

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryEntriesItemSelectorViewDescriptor);
		Assert.assertEquals(
			Long.valueOf(22L),
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_groupId"));
		Assert.assertEquals(
			Scope.DESIGN_LIBRARY,
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_scope"));
	}

	private void _testResolveReturnsEntriesWhenGroupConnected(
			HttpServletRequest httpServletRequest, Layout layout,
			PortletURL portletURL)
		throws Exception {

		Mockito.when(
			httpServletRequest.getParameter("level")
		).thenReturn(
			Level.ENTRIES.getKey()
		);

		Mockito.when(
			httpServletRequest.getParameter("scope")
		).thenReturn(
			Scope.DESIGN_LIBRARY.getKey()
		);

		Mockito.when(
			httpServletRequest.getParameter("groupId")
		).thenReturn(
			"22"
		);

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(11L)
		).thenReturn(
			new long[] {11L, 22L}
		);

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", null);

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryEntriesItemSelectorViewDescriptor);
		Assert.assertEquals(
			Long.valueOf(22L),
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_groupId"));
		Assert.assertEquals(
			Scope.DESIGN_LIBRARY,
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_scope"));
	}

	private void _testResolveReturnsLayoutDefaultWhenEntriesGroupNotConnected(
			HttpServletRequest httpServletRequest, Layout layout,
			PortletURL portletURL)
		throws Exception {

		Mockito.when(
			httpServletRequest.getParameter("level")
		).thenReturn(
			Level.ENTRIES.getKey()
		);

		Mockito.when(
			httpServletRequest.getParameter("scope")
		).thenReturn(
			Scope.DESIGN_LIBRARY.getKey()
		);

		Mockito.when(
			httpServletRequest.getParameter("groupId")
		).thenReturn(
			"999"
		);

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(11L)
		).thenReturn(
			new long[] {11L}
		);

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", null);

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryEntriesItemSelectorViewDescriptor);
		Assert.assertEquals(
			Long.valueOf(11L),
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_groupId"));
		Assert.assertEquals(
			Scope.SITE,
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_scope"));
	}

	private void _testResolveReturnsLayoutDefaultWhenLevelKeyUnknown(
		HttpServletRequest httpServletRequest, Layout layout,
		PortletURL portletURL) {

		Mockito.when(
			httpServletRequest.getParameter("level")
		).thenReturn(
			"foo"
		);

		Mockito.when(
			httpServletRequest.getParameter("scope")
		).thenReturn(
			Scope.DESIGN_LIBRARY.getKey()
		);

		Mockito.when(
			httpServletRequest.getParameter("groupId")
		).thenReturn(
			"999"
		);

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", null);

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryEntriesItemSelectorViewDescriptor);
		Assert.assertEquals(
			Long.valueOf(11L),
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_groupId"));
		Assert.assertEquals(
			Scope.SITE,
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_scope"));
	}

	private void _testResolveReturnsLayoutDefaultWhenScopeKeyUnknown(
		HttpServletRequest httpServletRequest, Layout layout,
		PortletURL portletURL) {

		Mockito.when(
			httpServletRequest.getParameter("level")
		).thenReturn(
			Level.ENTRIES.getKey()
		);

		Mockito.when(
			httpServletRequest.getParameter("scope")
		).thenReturn(
			"bar"
		);

		Mockito.when(
			httpServletRequest.getParameter("groupId")
		).thenReturn(
			"999"
		);

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", null);

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryEntriesItemSelectorViewDescriptor);
		Assert.assertEquals(
			Long.valueOf(11L),
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_groupId"));
		Assert.assertEquals(
			Scope.SITE,
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_scope"));
	}

	private void _testResolveReturnsRootWhenDesignLibraryScopeNotConnected(
			HttpServletRequest httpServletRequest, Layout layout,
			PortletURL portletURL)
		throws Exception {

		Group depotGroup = Mockito.mock(Group.class);

		Mockito.when(
			depotGroup.getGroupId()
		).thenReturn(
			22L
		);

		Mockito.when(
			_groupLocalService.fetchGroupByExternalReferenceCode("DepotERC", 7L)
		).thenReturn(
			depotGroup
		);

		Mockito.when(
			_siteConnectedGroupGroupProvider.
				getCurrentAndAncestorSiteAndDepotGroupIds(11L)
		).thenReturn(
			new long[] {11L}
		);

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", "DepotERC");

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryScopesItemSelectorViewDescriptor);
	}

	private void _testResolveReturnsRootWhenDesignLibraryScopeUnresolvable(
		HttpServletRequest httpServletRequest, Layout layout,
		PortletURL portletURL) {

		Mockito.when(
			_groupLocalService.fetchGroupByExternalReferenceCode("DepotERC", 7L)
		).thenReturn(
			null
		);

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", "DepotERC");

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryScopesItemSelectorViewDescriptor);
	}

	private void _testResolveReturnsSiteEntriesWhenFeatureFlagDisabled(
		HttpServletRequest httpServletRequest, Layout layout,
		PortletURL portletURL) {

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", null);

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryEntriesItemSelectorViewDescriptor);
		Assert.assertEquals(
			Long.valueOf(11L),
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_groupId"));
		Assert.assertEquals(
			Scope.SITE,
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_scope"));
	}

	private void _testResolveReturnsSiteEntriesWhenScopeERCMissing(
		HttpServletRequest httpServletRequest, Layout layout,
		PortletURL portletURL) {

		ItemSelectorViewDescriptor<?> itemSelectorViewDescriptor =
			_styleBookEntryScopedItemSelectorViewDescriptorResolver.resolve(
				httpServletRequest, layout, portletURL, "ERC-1", null);

		Assert.assertTrue(
			itemSelectorViewDescriptor instanceof
				StyleBookEntryEntriesItemSelectorViewDescriptor);
		Assert.assertEquals(
			Long.valueOf(11L),
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_groupId"));
		Assert.assertEquals(
			Scope.SITE,
			ReflectionTestUtil.getFieldValue(
				itemSelectorViewDescriptor, "_scope"));
	}

	private void _withResolver(
			boolean featureFlagEnabled,
			UnsafeTriConsumer<HttpServletRequest, Layout, PortletURL, Exception>
				unsafeTriConsumer)
		throws Exception {

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			Mockito.mock(ThemeDisplay.class)
		);

		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getCompanyId()
		).thenReturn(
			7L
		);

		Mockito.when(
			layout.getGroupId()
		).thenReturn(
			11L
		);

		PortletURL portletURL = Mockito.mock(PortletURL.class);

		_groupLocalService = Mockito.mock(GroupLocalService.class);

		_siteConnectedGroupGroupProvider = Mockito.mock(
			SiteConnectedGroupGroupProvider.class);

		_styleBookEntryScopedItemSelectorViewDescriptorResolver =
			new StyleBookEntryScopedItemSelectorViewDescriptorResolver(
				Mockito.mock(FrontendTokenDefinitionRegistry.class),
				_groupLocalService, _siteConnectedGroupGroupProvider);

		try (MockedStatic<FeatureFlagManagerUtil>
				featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
					FeatureFlagManagerUtil.class)) {

			featureFlagManagerUtilMockedStatic.when(
				() -> FeatureFlagManagerUtil.isEnabled(7L, "LPD-57283")
			).thenReturn(
				featureFlagEnabled
			);

			unsafeTriConsumer.accept(httpServletRequest, layout, portletURL);
		}
	}

	private GroupLocalService _groupLocalService;
	private SiteConnectedGroupGroupProvider _siteConnectedGroupGroupProvider;
	private StyleBookEntryScopedItemSelectorViewDescriptorResolver
		_styleBookEntryScopedItemSelectorViewDescriptorResolver;

}