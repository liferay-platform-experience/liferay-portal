/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.token.definition.FrontendTokenCategory;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.style.book.exception.DuplicateStyleBookTokenCategoryNameException;
import com.liferay.style.book.exception.StyleBookTokenCategoryNameException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.model.StyleBookTokenCategory;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.service.StyleBookTokenCategoryLocalService;
import com.liferay.style.book.service.StyleBookTokenCategoryService;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Lima
 */
@RunWith(Arquillian.class)
public class StyleBookTokenCategoryServiceTest {

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

		_styleBookEntry = _styleBookEntryLocalService.addStyleBookEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			_group.getGroupId(), false, null, RandomTestUtil.randomString(),
			null, RandomTestUtil.randomString(), _serviceContext);
	}

	@Test
	public void testAddStyleBookTokenCategory() throws Exception {
		String themeFrontendTokenDefinitionId = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String description = RandomTestUtil.randomString();

		StyleBookTokenCategory styleBookTokenCategory =
			_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
				_styleBookEntry.getStyleBookEntryId(),
				themeFrontendTokenDefinitionId, name, description,
				_serviceContext);

		Assert.assertEquals(
			description, styleBookTokenCategory.getDescription());
		Assert.assertEquals(
			_group.getGroupId(), styleBookTokenCategory.getGroupId());
		Assert.assertEquals(name, styleBookTokenCategory.getName());
		Assert.assertEquals(
			_styleBookEntry.getStyleBookEntryId(),
			styleBookTokenCategory.getStyleBookEntryId());
		Assert.assertEquals(
			themeFrontendTokenDefinitionId,
			styleBookTokenCategory.getThemeFrontendTokenDefinitionId());
		Assert.assertEquals(
			TestPropsValues.getUserId(), styleBookTokenCategory.getUserId());

		Assert.assertNotNull(
			_styleBookTokenCategoryLocalService.getStyleBookTokenCategory(
				styleBookTokenCategory.getStyleBookTokenCategoryId()));
	}

	@Test
	public void testAddStyleBookTokenCategoryWhenNameIsBaseTokenCategoryName()
		throws Exception {

		String foundCategoryName = null;
		String foundThemeId = null;

		Collection<FrontendTokenDefinition> frontendTokenDefinitions =
			_frontendTokenDefinitionRegistry.getFrontendTokenDefinitions(
				TestPropsValues.getCompanyId());

		outer:
		for (FrontendTokenDefinition frontendTokenDefinition :
				frontendTokenDefinitions) {

			for (FrontendTokenCategory frontendTokenCategory :
					frontendTokenDefinition.getFrontendTokenCategories()) {

				JSONObject jsonObject = frontendTokenCategory.getJSONObject(
					LocaleUtil.getSiteDefault());

				String categoryName = jsonObject.getString("name");

				if ((categoryName != null) && !categoryName.isEmpty()) {
					foundCategoryName = categoryName;
					foundThemeId = frontendTokenDefinition.getThemeId();

					break outer;
				}
			}
		}

		Assume.assumeTrue(
			"No registered frontend token definition with token categories",
			foundCategoryName != null);

		try {
			_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
				_styleBookEntry.getStyleBookEntryId(), foundThemeId,
				foundCategoryName, RandomTestUtil.randomString(),
				_serviceContext);

			Assert.fail();
		}
		catch (DuplicateStyleBookTokenCategoryNameException
					duplicateStyleBookTokenCategoryNameException) {
		}
	}

	@Test(expected = StyleBookTokenCategoryNameException.class)
	public void testAddStyleBookTokenCategoryWhenNameIsBlank()
		throws Exception {

		_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
			_styleBookEntry.getStyleBookEntryId(),
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), _serviceContext);
	}

	@Test(expected = DuplicateStyleBookTokenCategoryNameException.class)
	public void testAddStyleBookTokenCategoryWhenNameIsDuplicate()
		throws Exception {

		String themeFrontendTokenDefinitionId = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();

		_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
			_styleBookEntry.getStyleBookEntryId(),
			themeFrontendTokenDefinitionId, name, RandomTestUtil.randomString(),
			_serviceContext);

		_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
			_styleBookEntry.getStyleBookEntryId(),
			themeFrontendTokenDefinitionId, name, RandomTestUtil.randomString(),
			_serviceContext);
	}

	@Test(expected = StyleBookTokenCategoryNameException.class)
	public void testAddStyleBookTokenCategoryWhenNameIsNull() throws Exception {
		_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
			_styleBookEntry.getStyleBookEntryId(),
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_serviceContext);
	}

	@Test(expected = StyleBookTokenCategoryNameException.class)
	public void testAddStyleBookTokenCategoryWhenNameIsTooLong()
		throws Exception {

		_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
			_styleBookEntry.getStyleBookEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(76),
			RandomTestUtil.randomString(), _serviceContext);
	}

	@Test
	public void testAddStyleBookTokenCategoryWhenStyleBookEntryIdIsDifferent()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String themeFrontendTokenDefinitionId = RandomTestUtil.randomString();

		StyleBookTokenCategory styleBookTokenCategory1 =
			_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
				_styleBookEntry.getStyleBookEntryId(),
				themeFrontendTokenDefinitionId, name,
				RandomTestUtil.randomString(), _serviceContext);

		StyleBookEntry styleBookEntry =
			_styleBookEntryLocalService.addStyleBookEntry(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), false, null, RandomTestUtil.randomString(),
				null, RandomTestUtil.randomString(), _serviceContext);

		StyleBookTokenCategory styleBookTokenCategory2 =
			_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
				styleBookEntry.getStyleBookEntryId(),
				themeFrontendTokenDefinitionId, name,
				RandomTestUtil.randomString(), _serviceContext);

		Assert.assertNotEquals(
			styleBookTokenCategory1.getStyleBookTokenCategoryId(),
			styleBookTokenCategory2.getStyleBookTokenCategoryId());
	}

	@Test
	public void testAddStyleBookTokenCategoryWhenThemeFrontendTokenDefinitionIdIsDifferent()
		throws Exception {

		String name = RandomTestUtil.randomString();
		String themeFrontendTokenDefinitionId1 = RandomTestUtil.randomString();
		String themeFrontendTokenDefinitionId2 = RandomTestUtil.randomString();

		StyleBookTokenCategory styleBookTokenCategory1 =
			_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
				_styleBookEntry.getStyleBookEntryId(),
				themeFrontendTokenDefinitionId1, name,
				RandomTestUtil.randomString(), _serviceContext);

		StyleBookTokenCategory styleBookTokenCategory2 =
			_styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
				_styleBookEntry.getStyleBookEntryId(),
				themeFrontendTokenDefinitionId2, name,
				RandomTestUtil.randomString(), _serviceContext);

		Assert.assertNotEquals(
			styleBookTokenCategory1.getStyleBookTokenCategoryId(),
			styleBookTokenCategory2.getStyleBookTokenCategoryId());
	}

	@Test
	public void testAddStyleBookTokenCategoryWithoutPermission()
		throws Exception {

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_styleBookTokenCategoryService.addStyleBookTokenCategory(
				_styleBookEntry.getStyleBookEntryId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), _serviceContext);

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Inject
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;
	private StyleBookEntry _styleBookEntry;

	@Inject
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Inject
	private StyleBookTokenCategoryLocalService
		_styleBookTokenCategoryLocalService;

	@Inject
	private StyleBookTokenCategoryService _styleBookTokenCategoryService;

}