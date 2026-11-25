/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayEmptyState from '@clayui/empty-state';
import {ClayVerticalNav} from '@clayui/nav';
import classNames from 'classnames';
import React, {useRef} from 'react';

import UnifiedSidebar from './UnifiedSidebar';
import UnifiedSidebarResultsSkeleton from './UnifiedSidebarResultsSkeleton';
import {UnifiedSidebarItem} from './types';
import {useUnifiedSidebarStuck} from './useUnifiedSidebarStuck';

interface Props<T extends UnifiedSidebarItem> {
	active: string;
	children?: React.ReactNode;
	expandedKeys: Set<React.Key>;
	initialExpandedKeys: Set<React.Key>;
	items: Array<T>;
	label: string;
	loading?: boolean;
	numberOfResults: number;
	onExpandedChange: (expandedKeys: Set<React.Key>) => void;
	renderItem: (item: T) => React.ReactElement;
	stuckAnchorSelector?: string;
}

export default function UnifiedSidebarNavigation<T extends UnifiedSidebarItem>({
	active,
	children,
	expandedKeys,
	initialExpandedKeys,
	items,
	label,
	loading,
	numberOfResults,
	onExpandedChange,
	renderItem,
	stuckAnchorSelector,
}: Props<T>) {
	const scrollRef = useRef<HTMLDivElement>(null);

	const stuck = useUnifiedSidebarStuck(scrollRef, items, stuckAnchorSelector);

	return (
		<div
			className={classNames('unified-sidebar-scroll', {
				'unified-sidebar-scroll-stuck': stuck,
			})}
			ref={scrollRef}
		>
			<UnifiedSidebar.Body className="c-px-0">
				{loading ? (
					<UnifiedSidebarResultsSkeleton />
				) : numberOfResults ? (
					<ClayVerticalNav
						active={active}
						aria-label={label}
						defaultExpandedKeys={initialExpandedKeys}
						displayType="primary"
						expandedKeys={expandedKeys}
						itemAriaCurrent={true}
						items={items}
						onExpandedChange={onExpandedChange}
						stacked={true}
					>
						{(collectionItem) => {
							if (typeof collectionItem === 'string') {
								return <span>{collectionItem}</span>;
							}

							return renderItem(collectionItem as T);
						}}
					</ClayVerticalNav>
				) : (
					<ClayEmptyState
						className="c-mt-n2 c-px-4 text-center"
						description={Liferay.Language.get(
							'adjust-or-clear-the-search-to-view-all-navigation-items'
						)}
						small
						title={Liferay.Language.get('no-matching-items')}
					/>
				)}

				{children}
			</UnifiedSidebar.Body>

			<div className="unified-sidebar-scroll-shadow" />
		</div>
	);
}
