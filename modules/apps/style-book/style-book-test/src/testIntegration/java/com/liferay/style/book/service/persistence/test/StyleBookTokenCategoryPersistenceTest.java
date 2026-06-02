/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.style.book.exception.NoSuchTokenCategoryException;
import com.liferay.style.book.model.StyleBookTokenCategory;
import com.liferay.style.book.service.StyleBookTokenCategoryLocalServiceUtil;
import com.liferay.style.book.service.persistence.StyleBookTokenCategoryPersistence;
import com.liferay.style.book.service.persistence.StyleBookTokenCategoryUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class StyleBookTokenCategoryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.style.book.service"));

	@Before
	public void setUp() {
		_persistence = StyleBookTokenCategoryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<StyleBookTokenCategory> iterator =
			_styleBookTokenCategories.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookTokenCategory styleBookTokenCategory = _persistence.create(pk);

		Assert.assertNotNull(styleBookTokenCategory);

		Assert.assertEquals(styleBookTokenCategory.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		StyleBookTokenCategory newStyleBookTokenCategory =
			addStyleBookTokenCategory();

		_persistence.remove(newStyleBookTokenCategory);

		StyleBookTokenCategory existingStyleBookTokenCategory =
			_persistence.fetchByPrimaryKey(
				newStyleBookTokenCategory.getPrimaryKey());

		Assert.assertNull(existingStyleBookTokenCategory);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addStyleBookTokenCategory();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookTokenCategory newStyleBookTokenCategory = _persistence.create(
			pk);

		newStyleBookTokenCategory.setMvccVersion(RandomTestUtil.nextLong());

		newStyleBookTokenCategory.setCtCollectionId(RandomTestUtil.nextLong());

		newStyleBookTokenCategory.setGroupId(RandomTestUtil.nextLong());

		newStyleBookTokenCategory.setCompanyId(RandomTestUtil.nextLong());

		newStyleBookTokenCategory.setUserId(RandomTestUtil.nextLong());

		newStyleBookTokenCategory.setUserName(RandomTestUtil.randomString());

		newStyleBookTokenCategory.setCreateDate(RandomTestUtil.nextDate());

		newStyleBookTokenCategory.setModifiedDate(RandomTestUtil.nextDate());

		newStyleBookTokenCategory.setStyleBookEntryId(
			RandomTestUtil.nextLong());

		newStyleBookTokenCategory.setThemeFrontendTokenDefinitionId(
			RandomTestUtil.randomString());

		newStyleBookTokenCategory.setName(RandomTestUtil.randomString());

		newStyleBookTokenCategory.setDescription(RandomTestUtil.randomString());

		_styleBookTokenCategories.add(
			_persistence.update(newStyleBookTokenCategory));

		StyleBookTokenCategory existingStyleBookTokenCategory =
			_persistence.findByPrimaryKey(
				newStyleBookTokenCategory.getPrimaryKey());

		Assert.assertEquals(
			existingStyleBookTokenCategory.getMvccVersion(),
			newStyleBookTokenCategory.getMvccVersion());
		Assert.assertEquals(
			existingStyleBookTokenCategory.getCtCollectionId(),
			newStyleBookTokenCategory.getCtCollectionId());
		Assert.assertEquals(
			existingStyleBookTokenCategory.getStyleBookTokenCategoryId(),
			newStyleBookTokenCategory.getStyleBookTokenCategoryId());
		Assert.assertEquals(
			existingStyleBookTokenCategory.getGroupId(),
			newStyleBookTokenCategory.getGroupId());
		Assert.assertEquals(
			existingStyleBookTokenCategory.getCompanyId(),
			newStyleBookTokenCategory.getCompanyId());
		Assert.assertEquals(
			existingStyleBookTokenCategory.getUserId(),
			newStyleBookTokenCategory.getUserId());
		Assert.assertEquals(
			existingStyleBookTokenCategory.getUserName(),
			newStyleBookTokenCategory.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingStyleBookTokenCategory.getCreateDate()),
			Time.getShortTimestamp(newStyleBookTokenCategory.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingStyleBookTokenCategory.getModifiedDate()),
			Time.getShortTimestamp(
				newStyleBookTokenCategory.getModifiedDate()));
		Assert.assertEquals(
			existingStyleBookTokenCategory.getStyleBookEntryId(),
			newStyleBookTokenCategory.getStyleBookEntryId());
		Assert.assertEquals(
			existingStyleBookTokenCategory.getThemeFrontendTokenDefinitionId(),
			newStyleBookTokenCategory.getThemeFrontendTokenDefinitionId());
		Assert.assertEquals(
			existingStyleBookTokenCategory.getName(),
			newStyleBookTokenCategory.getName());
		Assert.assertEquals(
			existingStyleBookTokenCategory.getDescription(),
			newStyleBookTokenCategory.getDescription());
	}

	@Test
	public void testCountByStyleBookEntryId() throws Exception {
		_persistence.countByStyleBookEntryId(RandomTestUtil.nextLong());

		_persistence.countByStyleBookEntryId(0L);
	}

	@Test
	public void testCountBySBEI_TFTDI_N() throws Exception {
		_persistence.countBySBEI_TFTDI_N(RandomTestUtil.nextLong(), "", "");

		_persistence.countBySBEI_TFTDI_N(0L, "null", "null");

		_persistence.countBySBEI_TFTDI_N(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		StyleBookTokenCategory newStyleBookTokenCategory =
			addStyleBookTokenCategory();

		StyleBookTokenCategory existingStyleBookTokenCategory =
			_persistence.findByPrimaryKey(
				newStyleBookTokenCategory.getPrimaryKey());

		Assert.assertEquals(
			existingStyleBookTokenCategory, newStyleBookTokenCategory);
	}

	@Test(expected = NoSuchTokenCategoryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<StyleBookTokenCategory> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"StyleBookTokenCategory", "mvccVersion", true, "ctCollectionId",
			true, "styleBookTokenCategoryId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "styleBookEntryId", true,
			"themeFrontendTokenDefinitionId", true, "name", true, "description",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		StyleBookTokenCategory newStyleBookTokenCategory =
			addStyleBookTokenCategory();

		StyleBookTokenCategory existingStyleBookTokenCategory =
			_persistence.fetchByPrimaryKey(
				newStyleBookTokenCategory.getPrimaryKey());

		Assert.assertEquals(
			existingStyleBookTokenCategory, newStyleBookTokenCategory);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookTokenCategory missingStyleBookTokenCategory =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingStyleBookTokenCategory);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		StyleBookTokenCategory newStyleBookTokenCategory1 =
			addStyleBookTokenCategory();
		StyleBookTokenCategory newStyleBookTokenCategory2 =
			addStyleBookTokenCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newStyleBookTokenCategory1.getPrimaryKey());
		primaryKeys.add(newStyleBookTokenCategory2.getPrimaryKey());

		Map<Serializable, StyleBookTokenCategory> styleBookTokenCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, styleBookTokenCategories.size());
		Assert.assertEquals(
			newStyleBookTokenCategory1,
			styleBookTokenCategories.get(
				newStyleBookTokenCategory1.getPrimaryKey()));
		Assert.assertEquals(
			newStyleBookTokenCategory2,
			styleBookTokenCategories.get(
				newStyleBookTokenCategory2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, StyleBookTokenCategory> styleBookTokenCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(styleBookTokenCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		StyleBookTokenCategory newStyleBookTokenCategory =
			addStyleBookTokenCategory();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newStyleBookTokenCategory.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, StyleBookTokenCategory> styleBookTokenCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, styleBookTokenCategories.size());
		Assert.assertEquals(
			newStyleBookTokenCategory,
			styleBookTokenCategories.get(
				newStyleBookTokenCategory.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, StyleBookTokenCategory> styleBookTokenCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(styleBookTokenCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		StyleBookTokenCategory newStyleBookTokenCategory =
			addStyleBookTokenCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newStyleBookTokenCategory.getPrimaryKey());

		Map<Serializable, StyleBookTokenCategory> styleBookTokenCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, styleBookTokenCategories.size());
		Assert.assertEquals(
			newStyleBookTokenCategory,
			styleBookTokenCategories.get(
				newStyleBookTokenCategory.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			StyleBookTokenCategoryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<StyleBookTokenCategory>() {

				@Override
				public void performAction(
					StyleBookTokenCategory styleBookTokenCategory) {

					Assert.assertNotNull(styleBookTokenCategory);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		StyleBookTokenCategory newStyleBookTokenCategory =
			addStyleBookTokenCategory();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookTokenCategory.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"styleBookTokenCategoryId",
				newStyleBookTokenCategory.getStyleBookTokenCategoryId()));

		List<StyleBookTokenCategory> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		StyleBookTokenCategory existingStyleBookTokenCategory = result.get(0);

		Assert.assertEquals(
			existingStyleBookTokenCategory, newStyleBookTokenCategory);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookTokenCategory.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"styleBookTokenCategoryId", RandomTestUtil.nextLong()));

		List<StyleBookTokenCategory> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		StyleBookTokenCategory newStyleBookTokenCategory =
			addStyleBookTokenCategory();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookTokenCategory.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("styleBookTokenCategoryId"));

		Object newStyleBookTokenCategoryId =
			newStyleBookTokenCategory.getStyleBookTokenCategoryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"styleBookTokenCategoryId",
				new Object[] {newStyleBookTokenCategoryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingStyleBookTokenCategoryId = result.get(0);

		Assert.assertEquals(
			existingStyleBookTokenCategoryId, newStyleBookTokenCategoryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookTokenCategory.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("styleBookTokenCategoryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"styleBookTokenCategoryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		StyleBookTokenCategory newStyleBookTokenCategory =
			addStyleBookTokenCategory();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newStyleBookTokenCategory.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		StyleBookTokenCategory newStyleBookTokenCategory =
			addStyleBookTokenCategory();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookTokenCategory.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"styleBookTokenCategoryId",
				newStyleBookTokenCategory.getStyleBookTokenCategoryId()));

		List<StyleBookTokenCategory> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		StyleBookTokenCategory styleBookTokenCategory) {

		Assert.assertEquals(
			Long.valueOf(styleBookTokenCategory.getStyleBookEntryId()),
			ReflectionTestUtil.<Long>invoke(
				styleBookTokenCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "styleBookEntryId"));
		Assert.assertEquals(
			styleBookTokenCategory.getThemeFrontendTokenDefinitionId(),
			ReflectionTestUtil.invoke(
				styleBookTokenCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class},
				"themeFrontendTokenDefinitionId"));
		Assert.assertEquals(
			styleBookTokenCategory.getName(),
			ReflectionTestUtil.invoke(
				styleBookTokenCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected StyleBookTokenCategory addStyleBookTokenCategory()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		StyleBookTokenCategory styleBookTokenCategory = _persistence.create(pk);

		styleBookTokenCategory.setMvccVersion(RandomTestUtil.nextLong());

		styleBookTokenCategory.setCtCollectionId(RandomTestUtil.nextLong());

		styleBookTokenCategory.setGroupId(RandomTestUtil.nextLong());

		styleBookTokenCategory.setCompanyId(RandomTestUtil.nextLong());

		styleBookTokenCategory.setUserId(RandomTestUtil.nextLong());

		styleBookTokenCategory.setUserName(RandomTestUtil.randomString());

		styleBookTokenCategory.setCreateDate(RandomTestUtil.nextDate());

		styleBookTokenCategory.setModifiedDate(RandomTestUtil.nextDate());

		styleBookTokenCategory.setStyleBookEntryId(RandomTestUtil.nextLong());

		styleBookTokenCategory.setThemeFrontendTokenDefinitionId(
			RandomTestUtil.randomString());

		styleBookTokenCategory.setName(RandomTestUtil.randomString());

		styleBookTokenCategory.setDescription(RandomTestUtil.randomString());

		_styleBookTokenCategories.add(
			_persistence.update(styleBookTokenCategory));

		return styleBookTokenCategory;
	}

	private List<StyleBookTokenCategory> _styleBookTokenCategories =
		new ArrayList<StyleBookTokenCategory>();
	private StyleBookTokenCategoryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:2064846695