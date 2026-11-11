/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import {ClayVerticalNav} from '@clayui/nav';
import {SearchResultsMessage} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {
	UnifiedSidebar,
	UnifiedSidebarItem,
	UnifiedSidebarItemContent,
	UnifiedSidebarResultsSkeleton,
	UnifiedSidebarSearchInput,
	UnifiedSidebarSiteSelector,
	useUnifiedSidebarFilter,
	useUnifiedSidebarItems,
	useUnifiedSidebarStuck,
} from 'frontend-js-components-web';
import React, {useCallback, useMemo, useRef, useState} from 'react';

import ProductMenuSidebarPageTree from './ProductMenuSidebarPageTree';
import ProductMenuSidebarStaging, {
	ProductMenuSidebarStagingProps,
} from './ProductMenuSidebarStaging';
import {useProductMenuSidebarExpandedKeys} from './useProductMenuSidebarExpandedKeys';
import {useProductMenuSidebarVisibility} from './useProductMenuSidebarVisibility';

const NO_ITEMS: Array<ProductMenuSidebarItem> = [];

const PAGE_TREE_ITEM_ID = 'pageTree';

type ProductMenuSidebarItem = UnifiedSidebarItem & {
	disabled?: boolean;
	pageTree?: boolean;
};

interface Props {
	canonicalName: string;
	expandedKeys: Array<React.Key>;
	expandedKeysSessionKey: string;
	imageURL: string;
	items: Array<ProductMenuSidebarItem>;
	label: string;
	navigationItemsURL: string;
	pageTree?: {
		stateSessionKey: string;
		url: string;
		visible: boolean;
	};
	selectedPortletId: string;
	siteItemSelectedEventName: string;
	siteItemSelectorURL: string;
	staging?: ProductMenuSidebarStagingProps;
	visible: boolean;
	visibleSessionKey: string;
}

function countNavigableItems(
	navigationItems: Array<UnifiedSidebarItem>
): number {
	return navigationItems.reduce(
		(count, navigationItem) =>
			count +
			(navigationItem.href ? 1 : 0) +
			(navigationItem.items
				? countNavigableItems(navigationItem.items)
				: 0),
		0
	);
}

