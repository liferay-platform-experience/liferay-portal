/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence;

import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.style.book.exception.NoSuchTokenCategoryException;
import com.liferay.style.book.model.StyleBookTokenCategory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the style book token category service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookTokenCategoryUtil
 * @generated
 */
@ProviderType
public interface StyleBookTokenCategoryPersistence
	extends BasePersistence<StyleBookTokenCategory>,
			CTPersistence<StyleBookTokenCategory> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link StyleBookTokenCategoryUtil} to access the style book token category persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

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
	public java.util.List<StyleBookTokenCategory> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookTokenCategory>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Returns the first style book token category in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book token category
	 * @throws NoSuchTokenCategoryException if a matching style book token category could not be found
	 */
	public StyleBookTokenCategory findByStyleBookEntryId_First(
			long styleBookEntryId,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookTokenCategory> orderByComparator)
		throws NoSuchTokenCategoryException;

	/**
	 * Returns the first style book token category in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book token category, or <code>null</code> if a matching style book token category could not be found
	 */
	public StyleBookTokenCategory fetchByStyleBookEntryId_First(
		long styleBookEntryId,
		com.liferay.portal.kernel.util.OrderByComparator<StyleBookTokenCategory>
			orderByComparator);

	/**
	 * Removes all the style book token categories where styleBookEntryId = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 */
	public void removeByStyleBookEntryId(long styleBookEntryId);

	/**
	 * Returns the number of style book token categories where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the number of matching style book token categories
	 */
	public int countByStyleBookEntryId(long styleBookEntryId);

	/**
	 * Returns the style book token category where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchTokenCategoryException</code> if it could not be found.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the matching style book token category
	 * @throws NoSuchTokenCategoryException if a matching style book token category could not be found
	 */
	public StyleBookTokenCategory findBySBEI_TFTDI_N(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name)
		throws NoSuchTokenCategoryException;

	/**
	 * Returns the style book token category where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching style book token category, or <code>null</code> if a matching style book token category could not be found
	 */
	public StyleBookTokenCategory fetchBySBEI_TFTDI_N(
		long styleBookEntryId, String themeFrontendTokenDefinitionId,
		String name, boolean useFinderCache);

	/**
	 * Removes the style book token category where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the style book token category that was removed
	 */
	public StyleBookTokenCategory removeBySBEI_TFTDI_N(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name)
		throws NoSuchTokenCategoryException;

	/**
	 * Returns the number of style book token categories where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the number of matching style book token categories
	 */
	public int countBySBEI_TFTDI_N(
		long styleBookEntryId, String themeFrontendTokenDefinitionId,
		String name);

	/**
	 * Creates a new style book token category with the primary key. Does not add the style book token category to the database.
	 *
	 * @param styleBookTokenCategoryId the primary key for the new style book token category
	 * @return the new style book token category
	 */
	public StyleBookTokenCategory create(long styleBookTokenCategoryId);

	/**
	 * Removes the style book token category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category that was removed
	 * @throws NoSuchTokenCategoryException if a style book token category with the primary key could not be found
	 */
	public StyleBookTokenCategory remove(long styleBookTokenCategoryId)
		throws NoSuchTokenCategoryException;

	public StyleBookTokenCategory updateImpl(
		StyleBookTokenCategory styleBookTokenCategory);

	/**
	 * Returns the style book token category with the primary key or throws a <code>NoSuchTokenCategoryException</code> if it could not be found.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category
	 * @throws NoSuchTokenCategoryException if a style book token category with the primary key could not be found
	 */
	public StyleBookTokenCategory findByPrimaryKey(
			long styleBookTokenCategoryId)
		throws NoSuchTokenCategoryException;

	/**
	 * Returns the style book token category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category, or <code>null</code> if a style book token category with the primary key could not be found
	 */
	public StyleBookTokenCategory fetchByPrimaryKey(
		long styleBookTokenCategoryId);

	/**
	 * Returns the style book token category where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the matching style book token category, or <code>null</code> if a matching style book token category could not be found
	 */
	public default StyleBookTokenCategory fetchBySBEI_TFTDI_N(
		long styleBookEntryId, String themeFrontendTokenDefinitionId,
		String name) {

		return fetchBySBEI_TFTDI_N(
			styleBookEntryId, themeFrontendTokenDefinitionId, name, true);
	}

	/**
	 * Returns all the style book token categories where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the matching style book token categories
	 */
	public default java.util.List<StyleBookTokenCategory>
		findByStyleBookEntryId(long styleBookEntryId) {

		return findByStyleBookEntryId(
			styleBookEntryId,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS,
			com.liferay.portal.kernel.dao.orm.QueryUtil.ALL_POS, null, true);
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
	public default java.util.List<StyleBookTokenCategory>
		findByStyleBookEntryId(long styleBookEntryId, int start, int end) {

		return findByStyleBookEntryId(styleBookEntryId, start, end, null, true);
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
	public default java.util.List<StyleBookTokenCategory>
		findByStyleBookEntryId(
			long styleBookEntryId, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<StyleBookTokenCategory> orderByComparator) {

		return findByStyleBookEntryId(
			styleBookEntryId, start, end, orderByComparator, true);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1881246500