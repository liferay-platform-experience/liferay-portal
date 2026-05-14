/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.headless.delivery.client.dto.v1_0.AssetEntry;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class AssetEntryResourceTest extends BaseAssetEntryResourceTestCase {

	@Test
	public void testGetSiteAssetEntriesPageWithClassName() throws Exception {
		Long siteId = testGroup.getGroupId();

		BlogsEntry blogsEntry = _addBlogsEntry(siteId);
		JournalArticle journalArticle = JournalTestUtil.addArticle(siteId, 0);

		Page<AssetEntry> page = assetEntryResource.getSiteAssetEntriesPage(
			siteId, new String[] {BlogsEntry.class.getName()}, null, null, null,
			null, null, Pagination.of(1, 20), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(
			_containsClassPK(assetEntries, blogsEntry.getEntryId()));
		Assert.assertFalse(
			_containsClassPK(
				assetEntries, journalArticle.getResourcePrimKey()));
	}

	@Test
	public void testGetSiteAssetEntriesPageWithClassTypeId() throws Exception {
		Long siteId = testGroup.getGroupId();

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(siteId, 0);
		JournalArticle journalArticle2 = JournalTestUtil.addArticle(siteId, 0);

		DDMStructure ddmStructure1 = journalArticle1.getDDMStructure();
		DDMStructure ddmStructure2 = journalArticle2.getDDMStructure();

		Assert.assertNotEquals(ddmStructure1, ddmStructure2);

		Page<AssetEntry> page = assetEntryResource.getSiteAssetEntriesPage(
			siteId, new String[] {JournalArticle.class.getName()},
			ddmStructure1.getStructureId(), null, null, null, null,
			Pagination.of(1, 20), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(
			_containsClassPK(
				assetEntries, journalArticle1.getResourcePrimKey()));
		Assert.assertFalse(
			_containsClassPK(
				assetEntries, journalArticle2.getResourcePrimKey()));
	}

	@Test
	public void testGetSiteAssetEntriesPageWithGroupIds() throws Exception {
		Long siteId1 = irrelevantGroup.getGroupId();
		Long siteId2 = testGroup.getGroupId();

		BlogsEntry blogsEntry1 = _addBlogsEntry(siteId1);
		BlogsEntry blogsEntry2 = _addBlogsEntry(siteId2);

		Page<AssetEntry> page = assetEntryResource.getSiteAssetEntriesPage(
			siteId2, new String[] {BlogsEntry.class.getName()}, null,
			new Long[] {siteId1, siteId2}, null, null, null,
			Pagination.of(1, 50), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(
			_containsClassPK(assetEntries, blogsEntry1.getEntryId()));
		Assert.assertTrue(
			_containsClassPK(assetEntries, blogsEntry2.getEntryId()));
	}

	@Test
	public void testGetSiteAssetEntriesPageWithMultipleClassNames()
		throws Exception {

		Long siteId = testGroup.getGroupId();

		BlogsEntry blogsEntry = _addBlogsEntry(siteId);
		JournalArticle journalArticle = JournalTestUtil.addArticle(siteId, 0);

		Page<AssetEntry> page = assetEntryResource.getSiteAssetEntriesPage(
			siteId,
			new String[] {
				BlogsEntry.class.getName(), JournalArticle.class.getName()
			},
			null, null, null, null, null, Pagination.of(1, 20), null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(
			_containsClassPK(assetEntries, blogsEntry.getEntryId()));
		Assert.assertTrue(
			_containsClassPK(
				assetEntries, journalArticle.getResourcePrimKey()));
	}

	@Test
	public void testGetSiteAssetEntriesPageWithoutClassName() throws Exception {
		Long siteId = testGroup.getGroupId();

		BlogsEntry blogsEntry = _addBlogsEntry(siteId);
		JournalArticle journalArticle = JournalTestUtil.addArticle(siteId, 0);

		Page<AssetEntry> page = assetEntryResource.getSiteAssetEntriesPage(
			siteId, null, null, null, null, null, null, Pagination.of(1, 50),
			null);

		List<AssetEntry> assetEntries = (List<AssetEntry>)page.getItems();

		Assert.assertTrue(
			_containsClassPK(assetEntries, blogsEntry.getEntryId()));
		Assert.assertTrue(
			_containsClassPK(
				assetEntries, journalArticle.getResourcePrimKey()));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"assetEntryId", "className", "classPK", "title"};
	}

	@Override
	protected AssetEntry testGetAssetLibraryAssetEntriesPage_addAssetEntry(
			Long assetLibraryId, AssetEntry assetEntry)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.getDepotEntry(
			assetLibraryId);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			depotEntry.getGroupId(), 0);

		return _toAssetEntry(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());
	}

	@Override
	protected AssetEntry testGetSiteAssetEntriesPage_addAssetEntry(
			Long siteId, AssetEntry assetEntry)
		throws Exception {

		BlogsEntry blogsEntry = _addBlogsEntry(siteId);

		return _toAssetEntry(
			BlogsEntry.class.getName(), blogsEntry.getEntryId());
	}

	private BlogsEntry _addBlogsEntry(long groupId) throws Exception {
		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				groupId, TestPropsValues.getUserId()));
	}

	private boolean _containsClassPK(
		List<AssetEntry> assetEntries, long classPK) {

		for (AssetEntry assetEntry : assetEntries) {
			Long actualClassPK = assetEntry.getClassPK();

			if ((actualClassPK != null) && (actualClassPK == classPK)) {
				return true;
			}
		}

		return false;
	}

	private AssetEntry _toAssetEntry(String className, long classPK) {
		com.liferay.asset.kernel.model.AssetEntry persistedAssetEntry =
			_assetEntryLocalService.fetchEntry(className, classPK);

		AssetEntry assetEntry = new AssetEntry();

		assetEntry.setAssetEntryId(persistedAssetEntry.getEntryId());
		assetEntry.setClassName(className);
		assetEntry.setClassPK(classPK);
		assetEntry.setTitle(
			persistedAssetEntry.getTitle(LocaleUtil.getDefault()));

		return assetEntry;
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

}