function ProductMenuSidebar({
	canonicalName,
	expandedKeys: externalExpandedKeys,
	expandedKeysSessionKey,
	imageURL,
	items: externalItems,
	label,
	navigationItemsURL,
	pageTree,
	selectedPortletId,
	siteItemSelectedEventName,
	siteItemSelectorURL,
	staging,
	visible: initialVisible,
	visibleSessionKey,
}: Props) {
	const containerRef = useRef<HTMLElement | null>(
		document.getElementById(
			'com_liferay_product_navigation_product_menu_web_sidebar_container'
		)
	);

	const {overlay, updateVisible, visible} = useProductMenuSidebarVisibility(
		initialVisible,
		visibleSessionKey
	);

	const {
		items: navigationItems,
		loading,
		prefetchFilterOnlyItems,
	} = useUnifiedSidebarItems(externalItems, navigationItemsURL);

	const {
		expandedKeys: filterExpandedKeys,
		isFilterActive,
		items,
		numberOfMatches,
		setQuery,
	} = useUnifiedSidebarFilter(navigationItems);

	const {expandedKeys, initialExpandedKeys, updateExpandedKeys} =
		useProductMenuSidebarExpandedKeys({
			expandedKeys: externalExpandedKeys,
			expandedKeysSessionKey,
			filterExpandedKeys,
			isFilterActive,
		});

	const onExpandedChange = useCallback(
		(updatedExpandedKeys: Set<React.Key>) => {

			// The Page Tree item opens a view instead of a group, so it must
			// not persist an expanded key that would race the Page Tree state.

			if (updatedExpandedKeys.has(PAGE_TREE_ITEM_ID)) {
				return;
			}

			updateExpandedKeys(updatedExpandedKeys);
		},
		[updateExpandedKeys]
	);

	const [pageTreeVisible, setPageTreeVisible] = useState(
		Boolean(pageTree?.visible)
	);

	const updatePageTreeVisible = useCallback(
		(pageTreeVisible: boolean) => {
			if (!pageTree) {
				return;
			}

			setPageTreeVisible(pageTreeVisible);
			setQuery('');

			Liferay.Util.Session.set(
				pageTree.stateSessionKey,
				pageTreeVisible
					? String(Liferay.ThemeDisplay.getScopeGroupId())
					: ''
			).catch((error: Error) => {

				// eslint-disable-next-line no-console
				console.error(error);
			});
		},
		[pageTree, setQuery]
	);

	const showPageTree = pageTree && pageTreeVisible;

	const numberOfResults = useMemo(
		() => (isFilterActive ? numberOfMatches : countNavigableItems(items)),
		[isFilterActive, items, numberOfMatches]
	);

	// Keep already matched items on screen while the filter-only items are
	// still loading, so a slow request cannot blank a complete result set.

	const showResultsSkeleton = isFilterActive && loading && !numberOfResults;

	const scrollRef = useRef<HTMLDivElement>(null);

	// The list unmounts while the Page Tree shows, so swap the items to
	// observe the remounted list once the Page Tree closes.

	const stuck = useUnifiedSidebarStuck(
		scrollRef,
		showPageTree ? NO_ITEMS : items
	);

	return (
		<UnifiedSidebar
			backdrop={overlay}
			backdropContainerProps={{className: 'cadmin'}}
			canonicalName={canonicalName}
			categoryImageUrl={imageURL}
			containerRef={containerRef}
			headerActions={
				showPageTree ? undefined : (
					<UnifiedSidebarSiteSelector
						eventName={siteItemSelectedEventName}
						url={siteItemSelectorURL}
					/>
				)
			}
			label={showPageTree ? Liferay.Language.get('page-tree') : label}
			navigationId="com_liferay_product_navigation_product_menu_web_sidebar"
			onBack={
				showPageTree ? () => updatePageTreeVisible(false) : undefined
			}
			onOpenChange={updateVisible}
			open={visible}
			testId="sideNavigation"
		>
			{showPageTree ? (
				<ProductMenuSidebarPageTree url={pageTree.url} />
			) : (
				<>
					<UnifiedSidebar.Top>
						{staging && <ProductMenuSidebarStaging {...staging} />}

						<UnifiedSidebarSearchInput
							onChange={setQuery}
							onFocus={prefetchFilterOnlyItems}
						/>

						<SearchResultsMessage
							numberOfResults={
								showResultsSkeleton ? null : numberOfResults
							}
							resultType={Liferay.Language.get(
								'navigation-items'
							)}
						/>
					</UnifiedSidebar.Top>

					<div
						className={classNames('unified-sidebar-scroll', {
							'unified-sidebar-scroll-stuck': stuck,
						})}
						ref={scrollRef}
					>
						<UnifiedSidebar.Body className="c-px-0">
							{showResultsSkeleton ? (
								<UnifiedSidebarResultsSkeleton />
							) : numberOfResults ? (
								<ClayVerticalNav
									active={selectedPortletId}
									defaultExpandedKeys={initialExpandedKeys}
									displayType="primary"
									expandedKeys={expandedKeys}
									itemAriaCurrent={true}
									items={items}
									onExpandedChange={onExpandedChange}
									stacked={true}
								>
									{(collectionItem) => {
										if (
											typeof collectionItem === 'string'
										) {
											return (
												<span>{collectionItem}</span>
											);
										}

										const item =
											collectionItem as ProductMenuSidebarItem;

										if (item.disabled) {
											return (
												<li
													className="nav-item"
													key={item.id}
													role="none"
												>
													<button
														className="btn btn-unstyled disabled nav-link"
														data-canonical-name={
															item.canonicalName
														}
														disabled
														role="menuitem"
														type="button"
													>
														<UnifiedSidebarItemContent
															item={item}
														/>
													</button>
												</li>
											);
										}

										return (
											<ClayVerticalNav.Item
												className={classNames({
													'unified-sidebar-section-item':
														item.parentLabel,
												})}
												data-canonical-name={
													item.canonicalName
												}
												href={item.href}
												items={item.items}
												key={item.id}
												onClick={
													item.pageTree
														? () =>
																updatePageTreeVisible(
																	true
																)
														: undefined
												}
												textValue={item.label}
											>
												<UnifiedSidebarItemContent
													item={item}
												/>
											</ClayVerticalNav.Item>
										);
									}}
								</ClayVerticalNav>
							) : (
								<ClayEmptyState
									className="c-mt-n2 c-px-4 text-center"
									description={Liferay.Language.get(
										'adjust-or-clear-the-search-to-view-all-navigation-items'
									)}
									small
									title={Liferay.Language.get(
										'no-matching-items'
									)}
								/>
							)}
						</UnifiedSidebar.Body>

						<div className="unified-sidebar-scroll-shadow" />
					</div>
				</>
			)}
		</UnifiedSidebar>
	);
}

export default ProductMenuSidebar;
