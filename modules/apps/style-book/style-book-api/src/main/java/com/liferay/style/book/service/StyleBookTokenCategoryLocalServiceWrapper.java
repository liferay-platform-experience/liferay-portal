/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;
import com.liferay.style.book.model.StyleBookTokenCategory;

/**
 * Provides a wrapper for {@link StyleBookTokenCategoryLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see StyleBookTokenCategoryLocalService
 * @generated
 */
public class StyleBookTokenCategoryLocalServiceWrapper
	implements ServiceWrapper<StyleBookTokenCategoryLocalService>,
			   StyleBookTokenCategoryLocalService {

	public StyleBookTokenCategoryLocalServiceWrapper() {
		this(null);
	}

	public StyleBookTokenCategoryLocalServiceWrapper(
		StyleBookTokenCategoryLocalService styleBookTokenCategoryLocalService) {

		_styleBookTokenCategoryLocalService =
			styleBookTokenCategoryLocalService;
	}

	@Override
	public StyleBookTokenCategory addStyleBookTokenCategory(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name, String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
			styleBookEntryId, themeFrontendTokenDefinitionId, name, description,
			serviceContext);
	}

	/**
	 * Adds the style book token category to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookTokenCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookTokenCategory the style book token category
	 * @return the style book token category that was added
	 */
	@Override
	public StyleBookTokenCategory addStyleBookTokenCategory(
		StyleBookTokenCategory styleBookTokenCategory) {

		return _styleBookTokenCategoryLocalService.addStyleBookTokenCategory(
			styleBookTokenCategory);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookTokenCategoryLocalService.createPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Creates a new style book token category with the primary key. Does not add the style book token category to the database.
	 *
	 * @param styleBookTokenCategoryId the primary key for the new style book token category
	 * @return the new style book token category
	 */
	@Override
	public StyleBookTokenCategory createStyleBookTokenCategory(
		long styleBookTokenCategoryId) {

		return _styleBookTokenCategoryLocalService.createStyleBookTokenCategory(
			styleBookTokenCategoryId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookTokenCategoryLocalService.deletePersistedModel(
			persistedModel);
	}

	/**
	 * Deletes the style book token category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookTokenCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category that was removed
	 * @throws PortalException if a style book token category with the primary key could not be found
	 */
	@Override
	public StyleBookTokenCategory deleteStyleBookTokenCategory(
			long styleBookTokenCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookTokenCategoryLocalService.deleteStyleBookTokenCategory(
			styleBookTokenCategoryId);
	}

	/**
	 * Deletes the style book token category from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookTokenCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookTokenCategory the style book token category
	 * @return the style book token category that was removed
	 */
	@Override
	public StyleBookTokenCategory deleteStyleBookTokenCategory(
		StyleBookTokenCategory styleBookTokenCategory) {

		return _styleBookTokenCategoryLocalService.deleteStyleBookTokenCategory(
			styleBookTokenCategory);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _styleBookTokenCategoryLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _styleBookTokenCategoryLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _styleBookTokenCategoryLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _styleBookTokenCategoryLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookTokenCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _styleBookTokenCategoryLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookTokenCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _styleBookTokenCategoryLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _styleBookTokenCategoryLocalService.dynamicQueryCount(
			dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _styleBookTokenCategoryLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public StyleBookTokenCategory fetchStyleBookTokenCategory(
		long styleBookTokenCategoryId) {

		return _styleBookTokenCategoryLocalService.fetchStyleBookTokenCategory(
			styleBookTokenCategoryId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _styleBookTokenCategoryLocalService.getActionableDynamicQuery();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _styleBookTokenCategoryLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _styleBookTokenCategoryLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookTokenCategoryLocalService.getPersistedModel(
			primaryKeyObj);
	}

	/**
	 * Returns a range of all the style book token categories.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.style.book.model.impl.StyleBookTokenCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of style book token categories
	 * @param end the upper bound of the range of style book token categories (not inclusive)
	 * @return the range of style book token categories
	 */
	@Override
	public java.util.List<StyleBookTokenCategory> getStyleBookTokenCategories(
		int start, int end) {

		return _styleBookTokenCategoryLocalService.getStyleBookTokenCategories(
			start, end);
	}

	/**
	 * Returns the number of style book token categories.
	 *
	 * @return the number of style book token categories
	 */
	@Override
	public int getStyleBookTokenCategoriesCount() {
		return _styleBookTokenCategoryLocalService.
			getStyleBookTokenCategoriesCount();
	}

	/**
	 * Returns the style book token category with the primary key.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category
	 * @throws PortalException if a style book token category with the primary key could not be found
	 */
	@Override
	public StyleBookTokenCategory getStyleBookTokenCategory(
			long styleBookTokenCategoryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _styleBookTokenCategoryLocalService.getStyleBookTokenCategory(
			styleBookTokenCategoryId);
	}

	/**
	 * Updates the style book token category in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect StyleBookTokenCategoryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param styleBookTokenCategory the style book token category
	 * @return the style book token category that was updated
	 */
	@Override
	public StyleBookTokenCategory updateStyleBookTokenCategory(
		StyleBookTokenCategory styleBookTokenCategory) {

		return _styleBookTokenCategoryLocalService.updateStyleBookTokenCategory(
			styleBookTokenCategory);
	}

	@Override
	public BasePersistence<?> getBasePersistence() {
		return _styleBookTokenCategoryLocalService.getBasePersistence();
	}

	@Override
	public CTPersistence<StyleBookTokenCategory> getCTPersistence() {
		return _styleBookTokenCategoryLocalService.getCTPersistence();
	}

	@Override
	public Class<StyleBookTokenCategory> getModelClass() {
		return _styleBookTokenCategoryLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<StyleBookTokenCategory>, R, E>
				updateUnsafeFunction)
		throws E {

		return _styleBookTokenCategoryLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public StyleBookTokenCategoryLocalService getWrappedService() {
		return _styleBookTokenCategoryLocalService;
	}

	@Override
	public void setWrappedService(
		StyleBookTokenCategoryLocalService styleBookTokenCategoryLocalService) {

		_styleBookTokenCategoryLocalService =
			styleBookTokenCategoryLocalService;
	}

	private StyleBookTokenCategoryLocalService
		_styleBookTokenCategoryLocalService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-536278102