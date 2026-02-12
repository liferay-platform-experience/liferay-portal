/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {SidePanel} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {ClayVerticalNav} from '@clayui/nav';
import React, {useCallback, useEffect, useMemo, useRef, useState} from 'react';

import SideNavigationSearchInput from './SideNavigationSearchInput';

interface SideNavigationItem {
	href?: string;
	id: string;
	items?: Array<SideNavigationItem>;
	label: string;
	leadingIcon?: string;
}

interface SideNavigationFilter {
	expandedKeys?: Set<React.Key>;
	items: Array<SideNavigationItem>;
}

interface Props {
	expandedKeys: Array<React.Key>;
	expandedKeysSessionKey: string;
	items: Array<SideNavigationItem>;
	label: string;
	portletId: string;
	visible: boolean;
	visibleSessionKey: string;
}

const EMPTY_KEYS_SET = new Set<React.Key>();
const EMPTY_FILTER = {expandedKeys: EMPTY_KEYS_SET, items: []};

export function filterItemsByQuery(
	items: Array<SideNavigationItem>,
	query: string
): SideNavigationFilter {
	if (!query) {
		return {items};
	}

	return items.reduce<Required<SideNavigationFilter>>((result, item) => {
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
		}
		else if (item.label.toLowerCase().includes(query)) {
			return {
				expandedKeys: result.expandedKeys,
				items: result.items.concat(item),
			};
		}

		return result;
	}, EMPTY_FILTER);
}

function useSideNavigationFilter(items: Array<SideNavigationItem>) {
	const [query, setQuery] = useState('');

	const filter = useMemo(
		() => filterItemsByQuery(items, query),
		[items, query]
	);

	const updateQuery = useCallback((query: string) => {
		setQuery(query.trim().toLowerCase());
	}, []);

	return {
		expandedKeys: filter.expandedKeys,
		isFilterActive: !!query,
		items: filter.items,
		setQuery: updateQuery,
	};
}

function SideNavigation({
	expandedKeys: externalExpandedKeys,
	expandedKeysSessionKey,
	items: externalItems,
	label,
	portletId,
	visible: initialVisible,
	visibleSessionKey,
}: Props) {
	const containerRef = useRef<HTMLElement | null>(
		document.getElementById(
			'com_liferay_application_list_taglib_side_navigation_container'
		)
	);

	const [initialExpandedKeys] = useState<Set<React.Key>>(
		() => new Set(externalExpandedKeys)
	);

	const [userExpandedKeys, setUserExpandedKeys] =
		useState<Set<React.Key>>(initialExpandedKeys);

	const [visible, setVisible] = useState(initialVisible);

	const {expandedKeys, isFilterActive, items, setQuery} =
		useSideNavigationFilter(externalItems);

	const updateExpandedKeys = useCallback(
		async (updatedExpandedKeys: Set<React.Key>) => {
			if (isFilterActive) {
				return;
			}

			await Liferay.Util.Session.set(
				expandedKeysSessionKey,
				Array.from(updatedExpandedKeys).join(',')
			);

			setUserExpandedKeys(updatedExpandedKeys);
		},
		[expandedKeysSessionKey, isFilterActive]
	);

	const updateVisible = useCallback(
		async (visible: boolean) => {
			await Liferay.Util.Session.set(
				visibleSessionKey,
				visible ? 'visible' : 'hidden'
			);

			setVisible(visible);

			Liferay.fire('sideNavigationStateChanged', {visible});
		},
		[visibleSessionKey]
	);

	useEffect(() => {
		async function handleStateRequest({visible}: {visible: boolean}) {
			await updateVisible(visible);
		}

		Liferay.on('sideNavigationStateRequested', handleStateRequest);

		return () =>
			Liferay.detach('sideNavigationStateRequested', handleStateRequest);
	}, [updateVisible]);

	return (
		<SidePanel
			as="section"
			containerRef={containerRef}
			data-qa-id="sideNavigation"
			defaultOpen={initialVisible}
			direction="left"
			id="com_liferay_application_list_taglib_side_navigation"
			onOpenChange={updateVisible}
			open={visible}
			panelWidth={320}
			position="fixed"
		>
			<SidePanel.Header data-qa-id="sideNavigationHeader">
				<SidePanel.Title>
					<ClayIcon symbol="grid" />

					<span className="c-px-2" data-qa-id="sideNavigationLabel">
						{label}
					</span>
				</SidePanel.Title>
			</SidePanel.Header>

			<SidePanel.Body className="c-px-0">
				<div className="c-px-4">
					<SideNavigationSearchInput onChange={setQuery} />
				</div>

				<ClayVerticalNav
					active={portletId}
					defaultExpandedKeys={initialExpandedKeys}
					displayType="primary"
					expandedKeys={expandedKeys ?? userExpandedKeys}
					itemAriaCurrent={true}
					items={items}
					onExpandedChange={updateExpandedKeys}
				>
					{(item) => {
						if (typeof item === 'string') {
							return <span>{item}</span>;
						}

						return (
							<ClayVerticalNav.Item
								href={item.href}
								items={item.items}
								key={item.id}
								textValue={item.label}
							>
								{item.leadingIcon && (
									<ClayIcon
										className="c-mr-2"
										key={item.leadingIcon}
										symbol={item.leadingIcon}
									/>
								)}

								{item.label}
							</ClayVerticalNav.Item>
						);
					}}
				</ClayVerticalNav>
			</SidePanel.Body>
		</SidePanel>
	);
}

export default SideNavigation;
