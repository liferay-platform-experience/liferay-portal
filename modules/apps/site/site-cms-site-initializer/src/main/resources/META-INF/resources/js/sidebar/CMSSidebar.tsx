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
	UnifiedSidebarSearchInput,
	countUnifiedSidebarItems,
	useUnifiedSidebarAnnouncement,
	useUnifiedSidebarExpandedKeys,
	useUnifiedSidebarFilter,
	useUnifiedSidebarShortcut,
	useUnifiedSidebarStuck,
} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useMemo, useRef} from 'react';

import SpaceSticker from '../common/components/SpaceSticker';
import {LogoColor} from '../common/types/Space';
import {useCMSSidebarVisibility} from './useCMSSidebarVisibility';

interface CMSSidebarItem extends UnifiedSidebarItem {
	items?: Array<CMSSidebarItem>;
	logoColor?: LogoColor;
}

interface NavigationItem {
	href?: string;
	icon?: string;
	id: string;
	items?: Array<NavigationItem>;
	label: string;
}

type Section =
	| {
			props: {
				active: string;
				expandedKeys: Array<string>;
				navItems: Array<NavigationItem>;
			};
			type: 'navigation';
	  }
	| {
			props: {
				allSpacesActive: boolean;
				allSpacesURL: string;
				assetLibraries: Array<{
					active?: boolean;
					id: number;
					name: string;
					settings?: {
						logoColor?: string;
					};
					url: string;
				}>;
				assetLibrariesCount: number;
			};
			type: 'spaces';
	  };

type Props = {
	elements: Array<HTMLElement>;
	expandedKeys: Array<string> | null;
	expandedKeysSessionKey: string;
	imageURL: string;
	label: string;
	navigationId: string;
	sections: Array<Section>;
	visible: boolean;
};

const SPACES_ITEM_ID = 'spaces';

function toNavigationItems(
	navItems: Array<NavigationItem>,
	prefix: string
): Array<CMSSidebarItem> {
	return navItems.map((navItem) => ({
		href: navItem.href || undefined,
		id: `${prefix}-${navItem.id}`,
		items: navItem.items?.length
			? toNavigationItems(navItem.items, prefix)
			: undefined,
		label: navItem.label,
		leadingIcon: navItem.icon || undefined,
	}));
}

function getNavigation(sections: Array<Section>) {
	let active = '';
	const expandedKeys: Array<string> = [];
	const items: Array<CMSSidebarItem> = [];

	sections.forEach((section, index) => {
		if (section.type === 'spaces') {
			const {
				allSpacesActive,
				allSpacesURL,
				assetLibraries,
				assetLibrariesCount,
			} = section.props;

			assetLibraries.forEach((assetLibrary) => {
				if (assetLibrary.active) {
					active = `space-${assetLibrary.id}`;
				}
			});

			if (allSpacesActive) {
				active = 'all-spaces';
			}

			expandedKeys.push(SPACES_ITEM_ID);

			items.push({
				id: SPACES_ITEM_ID,
				items: [
					...assetLibraries.map((assetLibrary) => ({
						href: assetLibrary.url,
						id: `space-${assetLibrary.id}`,
						label: assetLibrary.name,
						logoColor: assetLibrary.settings
							?.logoColor as LogoColor,
					})),
					{
						href: allSpacesURL,
						id: 'all-spaces',
						label: sub(
							Liferay.Language.get('all-spaces-x'),
							assetLibrariesCount
						),
						leadingIcon: 'box-container',
					},
				],
				label: Liferay.Language.get('spaces'),
			});

			return;
		}

		const prefix = String(index);

		const {
			active: navigationActive,
			expandedKeys: navigationExpandedKeys,
			navItems,
		} = section.props;

		if (navigationActive) {
			active = `${prefix}-${navigationActive}`;
		}

		expandedKeys.push(
			...navigationExpandedKeys.map((key) => `${prefix}-${key}`)
		);

		items.push(...toNavigationItems(navItems, prefix));
	});

	return {active, expandedKeys, items};
}

