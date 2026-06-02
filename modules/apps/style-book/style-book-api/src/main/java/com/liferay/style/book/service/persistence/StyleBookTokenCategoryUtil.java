/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.style.book.model.StyleBookTokenCategory;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The persistence utility for the style book token category service. This utility wraps <code>com.liferay.style.book.service.persistence.impl.StyleBookTokenCategoryPersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookTokenCategoryPersistence
 * @generated
 */
public class StyleBookTokenCategoryUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(List)
	 */
	public static void cacheResult(
		List<StyleBookTokenCategory> styleBookTokenCategories) {

		getPersistence().cacheResult(styleBookTokenCategories);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#cacheResult(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void cacheResult(
		StyleBookTokenCategory styleBookTokenCategory) {

		getPersistence().cacheResult(styleBookTokenCategory);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(
		StyleBookTokenCategory styleBookTokenCategory) {

		getPersistence().clearCache(styleBookTokenCategory);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, StyleBookTokenCategory> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<StyleBookTokenCategory> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<StyleBookTokenCategory> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<StyleBookTokenCategory> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<StyleBookTokenCategory> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static StyleBookTokenCategory update(
		StyleBookTokenCategory styleBookTokenCategory) {

		return getPersistence().update(styleBookTokenCategory);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static StyleBookTokenCategory update(
		StyleBookTokenCategory styleBookTokenCategory,
		ServiceContext serviceContext) {

		return getPersistence().update(styleBookTokenCategory, serviceContext);
	}

	/**
	 * Returns an ordered range of all the style book token categories where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookTokenCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book token categories
	 * @param end the upper bound of the range of style book token categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book token categories
	 */
	public static List<StyleBookTokenCategory> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end,
		OrderByComparator<StyleBookTokenCategory> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findByStyleBookEntryId(
			styleBookEntryId, start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book token category in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book token category
	 * @throws NoSuchTokenCategoryException if a matching style book token category could not be found
	 */
	public static StyleBookTokenCategory findByStyleBookEntryId_First(
			long styleBookEntryId,
			OrderByComparator<StyleBookTokenCategory> orderByComparator)
		throws com.liferay.style.book.exception.NoSuchTokenCategoryException {

		return getPersistence().findByStyleBookEntryId_First(
			styleBookEntryId, orderByComparator);
	}

	/**
	 * Returns the first style book token category in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book token category, or <code>null</code> if a matching style book token category could not be found
	 */
	public static StyleBookTokenCategory fetchByStyleBookEntryId_First(
		long styleBookEntryId,
		OrderByComparator<StyleBookTokenCategory> orderByComparator) {

		return getPersistence().fetchByStyleBookEntryId_First(
			styleBookEntryId, orderByComparator);
	}

	/**
	 * Removes all the style book token categories where styleBookEntryId = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 */
	public static void removeByStyleBookEntryId(long styleBookEntryId) {
		getPersistence().removeByStyleBookEntryId(styleBookEntryId);
	}

	/**
	 * Returns the number of style book token categories where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the number of matching style book token categories
	 */
	public static int countByStyleBookEntryId(long styleBookEntryId) {
		return getPersistence().countByStyleBookEntryId(styleBookEntryId);
	}

	/**
	 * Returns the style book token category where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchTokenCategoryException</code> if it could not be found.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the matching style book token category
	 * @throws NoSuchTokenCategoryException if a matching style book token category could not be found
	 */
	public static StyleBookTokenCategory findBySBEI_TFTDI_N(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name)
		throws com.liferay.style.book.exception.NoSuchTokenCategoryException {

		return getPersistence().findBySBEI_TFTDI_N(
			styleBookEntryId, themeFrontendTokenDefinitionId, name);
	}

	/**
	 * Returns the style book token category where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book token category, or <code>null</code> if a matching style book token category could not be found
	 */
	public static StyleBookTokenCategory fetchBySBEI_TFTDI_N(
		long styleBookEntryId, String themeFrontendTokenDefinitionId,
		String name, boolean useFinderCache) {

		return getPersistence().fetchBySBEI_TFTDI_N(
			styleBookEntryId, themeFrontendTokenDefinitionId, name,
			useFinderCache);
	}

	/**
	 * Removes the style book token category where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the style book token category that was removed
	 */
	public static StyleBookTokenCategory removeBySBEI_TFTDI_N(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name)
		throws com.liferay.style.book.exception.NoSuchTokenCategoryException {

		return getPersistence().removeBySBEI_TFTDI_N(
			styleBookEntryId, themeFrontendTokenDefinitionId, name);
	}

	/**
	 * Returns the number of style book token categories where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the number of matching style book token categories
	 */
	public static int countBySBEI_TFTDI_N(
		long styleBookEntryId, String themeFrontendTokenDefinitionId,
		String name) {

		return getPersistence().countBySBEI_TFTDI_N(
			styleBookEntryId, themeFrontendTokenDefinitionId, name);
	}

	/**
	 * Creates a new style book token category with the primary key. Does not add the style book token category to the database.
	 *
	 * @param styleBookTokenCategoryId the primary key for the new style book token category
	 * @return the new style book token category
	 */
	public static StyleBookTokenCategory create(long styleBookTokenCategoryId) {
		return getPersistence().create(styleBookTokenCategoryId);
	}

	/**
	 * Removes the style book token category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category that was removed
	 * @throws NoSuchTokenCategoryException if a style book token category with the primary key could not be found
	 */
	public static StyleBookTokenCategory remove(long styleBookTokenCategoryId)
		throws com.liferay.style.book.exception.NoSuchTokenCategoryException {

		return getPersistence().remove(styleBookTokenCategoryId);
	}

	public static StyleBookTokenCategory updateImpl(
		StyleBookTokenCategory styleBookTokenCategory) {

		return getPersistence().updateImpl(styleBookTokenCategory);
	}

	/**
	 * Returns the style book token category with the primary key or throws a <code>NoSuchTokenCategoryException</code> if it could not be found.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category
	 * @throws NoSuchTokenCategoryException if a style book token category with the primary key could not be found
	 */
	public static StyleBookTokenCategory findByPrimaryKey(
			long styleBookTokenCategoryId)
		throws com.liferay.style.book.exception.NoSuchTokenCategoryException {

		return getPersistence().findByPrimaryKey(styleBookTokenCategoryId);
	}

	/**
	 * Returns the style book token category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category, or <code>null</code> if a style book token category with the primary key could not be found
	 */
	public static StyleBookTokenCategory fetchByPrimaryKey(
		long styleBookTokenCategoryId) {

		return getPersistence().fetchByPrimaryKey(styleBookTokenCategoryId);
	}

	/**
	 * Returns the style book token category where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the matching style book token category, or <code>null</code> if a matching style book token category could not be found
	 */
	public static StyleBookTokenCategory fetchBySBEI_TFTDI_N(
		long styleBookEntryId, String themeFrontendTokenDefinitionId,
		String name) {

		return getPersistence().fetchBySBEI_TFTDI_N(
			styleBookEntryId, themeFrontendTokenDefinitionId, name);
	}

	/**
	 * Returns all the style book token categories where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the matching style book token categories
	 */
	public static List<StyleBookTokenCategory> findByStyleBookEntryId(
		long styleBookEntryId) {

		return getPersistence().findByStyleBookEntryId(styleBookEntryId);
	}

	/**
	 * Returns a range of all the style book token categories where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookTokenCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book token categories
	 * @param end the upper bound of the range of style book token categories (not inclusive)
	 * @return the range of matching style book token categories
	 */
	public static List<StyleBookTokenCategory> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end) {

		return getPersistence().findByStyleBookEntryId(
			styleBookEntryId, start, end);
	}

	/**
	 * Returns an ordered range of all the style book token categories where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookTokenCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book token categories
	 * @param end the upper bound of the range of style book token categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching style book token categories
	 */
	public static List<StyleBookTokenCategory> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end,
		OrderByComparator<StyleBookTokenCategory> orderByComparator) {

		return getPersistence().findByStyleBookEntryId(
			styleBookEntryId, start, end, orderByComparator);
	}

	public static StyleBookTokenCategoryPersistence getPersistence() {
		return _persistence;
	}

	public static void setPersistence(
		StyleBookTokenCategoryPersistence persistence) {

		_persistence = persistence;
	}

	private static volatile StyleBookTokenCategoryPersistence _persistence;

}
// LIFERAY-SERVICE-BUILDER-HASH:1609622954