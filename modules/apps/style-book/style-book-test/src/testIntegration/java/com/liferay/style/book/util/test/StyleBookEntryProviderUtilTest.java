/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.util.StyleBookEntryProviderUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Lima
 */
@RunWith(Arquillian.class)
public class StyleBookEntryProviderUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_group = GroupTestUtil.addGroup();

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		layoutSet.setThemeId(_THEME_ID_CLASSIC);

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	@TestInfo("LPD-88081")
	public void testGetStyleBookEntriesExcludesConnectedDepotEntryStyleBookEntries()
		throws Exception {

		StyleBookEntry siteStyleBookEntry = _addStyleBookEntry(
			_group.getGroupId());

		Group depotGroup = _addConnectedDepotGroup();

		StyleBookEntry depotStyleBookEntry = _addStyleBookEntry(
			depotGroup.getGroupId());

		List<StyleBookEntry> styleBookEntries =
			StyleBookEntryProviderUtil.getStyleBookEntries(
				TestPropsValues.getCompanyId(), _group.getGroupId());

		Assert.assertFalse(
			styleBookEntries.toString(),
			styleBookEntries.contains(depotStyleBookEntry));
		Assert.assertTrue(
			styleBookEntries.toString(),
			styleBookEntries.contains(siteStyleBookEntry));
	}

	@Test
	@TestInfo("LPD-89205")
	public void testGetStyleBookEntriesFiltersByThemeId() throws Exception {
		StyleBookEntry classicStyleBookEntry = _addStyleBookEntry(
			_group.getGroupId());

		_addStyleBookEntry(_group.getGroupId(), _THEME_ID_OTHER);

		List<StyleBookEntry> styleBookEntries =
			StyleBookEntryProviderUtil.getStyleBookEntries(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				_THEME_ID_CLASSIC);

		Assert.assertEquals(
			styleBookEntries.toString(), 1, styleBookEntries.size());
		Assert.assertEquals(classicStyleBookEntry, styleBookEntries.get(0));
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-57283")}
	)
	@Test
	@TestInfo("LPD-89205")
	public void testGetStyleBookEntriesFromConnectedDepotsFiltersByThemeId()
		throws Exception {

		StyleBookEntry siteStyleBookEntry = _addStyleBookEntry(
			_group.getGroupId());

		Group depotGroup = _addConnectedDepotGroup();

		StyleBookEntry depotClassicStyleBookEntry = _addStyleBookEntry(
			depotGroup.getGroupId());

		_addStyleBookEntry(depotGroup.getGroupId(), _THEME_ID_OTHER);

		List<StyleBookEntry> styleBookEntries =
			StyleBookEntryProviderUtil.getStyleBookEntries(
				TestPropsValues.getCompanyId(), _group.getGroupId(),
				_THEME_ID_CLASSIC);

		Assert.assertTrue(
			styleBookEntries.toString(),
			styleBookEntries.contains(siteStyleBookEntry));
		Assert.assertTrue(
			styleBookEntries.toString(),
			styleBookEntries.contains(depotClassicStyleBookEntry));
		Assert.assertEquals(
			styleBookEntries.toString(), 2, styleBookEntries.size());
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-57283")}
	)
	@Test
	@TestInfo("LPD-88081")
	public void testGetStyleBookEntriesIncludesConnectedDepotEntryStyleBookEntries()
		throws Exception {

		StyleBookEntry siteStyleBookEntry = _addStyleBookEntry(
			_group.getGroupId());

		Group depotGroup = _addConnectedDepotGroup();

		StyleBookEntry depotStyleBookEntry = _addStyleBookEntry(
			depotGroup.getGroupId());

		List<StyleBookEntry> styleBookEntries =
			StyleBookEntryProviderUtil.getStyleBookEntries(
				TestPropsValues.getCompanyId(), _group.getGroupId());

		Assert.assertTrue(
			styleBookEntries.toString(),
			styleBookEntries.contains(depotStyleBookEntry));
		Assert.assertTrue(
			styleBookEntries.toString(),
			styleBookEntries.contains(siteStyleBookEntry));
	}

	@Test
	@TestInfo("LPD-88081")
	public void testGetStyleBookEntriesReturnsGroupStyleBookEntries()
		throws Exception {

		StyleBookEntry siteStyleBookEntry = _addStyleBookEntry(
			_group.getGroupId());

		List<StyleBookEntry> styleBookEntries =
			StyleBookEntryProviderUtil.getStyleBookEntries(
				TestPropsValues.getCompanyId(), _group.getGroupId());

		Assert.assertTrue(
			styleBookEntries.toString(),
			styleBookEntries.contains(siteStyleBookEntry));
	}

	@FeatureFlags(
		featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-57283")}
	)
	@Test
	@TestInfo("LPD-88081")
	public void testGetStyleBookEntry() throws Exception {
		_testGetStyleBookEntryWithConnectedDepotScope();
		_testGetStyleBookEntryWithNonexistentEntryERC();
		_testGetStyleBookEntryWithNonexistentScopeERC();
		_testGetStyleBookEntryWithSiteScope();
	}

	private Group _addConnectedDepotGroup() throws Exception {
		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), _group.getGroupId());

		_depotEntries.add(depotEntry);

		return depotEntry.getGroup();
	}

	private StyleBookEntry _addStyleBookEntry(long groupId) throws Exception {
		return _addStyleBookEntry(groupId, _THEME_ID_CLASSIC);
	}

	private StyleBookEntry _addStyleBookEntry(long groupId, String themeId)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			false, null, RandomTestUtil.randomString(), null, themeId, null);
	}

	private void _testGetStyleBookEntryWithConnectedDepotScope()
		throws Exception {

		Group depotGroup = _addConnectedDepotGroup();

		StyleBookEntry depotStyleBookEntry = _addStyleBookEntry(
			depotGroup.getGroupId());

		_layout.setStyleBookEntryERC(
			depotStyleBookEntry.getExternalReferenceCode());
		_layout.setStyleBookEntryScopeERC(
			depotGroup.getExternalReferenceCode());

		_layout = _layoutLocalService.updateLayout(_layout);

		StyleBookEntry resolvedStyleBookEntry =
			StyleBookEntryProviderUtil.getStyleBookEntry(_layout);

		Assert.assertEquals(
			depotStyleBookEntry.getStyleBookEntryId(),
			resolvedStyleBookEntry.getStyleBookEntryId());
	}

	private void _testGetStyleBookEntryWithNonexistentEntryERC()
		throws Exception {

		Group depotGroup = _addConnectedDepotGroup();

		_layout.setStyleBookEntryERC("nonexistent-entry-erc");
		_layout.setStyleBookEntryScopeERC(
			depotGroup.getExternalReferenceCode());

		_layout = _layoutLocalService.updateLayout(_layout);

		Assert.assertNull(
			StyleBookEntryProviderUtil.getStyleBookEntry(_layout));
	}

	private void _testGetStyleBookEntryWithNonexistentScopeERC()
		throws Exception {

		_layout.setStyleBookEntryERC(RandomTestUtil.randomString());
		_layout.setStyleBookEntryScopeERC("nonexistent-scope-erc");

		_layout = _layoutLocalService.updateLayout(_layout);

		Assert.assertNull(
			StyleBookEntryProviderUtil.getStyleBookEntry(_layout));
	}

	private void _testGetStyleBookEntryWithSiteScope() throws Exception {
		StyleBookEntry siteStyleBookEntry = _addStyleBookEntry(
			_group.getGroupId());

		_layout.setStyleBookEntryERC(
			siteStyleBookEntry.getExternalReferenceCode());
		_layout.setStyleBookEntryScopeERC(null);

		_layout = _layoutLocalService.updateLayout(_layout);

		StyleBookEntry resolvedStyleBookEntry =
			StyleBookEntryProviderUtil.getStyleBookEntry(_layout);

		Assert.assertEquals(
			siteStyleBookEntry.getStyleBookEntryId(),
			resolvedStyleBookEntry.getStyleBookEntryId());
	}

	private static final String _THEME_ID_CLASSIC = "classic_WAR_classictheme";

	private static final String _THEME_ID_OTHER = "other_WAR_othertheme";

	@DeleteAfterTestRun
	private List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

}