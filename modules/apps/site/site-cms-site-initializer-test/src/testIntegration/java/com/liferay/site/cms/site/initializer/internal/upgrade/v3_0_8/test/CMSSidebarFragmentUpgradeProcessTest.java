/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_8.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mario Leandro
 */
@RunWith(Arquillian.class)
public class CMSSidebarFragmentUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_companyLocalService.deleteCompany(_company);
	}

	@Test
	public void testUpgrade() throws Exception {
		Group group = _groupLocalService.getGroup(
			_company.getCompanyId(), GroupConstants.CMS);

		_downgradeFragmentEntry(group, "sidebar");
		_downgradeFragmentEntry(group, "vertical-navigation");

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_assertFragmentEntry(group, "sidebar", upgradeProcess);
		_assertFragmentEntry(group, "vertical-navigation", upgradeProcess);
	}

	@Test
	public void testUpgradeWithoutFragmentEntryUser() throws Exception {
		Group group = _groupLocalService.getGroup(
			_company.getCompanyId(), GroupConstants.CMS);

		FragmentEntry fragmentEntry = _downgradeFragmentEntry(group, "sidebar");

		fragmentEntry.setUserId(RandomTestUtil.nextLong());

		_fragmentEntryLocalService.updateFragmentEntry(fragmentEntry);

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_assertFragmentEntry(group, "sidebar", upgradeProcess);
	}

	private void _assertFragmentEntry(
			Group group, String fragmentEntryKey, UpgradeProcess upgradeProcess)
		throws Exception {

		String html = _read(upgradeProcess, fragmentEntryKey, "index.html");
		String js = _read(upgradeProcess, fragmentEntryKey, "index.js");

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				group.getGroupId(), fragmentEntryKey);

		Assert.assertEquals(html, fragmentEntry.getHtml());
		Assert.assertEquals(js, fragmentEntry.getJs());

		List<FragmentEntryLink> fragmentEntryLinks =
			_fragmentEntryLinkLocalService.getFragmentEntryLinksByFragmentEntry(
				group.getGroupId(), fragmentEntry);

		Assert.assertFalse(fragmentEntryLinks.isEmpty());

		for (FragmentEntryLink fragmentEntryLink : fragmentEntryLinks) {
			fragmentEntryLink =
				_fragmentEntryLinkLocalService.getFragmentEntryLink(
					fragmentEntryLink.getFragmentEntryLinkId());

			Assert.assertEquals(html, fragmentEntryLink.getHtml());
			Assert.assertEquals(js, fragmentEntryLink.getJs());
		}
	}

	private FragmentEntry _downgradeFragmentEntry(
			Group group, String fragmentEntryKey)
		throws Exception {

		String html = "<div>" + RandomTestUtil.randomString() + "</div>";
		String js = RandomTestUtil.randomString();

		FragmentEntry fragmentEntry =
			_fragmentEntryLocalService.fetchFragmentEntry(
				group.getGroupId(), fragmentEntryKey);

		for (FragmentEntryLink fragmentEntryLink :
				_fragmentEntryLinkLocalService.
					getFragmentEntryLinksByFragmentEntry(
						group.getGroupId(), fragmentEntry)) {

			fragmentEntryLink.setHtml(html);
			fragmentEntryLink.setJs(js);

			_fragmentEntryLinkLocalService.updateFragmentEntryLink(
				fragmentEntryLink);
		}

		fragmentEntry.setHtml(html);
		fragmentEntry.setJs(js);

		return _fragmentEntryLocalService.updateFragmentEntry(fragmentEntry);
	}

	private String _read(
		UpgradeProcess upgradeProcess, String fragmentEntryKey,
		String fileName) {

		return StringUtil.read(
			upgradeProcess.getClass(),
			StringBundler.concat(
				"dependencies/", fragmentEntryKey, "/", fileName));
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.cms.site.initializer.internal.upgrade.v3_0_8." +
			"CMSSidebarFragmentUpgradeProcess";

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.upgrade.registry.SiteCMSSiteInitializerUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}