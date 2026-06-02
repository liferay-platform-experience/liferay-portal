/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence.impl;

import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.service.persistence.impl.CollectionPersistenceFinder;
import com.liferay.portal.kernel.service.persistence.impl.FinderColumn;
import com.liferay.portal.kernel.service.persistence.impl.UniquePersistenceFinder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.style.book.exception.NoSuchTokenCategoryException;
import com.liferay.style.book.model.StyleBookTokenCategory;
import com.liferay.style.book.model.StyleBookTokenCategoryTable;
import com.liferay.style.book.model.impl.StyleBookTokenCategoryImpl;
import com.liferay.style.book.model.impl.StyleBookTokenCategoryModelImpl;
import com.liferay.style.book.service.persistence.StyleBookTokenCategoryPersistence;
import com.liferay.style.book.service.persistence.StyleBookTokenCategoryUtil;
import com.liferay.style.book.service.persistence.impl.constants.StyleBookPersistenceConstants;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the style book token category service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = StyleBookTokenCategoryPersistence.class)
public class StyleBookTokenCategoryPersistenceImpl
	extends BasePersistenceImpl
		<StyleBookTokenCategory, NoSuchTokenCategoryException>
	implements StyleBookTokenCategoryPersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>StyleBookTokenCategoryUtil</code> to access the style book token category persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		StyleBookTokenCategoryImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private CollectionPersistenceFinder
		<StyleBookTokenCategory, NoSuchTokenCategoryException>
			_collectionPersistenceFinderByStyleBookEntryId;

	/**
	 * Returns an ordered range of all the style book token categories where styleBookEntryId = &#63;.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>StyleBookTokenCategoryModelImpl</code>.
	 * </p>
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param start the lower bound of the range of style book token categories
	 * @param end the upper bound of the range of style book token categories (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of matching style book token categories
	 */
	@Override
	public List<StyleBookTokenCategory> findByStyleBookEntryId(
		long styleBookEntryId, int start, int end,
		OrderByComparator<StyleBookTokenCategory> orderByComparator,
		boolean useFinderCache) {

		return _collectionPersistenceFinderByStyleBookEntryId.find(
			finderCache, new Object[] {styleBookEntryId}, start, end,
			orderByComparator, useFinderCache);
	}

	/**
	 * Returns the first style book token category in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book token category
	 * @throws NoSuchTokenCategoryException if a matching style book token category could not be found
	 */
	@Override
	public StyleBookTokenCategory findByStyleBookEntryId_First(
			long styleBookEntryId,
			OrderByComparator<StyleBookTokenCategory> orderByComparator)
		throws NoSuchTokenCategoryException {

		return _collectionPersistenceFinderByStyleBookEntryId.findFirst(
			finderCache, new Object[] {styleBookEntryId}, orderByComparator);
	}

	/**
	 * Returns the first style book token category in the ordered set where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param orderByComparator the comparator to order the set by (optionally <code>null</code>)
	 * @return the first matching style book token category, or <code>null</code> if a matching style book token category could not be found
	 */
	@Override
	public StyleBookTokenCategory fetchByStyleBookEntryId_First(
		long styleBookEntryId,
		OrderByComparator<StyleBookTokenCategory> orderByComparator) {

		return _collectionPersistenceFinderByStyleBookEntryId.fetchFirst(
			finderCache, new Object[] {styleBookEntryId}, orderByComparator);
	}

	/**
	 * Removes all the style book token categories where styleBookEntryId = &#63; from the database.
	 *
	 * @param styleBookEntryId the style book entry ID
	 */
	@Override
	public void removeByStyleBookEntryId(long styleBookEntryId) {
		_collectionPersistenceFinderByStyleBookEntryId.remove(
			finderCache, new Object[] {styleBookEntryId});
	}

	/**
	 * Returns the number of style book token categories where styleBookEntryId = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @return the number of matching style book token categories
	 */
	@Override
	public int countByStyleBookEntryId(long styleBookEntryId) {
		return _collectionPersistenceFinderByStyleBookEntryId.count(
			finderCache, new Object[] {styleBookEntryId});
	}

	private UniquePersistenceFinder
		<StyleBookTokenCategory, NoSuchTokenCategoryException>
			_uniquePersistenceFinderBySBEI_TFTDI_N;

	/**
	 * Returns the style book token category where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63; or throws a <code>NoSuchTokenCategoryException</code> if it could not be found.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the matching style book token category
	 * @throws NoSuchTokenCategoryException if a matching style book token category could not be found
	 */
	@Override
	public StyleBookTokenCategory findBySBEI_TFTDI_N(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name)
		throws NoSuchTokenCategoryException {

		return _uniquePersistenceFinderBySBEI_TFTDI_N.find(
			finderCache,
			new Object[] {
				styleBookEntryId, themeFrontendTokenDefinitionId, name
			});
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
	@Override
	public StyleBookTokenCategory fetchBySBEI_TFTDI_N(
		long styleBookEntryId, String themeFrontendTokenDefinitionId,
		String name, boolean useFinderCache) {

		return _uniquePersistenceFinderBySBEI_TFTDI_N.fetch(
			finderCache,
			new Object[] {
				styleBookEntryId, themeFrontendTokenDefinitionId, name
			},
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
	@Override
	public StyleBookTokenCategory removeBySBEI_TFTDI_N(
			long styleBookEntryId, String themeFrontendTokenDefinitionId,
			String name)
		throws NoSuchTokenCategoryException {

		StyleBookTokenCategory styleBookTokenCategory = findBySBEI_TFTDI_N(
			styleBookEntryId, themeFrontendTokenDefinitionId, name);

		return remove(styleBookTokenCategory);
	}

	/**
	 * Returns the number of style book token categories where styleBookEntryId = &#63; and themeFrontendTokenDefinitionId = &#63; and name = &#63;.
	 *
	 * @param styleBookEntryId the style book entry ID
	 * @param themeFrontendTokenDefinitionId the theme frontend token definition ID
	 * @param name the name
	 * @return the number of matching style book token categories
	 */
	@Override
	public int countBySBEI_TFTDI_N(
		long styleBookEntryId, String themeFrontendTokenDefinitionId,
		String name) {

		return _uniquePersistenceFinderBySBEI_TFTDI_N.count(
			finderCache,
			new Object[] {
				styleBookEntryId, themeFrontendTokenDefinitionId, name
			});
	}

	public StyleBookTokenCategoryPersistenceImpl() {
		setModelClass(StyleBookTokenCategory.class);

		setModelImplClass(StyleBookTokenCategoryImpl.class);
		setModelPKClass(long.class);

		setTable(StyleBookTokenCategoryTable.INSTANCE);
	}

	/**
	 * Creates a new style book token category with the primary key. Does not add the style book token category to the database.
	 *
	 * @param styleBookTokenCategoryId the primary key for the new style book token category
	 * @return the new style book token category
	 */
	@Override
	public StyleBookTokenCategory create(long styleBookTokenCategoryId) {
		StyleBookTokenCategory styleBookTokenCategory =
			new StyleBookTokenCategoryImpl();

		styleBookTokenCategory.setNew(true);
		styleBookTokenCategory.setPrimaryKey(styleBookTokenCategoryId);

		styleBookTokenCategory.setCompanyId(CompanyThreadLocal.getCompanyId());

		return styleBookTokenCategory;
	}

	/**
	 * Removes the style book token category with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category that was removed
	 * @throws NoSuchTokenCategoryException if a style book token category with the primary key could not be found
	 */
	@Override
	public StyleBookTokenCategory remove(long styleBookTokenCategoryId)
		throws NoSuchTokenCategoryException {

		return remove((Serializable)styleBookTokenCategoryId);
	}

	@Override
	protected StyleBookTokenCategory removeImpl(
		StyleBookTokenCategory styleBookTokenCategory) {

		Session session = null;

		try {
			session = openSession();

			if (!session.contains(styleBookTokenCategory)) {
				styleBookTokenCategory = (StyleBookTokenCategory)session.get(
					StyleBookTokenCategoryImpl.class,
					styleBookTokenCategory.getPrimaryKeyObj());
			}

			if ((styleBookTokenCategory != null) &&
				ctPersistenceHelper.isRemove(styleBookTokenCategory)) {

				session.delete(styleBookTokenCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (styleBookTokenCategory != null) {
			clearCache(styleBookTokenCategory);
		}

		return styleBookTokenCategory;
	}

	@Override
	public StyleBookTokenCategory updateImpl(
		StyleBookTokenCategory styleBookTokenCategory) {

		boolean isNew = styleBookTokenCategory.isNew();

		if (!(styleBookTokenCategory instanceof
				StyleBookTokenCategoryModelImpl)) {

			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(styleBookTokenCategory.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					styleBookTokenCategory);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in styleBookTokenCategory proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom StyleBookTokenCategory implementation " +
					styleBookTokenCategory.getClass());
		}

		StyleBookTokenCategoryModelImpl styleBookTokenCategoryModelImpl =
			(StyleBookTokenCategoryModelImpl)styleBookTokenCategory;

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Date date = new Date();

		if (isNew && (styleBookTokenCategory.getCreateDate() == null)) {
			if (serviceContext == null) {
				styleBookTokenCategory.setCreateDate(date);
			}
			else {
				styleBookTokenCategory.setCreateDate(
					serviceContext.getCreateDate(date));
			}
		}

		if (!styleBookTokenCategoryModelImpl.hasSetModifiedDate()) {
			if (serviceContext == null) {
				styleBookTokenCategory.setModifiedDate(date);
			}
			else {
				styleBookTokenCategory.setModifiedDate(
					serviceContext.getModifiedDate(date));
			}
		}

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(styleBookTokenCategory)) {
				if (!isNew) {
					session.evict(
						StyleBookTokenCategoryImpl.class,
						styleBookTokenCategory.getPrimaryKeyObj());
				}

				session.save(styleBookTokenCategory);
			}
			else {
				styleBookTokenCategory = (StyleBookTokenCategory)session.merge(
					styleBookTokenCategory);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		cacheUniqueFindersResult(styleBookTokenCategory, false);

		if (isNew) {
			styleBookTokenCategory.setNew(false);
		}

		styleBookTokenCategory.resetOriginalValues();

		return styleBookTokenCategory;
	}

	/**
	 * Returns the style book token category with the primary key or throws a <code>NoSuchTokenCategoryException</code> if it could not be found.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category
	 * @throws NoSuchTokenCategoryException if a style book token category with the primary key could not be found
	 */
	@Override
	public StyleBookTokenCategory findByPrimaryKey(
			long styleBookTokenCategoryId)
		throws NoSuchTokenCategoryException {

		return findByPrimaryKey((Serializable)styleBookTokenCategoryId);
	}

	@Override
	protected CTPersistenceHelper getCTPersistenceHelper() {
		return ctPersistenceHelper;
	}

	/**
	 * Returns the style book token category with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param styleBookTokenCategoryId the primary key of the style book token category
	 * @return the style book token category, or <code>null</code> if a style book token category with the primary key could not be found
	 */
	@Override
	public StyleBookTokenCategory fetchByPrimaryKey(
		long styleBookTokenCategoryId) {

		return fetchByPrimaryKey((Serializable)styleBookTokenCategoryId);
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "styleBookTokenCategoryId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_STYLEBOOKTOKENCATEGORY;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.getOrDefault(
			ctColumnResolutionType, Collections.emptySet());
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return StyleBookTokenCategoryModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "StyleBookTokenCategory";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("groupId");
		ctStrictColumnNames.add("companyId");
		ctStrictColumnNames.add("userId");
		ctStrictColumnNames.add("userName");
		ctStrictColumnNames.add("createDate");
		ctIgnoreColumnNames.add("modifiedDate");
		ctMergeColumnNames.add("styleBookEntryId");
		ctMergeColumnNames.add("themeFrontendTokenDefinitionId");
		ctMergeColumnNames.add("name");
		ctMergeColumnNames.add("description");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("styleBookTokenCategoryId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(
			new String[] {
				"styleBookEntryId", "themeFrontendTokenDefinitionId", "name"
			});
	}

	/**
	 * Initializes the style book token category persistence.
	 */
	@Activate
	public void activate() {
		_collectionPersistenceFinderByStyleBookEntryId =
			new CollectionPersistenceFinder<>(
				this,
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITH_PAGINATION,
					"findByStyleBookEntryId",
					new String[] {
						Long.class.getName(), Integer.class.getName(),
						Integer.class.getName(),
						OrderByComparator.class.getName()
					},
					new String[] {"styleBookEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"findByStyleBookEntryId",
					new String[] {Long.class.getName()},
					new String[] {"styleBookEntryId"}, true),
				new FinderPath(
					FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION,
					"countByStyleBookEntryId",
					new String[] {Long.class.getName()},
					new String[] {"styleBookEntryId"}, false),
				_SQL_SELECT_STYLEBOOKTOKENCATEGORY_WHERE,
				_SQL_COUNT_STYLEBOOKTOKENCATEGORY_WHERE,
				StyleBookTokenCategoryModelImpl.ORDER_BY_JPQL,
				_ENTITY_ALIAS_PREFIX, "",
				new FinderColumn<>(
					"styleBookTokenCategory.", "styleBookEntryId",
					FinderColumn.Type.LONG, "=", true, true,
					StyleBookTokenCategory::getStyleBookEntryId));

		_uniquePersistenceFinderBySBEI_TFTDI_N = new UniquePersistenceFinder<>(
			this,
			createUniqueFinderPath(
				FINDER_CLASS_NAME_ENTITY, "fetchBySBEI_TFTDI_N",
				new String[] {
					Long.class.getName(), String.class.getName(),
					String.class.getName()
				},
				new String[] {
					"styleBookEntryId", "themeFrontendTokenDefinitionId", "name"
				},
				0, 6, false, StyleBookTokenCategory::getStyleBookEntryId,
				convertNullFunction(
					StyleBookTokenCategory::getThemeFrontendTokenDefinitionId),
				convertNullFunction(StyleBookTokenCategory::getName)),
			_SQL_SELECT_STYLEBOOKTOKENCATEGORY_WHERE, "",
			new FinderColumn<>(
				"styleBookTokenCategory.", "styleBookEntryId",
				FinderColumn.Type.LONG, "=", true, true,
				StyleBookTokenCategory::getStyleBookEntryId),
			new FinderColumn<>(
				"styleBookTokenCategory.", "themeFrontendTokenDefinitionId",
				FinderColumn.Type.STRING, "=", true, true,
				StyleBookTokenCategory::getThemeFrontendTokenDefinitionId),
			new FinderColumn<>(
				"styleBookTokenCategory.", "name", FinderColumn.Type.STRING,
				"=", true, true, StyleBookTokenCategory::getName));

		StyleBookTokenCategoryUtil.setPersistence(this);
	}

	@Deactivate
	public void deactivate() {
		StyleBookTokenCategoryUtil.setPersistence(null);

		entityCache.removeCache(StyleBookTokenCategoryImpl.class.getName());
	}

	@Override
	@Reference(
		target = StyleBookPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = StyleBookPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = StyleBookPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _ENTITY_ALIAS_PREFIX =
		StyleBookTokenCategoryModelImpl.ENTITY_ALIAS + ".";

	private static final String _SQL_SELECT_STYLEBOOKTOKENCATEGORY =
		"SELECT styleBookTokenCategory FROM StyleBookTokenCategory styleBookTokenCategory";

	private static final String _SQL_SELECT_STYLEBOOKTOKENCATEGORY_WHERE =
		"SELECT styleBookTokenCategory FROM StyleBookTokenCategory styleBookTokenCategory WHERE ";

	private static final String _SQL_COUNT_STYLEBOOKTOKENCATEGORY_WHERE =
		"SELECT COUNT(styleBookTokenCategory) FROM StyleBookTokenCategory styleBookTokenCategory WHERE ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No StyleBookTokenCategory exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		StyleBookTokenCategoryPersistenceImpl.class);

	@Override
	protected FinderCache getFinderCache() {
		return finderCache;
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-2079853503