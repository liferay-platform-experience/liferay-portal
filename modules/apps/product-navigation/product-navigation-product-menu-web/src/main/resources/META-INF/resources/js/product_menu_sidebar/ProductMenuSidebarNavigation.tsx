/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayVerticalNav} from '@clayui/nav';
import {SearchResultsMessage} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {
	UnifiedSidebar,
	UnifiedSidebarItem,
	UnifiedSidebarItemContent,
	UnifiedSidebarNavigation,
	UnifiedSidebarSearchInput,
	countUnifiedSidebarItems,
} from 'frontend-js-components-web';
import React, {useMemo} from 'react';

import ProductMenuSidebarStaging, {
	ProductMenuSidebarStagingProps,
} from './ProductMenuSidebarStaging';

export type ProductMenuSidebarItem = UnifiedSidebarItem & {
	disabled?: boolean;
	pageTree?: boolean;
};

interface Props {
	expandedKeys: Set<React.Key>;
	initialExpandedKeys: Set<React.Key>;
	isFilterActive: boolean;
	items: Array<ProductMenuSidebarItem>;
	label: string;
	loading: boolean;
	numberOfMatches: number;
	onExpandedChange: (expandedKeys: Set<React.Key>) => void;
	onPageTreeOpen: () => void;
	onQueryChange: (query: string) => void;
	onSearchFocus: () => void;
	selectedPortletId: string;
	staging?: ProductMenuSidebarStagingProps;
}

function ProductMenuSidebarNavigation({
	expandedKeys,
	initialExpandedKeys,
	isFilterActive,
	items,
	label,
	loading,
	numberOfMatches,
	onExpandedChange,
	onPageTreeOpen,
	onQueryChange,
	onSearchFocus,
	selectedPortletId,
	staging,
}: Props) {
	const numberOfResults = useMemo(
		() =>
			isFilterActive ? numberOfMatches : countUnifiedSidebarItems(items),
		[isFilterActive, items, numberOfMatches]
	);

	// Keep already matched items on screen while the filter-only items are
	// still loading, so a slow request cannot blank a complete result set.

	const showResultsSkeleton = isFilterActive && loading && !numberOfResults;

	return (
		<>
			<UnifiedSidebar.Top>
				{staging && <ProductMenuSidebarStaging {...staging} />}

				<UnifiedSidebarSearchInput
					onChange={onQueryChange}
					onFocus={onSearchFocus}
				/>

				<SearchResultsMessage
					numberOfResults={
						showResultsSkeleton ? null : numberOfResults
					}
					resultType={Liferay.Language.get('navigation-items')}
				/>
			</UnifiedSidebar.Top>

			<UnifiedSidebarNavigation
				active={selectedPortletId}
				expandedKeys={expandedKeys}
				initialExpandedKeys={initialExpandedKeys}
				items={items}
				label={label}
				loading={showResultsSkeleton}
				numberOfResults={numberOfResults}
				onExpandedChange={onExpandedChange}
				renderItem={(item) => {
					if (item.disabled) {
						return (
							<li className="nav-item" key={item.id} role="none">
								<button
									className="btn btn-unstyled disabled nav-link"
									data-canonical-name={item.canonicalName}
									disabled
									role="menuitem"
									type="button"
								>
									<UnifiedSidebarItemContent item={item} />
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
							data-canonical-name={item.canonicalName}
							href={item.href}
							items={item.items}
							key={item.id}
							onClick={item.pageTree ? onPageTreeOpen : undefined}
							textValue={item.label}
						>
							<UnifiedSidebarItemContent item={item} />
						</ClayVerticalNav.Item>
					);
				}}
			/>
		</>
	);
}

export default ProductMenuSidebarNavigation;
