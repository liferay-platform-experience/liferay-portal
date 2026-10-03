/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.exception.DuplicateStyleBookEntryExternalReferenceCodeException;
import com.liferay.style.book.exception.StyleBookEntryThemeIdException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.persistence.StyleBookEntryPersistence;
import com.liferay.style.book.test.util.FrontendTokenDefinitionTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eudaldo Alonso
 */
@RunWith(Arquillian.class)
public class StyleBookEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test(expected = StyleBookEntryThemeIdException.MustNotBeNull.class)
	public void testAddStyleBookEntry() throws Exception {
		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			false, RandomTestUtil.randomString());

		Assert.assertTrue(
			Validator.isNotNull(styleBookEntry.getExternalReferenceCode()));

		styleBookEntry = _addStyleBookEntry(
			true, RandomTestUtil.randomString());

		StyleBookEntry defaultStyleBookEntry1 =
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), styleBookEntry.getThemeId());

		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			defaultStyleBookEntry1.getStyleBookEntryId());

		styleBookEntry = _addStyleBookEntry(
			true, RandomTestUtil.randomString());

		StyleBookEntry defaultStyleBookEntry2 =
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), styleBookEntry.getThemeId());

		Assert.assertNotEquals(
			defaultStyleBookEntry1.getStyleBookEntryId(),
			defaultStyleBookEntry2.getStyleBookEntryId());
		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			defaultStyleBookEntry2.getStyleBookEntryId());

		_addStyleBookEntry(false, null);
	}

	@Test(
		expected = DuplicateStyleBookEntryExternalReferenceCodeException.class
	)
	public void testAddStyleBookEntryWithExistingExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		_styleBookEntryLocalService.addStyleBookEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), false, null, null,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_serviceContext);
		_styleBookEntryLocalService.addStyleBookEntry(
			externalReferenceCode, TestPropsValues.getUserId(),
			_group.getGroupId(), false, null, null,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_serviceContext);
	}

	@Test
	public void testCopyStyleBookEntry() throws Exception {
		String frontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString());

		StyleBookEntry sourceStyleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, frontendTokenDefinition, null,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _serviceContext);

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(sourceStyleBookEntry);

		String draftFrontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString());

		draftStyleBookEntry.setFrontendTokenDefinition(
			draftFrontendTokenDefinition);

		_styleBookEntryLocalService.updateDraft(draftStyleBookEntry);

		StyleBookEntry copyStyleBookEntry =
			_styleBookEntryLocalService.copyStyleBookEntry(
				TestPropsValues.getUserId(), _group.getGroupId(),
				sourceStyleBookEntry.getStyleBookEntryId(), _serviceContext);

		Assert.assertEquals(
			frontendTokenDefinition,
			copyStyleBookEntry.getFrontendTokenDefinition());

		StyleBookEntry copyDraftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(copyStyleBookEntry);

		Assert.assertEquals(
			draftFrontendTokenDefinition,
			copyDraftStyleBookEntry.getFrontendTokenDefinition());
	}

	@Test
	public void testDeleteGroup() throws Exception {
		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			false, RandomTestUtil.randomString());

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		_groupLocalService.deleteGroup(_group);

		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				styleBookEntry.getStyleBookEntryId()));
		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				draftStyleBookEntry.getStyleBookEntryId()));
	}

	@Test
	public void testDeleteStyleBookEntryByExternalReferenceCode()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			false, RandomTestUtil.randomString());

		_styleBookEntryLocalService.deleteStyleBookEntry(
			styleBookEntry.getExternalReferenceCode(),
			styleBookEntry.getGroupId());

		Assert.assertNull(
			_styleBookEntryLocalService.fetchStyleBookEntry(
				styleBookEntry.getStyleBookEntryId()));
	}

	@Test
	public void testUpdateDefaultStyleBookEntry() throws Exception {
		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		Assert.assertTrue(styleBookEntry1.isDefaultStyleBookEntry());

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry1);

		Assert.assertTrue(draftStyleBookEntry.isDefaultStyleBookEntry());

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		Assert.assertFalse(styleBookEntry2.isDefaultStyleBookEntry());

		_styleBookEntryLocalService.getDraft(styleBookEntry2);

		styleBookEntry2 =
			_styleBookEntryLocalService.updateDefaultStyleBookEntry(
				styleBookEntry2.getStyleBookEntryId(), true);

		Assert.assertTrue(styleBookEntry2.isDefaultStyleBookEntry());

		_assertDefaultStyleBookEntry(styleBookEntry2, styleBookEntry1);
	}

	@Test
	public void testUpdateDefaultStyleBookEntryWithDefaultStyleBookEntry()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			true, RandomTestUtil.randomString());

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntry.getStyleBookEntryId(), true);

		_assertDefaultStyleBookEntry(styleBookEntry);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			draftStyleBookEntry.getStyleBookEntryId(), true);

		_assertDefaultStyleBookEntry(styleBookEntry);
	}

	@Test
	public void testUpdateDefaultStyleBookEntryWithDraftStyleBookEntryId()
		throws Exception {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry1);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		StyleBookEntry draftStyleBookEntry =
			_styleBookEntryLocalService.getDraft(styleBookEntry2);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			draftStyleBookEntry.getStyleBookEntryId(), true);

		_assertDefaultStyleBookEntry(styleBookEntry2, styleBookEntry1);
	}

	@Test
	public void testUpdateDefaultStyleBookEntryWithDuplicateDefaultStyleBookEntries()
		throws Throwable {

		String themeId = RandomTestUtil.randomString();

		StyleBookEntry styleBookEntry1 = _addStyleBookEntry(true, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry1);

		StyleBookEntry styleBookEntry2 = _addStyleBookEntry(false, themeId);

		styleBookEntry2.setDefaultStyleBookEntry(true);

		styleBookEntry2 = _updateStyleBookEntry(styleBookEntry2);

		_styleBookEntryLocalService.getDraft(styleBookEntry2);

		StyleBookEntry styleBookEntry3 = _addStyleBookEntry(false, themeId);

		_styleBookEntryLocalService.getDraft(styleBookEntry3);

		_styleBookEntryLocalService.updateDefaultStyleBookEntry(
			styleBookEntry3.getStyleBookEntryId(), true);

		_assertDefaultStyleBookEntry(
			styleBookEntry3, styleBookEntry1, styleBookEntry2);
	}

	@Test
	public void testUpdateFrontendTokenDefinition() throws Exception {
		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			false, RandomTestUtil.randomString());

		long styleBookEntryId = styleBookEntry.getStyleBookEntryId();

		String frontendTokenDefinition =
			FrontendTokenDefinitionTestUtil.getFrontendTokenDefinition(
				RandomTestUtil.randomString());

		styleBookEntry =
			_styleBookEntryLocalService.updateFrontendTokenDefinition(
				styleBookEntryId, frontendTokenDefinition, _serviceContext);

		Assert.assertEquals(
			frontendTokenDefinition,
			styleBookEntry.getFrontendTokenDefinition());
	}

	@Test
	public void testUpdateStyleBookEntryWithDefaultStyleBookEntry()
		throws Exception {

		StyleBookEntry styleBookEntry = _addStyleBookEntry(
			true, RandomTestUtil.randomString());

		_styleBookEntryLocalService.getDraft(styleBookEntry);

		_styleBookEntryLocalService.updateStyleBookEntry(
			TestPropsValues.getUserId(), styleBookEntry.getStyleBookEntryId(),
			true, styleBookEntry.getFrontendTokenDefinition(),
			styleBookEntry.getFrontendTokensValues(),
			RandomTestUtil.randomString(),
			styleBookEntry.getStyleBookEntryKey(),
			styleBookEntry.getPreviewFileEntryId(), _serviceContext);

		_assertDefaultStyleBookEntry(styleBookEntry);
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

	private void _assertDefaultStyleBookEntry(
			StyleBookEntry styleBookEntry,
			StyleBookEntry... nondefaultStyleBookEntries)
		throws Exception {

		StyleBookEntry defaultStyleBookEntry =
			_styleBookEntryLocalService.fetchDefaultStyleBookEntry(
				_group.getGroupId(), styleBookEntry.getThemeId());

		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryId(),
			defaultStyleBookEntry.getStyleBookEntryId());

		_assertDefaultStyleBookEntry(true, styleBookEntry);

		for (StyleBookEntry nondefaultStyleBookEntry :
				nondefaultStyleBookEntries) {

			_assertDefaultStyleBookEntry(false, nondefaultStyleBookEntry);
		}
	}

	private StyleBookEntry _updateStyleBookEntry(StyleBookEntry styleBookEntry)
		throws Throwable {

		return TransactionInvokerUtil.invoke(
			_transactionConfig,
			() -> _styleBookEntryPersistence.update(styleBookEntry));
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceContext _serviceContext;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private StyleBookEntryPersistence _styleBookEntryPersistence;

}