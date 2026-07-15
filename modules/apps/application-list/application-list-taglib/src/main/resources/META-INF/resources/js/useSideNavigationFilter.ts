/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo, useState} from 'react';

import {SideNavigationItem} from './types/SideNavigation';

interface SideNavigationFilter {
	expandedKeys?: Set<React.Key>;
	items: Array<SideNavigationItem>;
}

const EMPTY_KEYS_SET = new Set<React.Key>();
const EMPTY_FILTER = {expandedKeys: EMPTY_KEYS_SET, items: []};

export function removeFilterOnlyItems(
	items: Array<SideNavigationItem>
): Array<SideNavigationItem> {
	let changed = false;

	const visibleItems: Array<SideNavigationItem> = [];

	for (const item of items) {
		if (item.filterOnly) {
			changed = true;

			continue;
		}

		if (item.items && item.items.length) {
			const childItems = removeFilterOnlyItems(item.items);

			if (childItems !== item.items) {
				changed = true;

				visibleItems.push({
					...item,
					items: childItems.length ? childItems : undefined,
				});

				continue;
			}
		}

		visibleItems.push(item);
	}

	return changed ? visibleItems : items;
}

export function filterItemsByQuery(
	items: Array<SideNavigationItem>,
	query: string
): SideNavigationFilter {
	if (!query) {
		return {items: removeFilterOnlyItems(items)};
	}

	return items.reduce<Required<SideNavigationFilter>>((result, item) => {
		const labelMatches = item.label.toLowerCase().includes(query);

		if (item.items && item.items.length) {
			const {expandedKeys, items} = filterItemsByQuery(item.items, query);

			if (items.length) {
				return {
					expandedKeys: new Set([
						...result.expandedKeys,
						...(expandedKeys ?? EMPTY_KEYS_SET),
						item.id,
					]),

					items: result.items.concat({
						...item,
						items,
					}),
				};
			}

			if (labelMatches) {
				const visibleChildItems = removeFilterOnlyItems(item.items);

				return {
					expandedKeys: new Set([...result.expandedKeys, item.id]),
					items: result.items.concat({
						...item,
						items: visibleChildItems.length
							? visibleChildItems
							: undefined,
					}),
				};
			}
		}
		else if (labelMatches) {
			return {
				expandedKeys: result.expandedKeys,
				items: result.items.concat(item),
			};
		}

		return result;
	}, EMPTY_FILTER);
}

export function useSideNavigationFilter(items: Array<SideNavigationItem>) {
	const [query, setQuery] = useState('');

	const filter = useMemo(
		() => filterItemsByQuery(items, query),
		[items, query]
	);

	return {
		expandedKeys: filter.expandedKeys,
		isFilterActive: !!query,
		items: filter.items,
		setQuery: (query: string) => setQuery(query.trim().toLowerCase()),
	};
}
