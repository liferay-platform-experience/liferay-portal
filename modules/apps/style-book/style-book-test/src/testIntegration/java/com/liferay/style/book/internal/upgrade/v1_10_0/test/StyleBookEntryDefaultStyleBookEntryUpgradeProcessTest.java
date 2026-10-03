/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.internal.upgrade.v1_10_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.persistence.StyleBookEntryPersistence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mario Leandro
 */
@RunWith(Arquillian.class)
public class StyleBookEntryDefaultStyleBookEntryUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test
	public void testUpgrade() throws Throwable {
		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry1);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry2);

		styleBookEntry2.setDefaultStyleBookEntry(true);

		styleBookEntry2 = _updateStyleBookEntry(styleBookEntry2);

		StyleBookEntry styleBookEntry3 = _addStyleBookEntry(
			true, RandomTestUtil.randomString());

		_styleBookEntryLocalService.getDraft(styleBookEntry3);

		_runUpgrade();

		EntityCacheUtil.clearCache();
		FinderCacheUtil.clearCache();

		StyleBookEntry defaultStyleBookEntry =
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), themeId);

		Assert.assertEquals(
			styleBookEntry2.getStyleBookEntryId(),
			defaultStyleBookEntry.getStyleBookEntryId());

		_assertDefaultStyleBookEntry(false, styleBookEntry1);
		_assertDefaultStyleBookEntry(true, styleBookEntry2);
		_assertDefaultStyleBookEntry(true, styleBookEntry3);
	}

	private StyleBookEntry _addStyleBookEntry(
			boolean defaultStyleBookEntry, String themeId)
		throws Exception {

		return _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), defaultStyleBookEntry, null, null,
			RandomTestUtil.randomString(), null, themeId, _serviceContext);
	}

	private void _assertDefaultStyleBookEntry(
			boolean defaultStyleBookEntry, StyleBookEntry styleBookEntry)
		throws Exception {

		styleBookEntry = _styleBookEntryLocalService.getStyleBookEntry(
			styleBookEntry.getStyleBookEntryId());

		Assert.assertEquals(
			defaultStyleBookEntry, styleBookEntry.isDefaultStyleBookEntry());

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.fetchDraft(styleBookEntry);

		Assert.assertEquals(
			defaultStyleBookEntry,
			draftStyleBookEntry.isDefaultStyleBookEntry());
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();
	}

	private StyleBookEntry _updateStyleBookEntry(StyleBookEntry styleBookEntry)
		throws Throwable {

		return TransactionInvokerUtil.invoke(
			_transactionConfig,
			() -> _styleBookEntryPersistence.update(styleBookEntry));
	}

	private static final String _CLASS_NAME =
		"com.liferay.style.book.internal.upgrade.v1_10_0." +
			"StyleBookEntryDefaultStyleBookEntryUpgradeProcess";

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private StyleBookEntryPersistence _styleBookEntryPersistence;

	@Inject(
		filter = "(&(component.name=com.liferay.style.book.internal.upgrade.registry.StyleBookServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}