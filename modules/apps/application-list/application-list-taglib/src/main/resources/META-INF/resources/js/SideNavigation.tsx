/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayVerticalNav} from '@clayui/nav';
import {SearchResultsMessage} from '@liferay/layout-js-components-web';
import classNames from 'classnames';
import {
	UnifiedSidebar,
	UnifiedSidebarItemContent,
	UnifiedSidebarNavigation,
	UnifiedSidebarSearchInput,
	UnifiedSidebarSiteSelector,
	countUnifiedSidebarItems,
	useId,
	useUnifiedSidebarExpandedKeys,
	useUnifiedSidebarFilter,
	useUnifiedSidebarItems,
	useUnifiedSidebarVisibility,
} from 'frontend-js-components-web';
import React, {useMemo, useRef} from 'react';

import SideNavigationScopeItem from './SideNavigationScopeItem';
import {
	filterSideNavigationItems,
	isScopeMarker,
} from './filterSideNavigationItems';
import {SideNavigationItem} from './types/SideNavigation';

interface Props {
	canonicalName: string;
	categoryImageUrl: string;
	colorScheme: 'dark' | 'light';
	colorSchemeSessionKey: string;
	expandedKeys: Array<React.Key>;
	expandedKeysSessionKey: string;
	items: Array<SideNavigationItem>;
	label: string;
	navigationItemsURL: string;
	selectedPortletId: string;
	sidebarStateSessionKey: string;
	siteAdministrationItemSelectedEventName: string;
	siteAdministrationItemSelectorUrl: string;
	visible: boolean;
}

function SideNavigation({
	canonicalName,
	categoryImageUrl,
	colorScheme,
	colorSchemeSessionKey,
	expandedKeys: externalExpandedKeys,
	expandedKeysSessionKey,
	items: externalItems,
	label,
	navigationItemsURL,
	selectedPortletId,
	sidebarStateSessionKey,
	siteAdministrationItemSelectedEventName,
	siteAdministrationItemSelectorUrl,
	visible: initialVisible,
}: Props) {
	const containerRef = useRef<HTMLElement | null>(
		document.getElementById(
			'com_liferay_application_list_taglib_side_navigation_container'
		)
	);

	const baseId = useId();

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
	} = useUnifiedSidebarFilter(navigationItems, filterSideNavigationItems);

	const {expandedKeys, initialExpandedKeys, updateExpandedKeys} =
		useUnifiedSidebarExpandedKeys({
			expandedKeys: externalExpandedKeys,
			expandedKeysSessionKey,
			filterExpandedKeys,
			isFilterActive,
		});

	const {updateVisible, visible} = useUnifiedSidebarVisibility({
		initialVisible,
		sessionKey: sidebarStateSessionKey,
		stateChangedEventName: 'sideNavigationStateChanged',
		stateRequestedEventName: 'sideNavigationStateRequested',
	});

	const numberOfResults = useMemo(
		() =>
			isFilterActive ? numberOfMatches : countUnifiedSidebarItems(items),
		[isFilterActive, items, numberOfMatches]
	);

	// Keep already matched items on screen while the filter-only items are
	// still loading, so a slow request cannot blank a complete result set.

	const showResultsSkeleton = isFilterActive && loading && !numberOfResults;

	return (
		<UnifiedSidebar
			canonicalName={canonicalName}
			categoryImageUrl={categoryImageUrl}
			colorScheme={colorScheme}
			colorSchemeSessionKey={colorSchemeSessionKey}
			containerRef={containerRef}
			headerActions={
				siteAdministrationItemSelectedEventName &&
				siteAdministrationItemSelectorUrl ? (
					<UnifiedSidebarSiteSelector
						eventName={siteAdministrationItemSelectedEventName}
						url={siteAdministrationItemSelectorUrl}
					/>
				) : undefined
			}
			label={label}
			navigationId="com_liferay_application_list_taglib_side_navigation"
			onOpenChange={updateVisible}
			open={visible}
			testId="sideNavigation"
		>
			<UnifiedSidebar.Top>
				<UnifiedSidebarSearchInput
					onChange={setQuery}
					onFocus={prefetchFilterOnlyItems}
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
				onExpandedChange={updateExpandedKeys}
				renderItem={(item) => {
					const scopeItemId = `${baseId}-${item.scope}`;

					if (isScopeMarker(item)) {
						return (
							<SideNavigationScopeItem
								id={scopeItemId}
								key={item.id}
								label={item.label}
								scope={item.scope}
							/>
						);
					}

					return (
						<ClayVerticalNav.Item
							aria-describedby={
								item.scope ? scopeItemId : undefined
							}
							className={classNames({
								'side-navigation-scope-zone': item.scope,
								[`side-navigation-scope-zone-${item.scope}`]:
									item.scope,
								'unified-sidebar-section-item':
									item.parentLabel,
							})}
							data-canonical-name={item.canonicalName}
							href={item.href}
							items={item.items}
							key={item.id}
							textValue={item.label}
						>
							<UnifiedSidebarItemContent item={item} />
						</ClayVerticalNav.Item>
					);
				}}
				stuckAnchorSelector=".side-navigation-scope-item"
			/>
		</UnifiedSidebar>
	);
}

export default SideNavigation;
