/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	filterItemsByQuery,
	removeFilterOnlyItems,
} from '../../src/main/resources/META-INF/resources/js/useSideNavigationFilter';

describe('Single layer items filtering', () => {
	const singleLayerItems = [
		{id: '1', label: 'Blogs'},
		{id: '2', label: 'Wiki'},
		{id: '3', label: 'Web Content'},
		{id: '4', label: 'Content Templates'},
	];

	it('returns all items when query is empty', () => {
		const result = filterItemsByQuery(singleLayerItems, '');

		expect(result.items).toHaveLength(singleLayerItems.length);
		expect(result.items).toBe(singleLayerItems);
	});

	it('returns matching leaf items', () => {
		const result = filterItemsByQuery(singleLayerItems, 'blo');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].label).toBe('Blogs');
	});

	it('returns empty when nothing matches', () => {
		const result = filterItemsByQuery(singleLayerItems, 'calendar');

		expect(result.items).toHaveLength(0);
	});

	it('is case-sensitive (expects query to be lower case and trimmed)', () => {
		const lowerCaseResult = filterItemsByQuery(singleLayerItems, 'wiki');

		expect(lowerCaseResult.items).toHaveLength(1);
		expect(lowerCaseResult.items[0].label).toBe('Wiki');

		const upperCaseResult = filterItemsByQuery(singleLayerItems, 'WIKI');

		expect(upperCaseResult.items).toHaveLength(0);

		const leadingTrailingSpacesResult = filterItemsByQuery(
			singleLayerItems,
			' wiki '
		);

		expect(leadingTrailingSpacesResult.items).toHaveLength(0);
	});

	it('returns all items when query is a common substring', () => {
		const result = filterItemsByQuery(singleLayerItems, 'content');

		expect(result.items).toHaveLength(2);
	});
});

describe('Multi layer items filtering', () => {
	const multiLayerItems = [
		{
			id: 'parent1',
			items: [
				{id: 'child1', label: 'Blogs'},
				{id: 'child2', label: 'Wiki'},
			],
			label: 'Content',
		},
		{
			id: 'parent2',
			items: [
				{id: 'child3', label: 'Web Content'},
				{id: 'child4', label: 'Content Templates'},
			],
			label: 'Assets',
		},
	];

	it('returns all items when query is empty', () => {
		const result = filterItemsByQuery(multiLayerItems, '');

		expect(result.items).toBe(multiLayerItems);
		expect(result.expandedKeys).toBeUndefined();
	});

	it('returns empty when nothing matches', () => {
		const result = filterItemsByQuery(multiLayerItems, 'calendar');

		expect(result.items).toHaveLength(0);
		expect(result.expandedKeys).toBeDefined();
		expect(result.expandedKeys?.size).toBe(0);
	});

	it('filters nested items and expands parent', () => {
		const result = filterItemsByQuery(multiLayerItems, 'blog');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].id).toBe('parent1');
		expect(result.items[0].items).toHaveLength(1);
		expect(result.items[0].items![0].label).toBe('Blogs');
		expect(result.expandedKeys?.has('parent1')).toBe(true);
		expect(result.expandedKeys?.has('parent2')).toBe(false);
	});

	it('matches the whole parent item if the query matches the parent label', () => {
		const result = filterItemsByQuery(multiLayerItems, 'assets');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].id).toBe('parent2');
		expect(result.items[0].items).toHaveLength(2);
		expect(result.items[0].items![0].label).toBe('Web Content');
		expect(result.items[0].items![1].label).toBe('Content Templates');
		expect(result.expandedKeys?.has('parent2')).toBe(true);
		expect(result.expandedKeys?.has('parent1')).toBe(false);
	});
});

describe('Filter-only items filtering', () => {
	const filterOnlyItems = [
		{
			id: 'users',
			items: [
				{
					href: 'usersAndOrganizationsHref',
					id: 'usersAndOrganizations',
					items: [
						{
							filterOnly: true,
							href: 'usersTabHref',
							id: 'usersTab',
							label: 'Users',
						},
						{
							filterOnly: true,
							href: 'organizationsTabHref',
							id: 'organizationsTab',
							label: 'Organizations',
						},
					],
					label: 'Users and Organizations',
				},
				{href: 'rolesHref', id: 'roles', label: 'Roles'},
			],
			label: 'Users',
		},
	];

	it('hides filter-only items when the query is empty', () => {
		const result = filterItemsByQuery(filterOnlyItems, '');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].items).toHaveLength(2);
		expect(result.items[0].items![0].items).toBeUndefined();
	});

	it('shows a matching filter-only item with its parent', () => {
		const result = filterItemsByQuery(filterOnlyItems, 'organizations');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].id).toBe('users');
		expect(result.items[0].items).toHaveLength(1);
		expect(result.items[0].items![0].id).toBe('usersAndOrganizations');
		expect(result.items[0].items![0].items).toHaveLength(1);
		expect(result.items[0].items![0].items![0].id).toBe('organizationsTab');
		expect(result.expandedKeys?.has('users')).toBe(true);
		expect(result.expandedKeys?.has('usersAndOrganizations')).toBe(true);
	});

	it('keeps filter-only items hidden when only the parent label matches', () => {
		const result = filterItemsByQuery(filterOnlyItems, 'users and');

		expect(result.items).toHaveLength(1);
		expect(result.items[0].id).toBe('users');
		expect(result.items[0].items).toHaveLength(1);
		expect(result.items[0].items![0].id).toBe('usersAndOrganizations');
		expect(result.items[0].items![0].items).toBeUndefined();
	});
});

describe('removeFilterOnlyItems', () => {
	it('returns the same array when there are no filter-only items', () => {
		const items = [
			{
				id: 'parent',
				items: [{href: 'childHref', id: 'child', label: 'Child'}],
				label: 'Parent',
			},
		];

		expect(removeFilterOnlyItems(items)).toBe(items);
	});

	it('removes nested filter-only items', () => {
		const items = [
			{
				id: 'parent',
				items: [
					{
						filterOnly: true,
						href: 'childHref',
						id: 'child',
						label: 'Child',
					},
				],
				label: 'Parent',
			},
		];

		const visibleItems = removeFilterOnlyItems(items);

		expect(visibleItems).toHaveLength(1);
		expect(visibleItems[0].items).toBeUndefined();
	});
});