export default function CMSSidebar({
	elements,
	expandedKeys: externalExpandedKeys,
	expandedKeysSessionKey,
	imageURL,
	label,
	navigationId,
	sections,
	visible: initialVisible,
}: Props) {
	const {overlay, updateVisible, visible} =
		useCMSSidebarVisibility(initialVisible);

	const {announceNextChange, announcement} =
		useUnifiedSidebarAnnouncement(visible);

	useUnifiedSidebarShortcut(navigationId, () => {
		announceNextChange();

		updateVisible(!visible);
	});

	const navigation = useMemo(() => getNavigation(sections), [sections]);

	const {
		expandedKeys: filterExpandedKeys,
		isFilterActive,
		items,
		numberOfMatches,
		setQuery,
	} = useUnifiedSidebarFilter(navigation.items);

	const {expandedKeys, initialExpandedKeys, updateExpandedKeys} =
		useUnifiedSidebarExpandedKeys({
			expandedKeys: externalExpandedKeys ?? navigation.expandedKeys,
			expandedKeysSessionKey,
			filterExpandedKeys,
			isFilterActive,
		});

	const numberOfResults = isFilterActive
		? numberOfMatches
		: countUnifiedSidebarItems(items);

	const scrollRef = useRef<HTMLDivElement>(null);

	const stuck = useUnifiedSidebarStuck(scrollRef, items);

	const elementsRef = useRef<HTMLDivElement>(null);

	useEffect(
		function moveElements() {

			// Elements like the banner render on the server and may mount
			// their own components, so they are moved in instead of being
			// rendered again.

			elementsRef.current?.append(...elements);
		},
		[elements]
	);

	return (
		<>
			<UnifiedSidebar
				backdrop={overlay}
				backdropContainerProps={{className: 'cadmin'}}
				categoryImageUrl={imageURL}
				label={label}
				navigationId={navigationId}
				onOpenChange={updateVisible}
				open={visible}
				testId="cmsSidebar"
			>
				<UnifiedSidebar.Top>
					<UnifiedSidebarSearchInput onChange={setQuery} />

					<SearchResultsMessage
						numberOfResults={numberOfResults}
						resultType={Liferay.Language.get('navigation-items')}
					/>
				</UnifiedSidebar.Top>

				<div
					className={classNames('unified-sidebar-scroll', {
						'unified-sidebar-scroll-stuck': stuck,
					})}
					ref={scrollRef}
				>
					<UnifiedSidebar.Body className="c-px-0">
						{numberOfResults ? (
							<ClayVerticalNav
								active={navigation.active}
								aria-label={label}
								defaultExpandedKeys={initialExpandedKeys}
								displayType="primary"
								expandedKeys={expandedKeys}
								itemAriaCurrent={true}
								items={items}
								onExpandedChange={updateExpandedKeys}
								stacked={true}
							>
								{(collectionItem) => {
									if (typeof collectionItem === 'string') {
										return <span>{collectionItem}</span>;
									}

									const item =
										collectionItem as CMSSidebarItem;

									return (
										<ClayVerticalNav.Item
											href={item.href}
											items={item.items}
											key={item.id}
											textValue={item.label}
										>
											{item.logoColor ? (
												<SpaceSticker
													aria-hidden="true"
													displayType={item.logoColor}
													name={item.label}
													size="sm"
												/>
											) : (
												<UnifiedSidebarItemContent
													item={item}
												/>
											)}
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

						<div hidden={isFilterActive} ref={elementsRef} />
					</UnifiedSidebar.Body>

					<div className="unified-sidebar-scroll-shadow" />
				</div>
			</UnifiedSidebar>

			<span aria-live="polite" className="sr-only">
				{announcement}
			</span>
		</>
	);
}
