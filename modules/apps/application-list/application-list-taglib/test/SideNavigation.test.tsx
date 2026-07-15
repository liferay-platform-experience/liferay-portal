/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {configure} from '@testing-library/dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {SideNavigation} from '../src/main/resources/META-INF/resources/js';

configure({
	testIdAttribute: 'data-qa-id',
});

const ITEMS = [
	{
		id: 'content',
		items: [
			{
				canonicalName: 'assetsCanonicalName',
				href: 'assetsHref',
				id: 'assets',
				items: [
					{
						canonicalName: 'categoriesCanonicalName',
						filterOnly: true,
						href: 'categoriesHref',
						id: 'categories',
						label: 'Categories',
					},
				],
				label: 'Assets',
				leadingIcon: 'assetsIcon',
			},
			{
				canonicalName: 'dashboardCanonicalName',
				href: 'dashboardHref',
				id: 'dashboard',
				label: 'Dashboard',
				leadingIcon: 'dashboardIcon',
			},
		],
		label: 'Content',
	},
	{
		id: 'workflow',
		items: [
			{
				canonicalName: 'metricsCanonicalName',
				href: 'metricsHref',
				id: 'metrics',
				label: 'Metrics',
				leadingIcon: 'metricsIcon',
			},
		],
		label: 'Workflow',
	},
];

const renderComponent = ({expandedKeys = ['content', 'workflow']} = {}) =>
	render(
		<SideNavigation
			canonicalName="sideNavigationCanonicalName"
			categoryImageUrl="categoryImageUrl"
			colorScheme="light"
			colorSchemeSessionKey="colorSchemeSessionKey"
			expandedKeys={expandedKeys}
			expandedKeysSessionKey="expandedKeysSessionKey"
			items={ITEMS}
			label="Applications"
			selectedPortletId="assets"
			siteAdministrationItemSelectedEventName="siteAdministrationItemSelectedEventName"
			siteAdministrationItemSelectorUrl="siteAdministrationItemSelectorUrl"
			visible
			visibleSessionKey="visibleSessionKey"
		/>
	);

describe('SideNavigation', () => {
	it('renders the side navigation with canonical name', () => {
		const {getByRole, getByTestId} = renderComponent();

		const sideNavigation = getByTestId('sideNavigation');

		expect(sideNavigation).toBeInTheDocument();
		expect(sideNavigation).toHaveAttribute(
			'data-canonical-name',
			'sideNavigationCanonicalName'
		);

		const assetsItem = getByRole('menuitem', {name: 'Assets'});

		expect(assetsItem.parentElement).toBeInTheDocument();
		expect(assetsItem.parentElement).toHaveAttribute(
			'data-canonical-name',
			'assetsCanonicalName'
		);
	});

	it('renders the side navigation header', () => {
		renderComponent();

		const title = screen.getByText('Applications');

		expect(title).toBeInTheDocument();

		const icon = screen.getByTestId('sideNavigationProductIcon');

		expect(icon).toHaveAttribute('src', 'categoryImageUrl');
	});

	it('renders each navigation item', () => {
		renderComponent();

		const menuItems = screen.getAllByRole('menuitem');

		expect(menuItems).toHaveLength(5);

		['Content', 'Workflow'].forEach((label) => {
			expect(screen.getByText(label)).toHaveAttribute(
				'aria-expanded',
				'true'
			);
		});

		['Assets', 'Dashboard', 'Metrics'].forEach((label) => {
			expect(screen.getByText(label)).toHaveAttribute(
				'href',
				`${label.toLowerCase()}Href`
			);
		});

		expect(screen.getByText('Assets')).toHaveClass('active');
		expect(screen.getByText('Workflow')).not.toHaveClass('active');
		expect(screen.getByText('Metrics')).not.toHaveClass('active');
	});

	it('shows only the navigation items from the expanded keys', () => {
		renderComponent({expandedKeys: ['workflow']});

		const menuItems = screen.getAllByRole('menuitem');

		expect(menuItems).toHaveLength(3);

		expect(screen.getByText('Content')).toHaveAttribute(
			'aria-expanded',
			'false'
		);

		expect(screen.getByText('Workflow')).toHaveAttribute(
			'aria-expanded',
			'true'
		);
	});

	it('hides filter-only items until the search matches them', async () => {
		renderComponent();

		expect(screen.queryByText('Categories')).not.toBeInTheDocument();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'categories'
		);

		const categoriesItem = await screen.findByText('Categories');

		expect(categoriesItem).toHaveAttribute('href', 'categoriesHref');
		expect(screen.getByText('Assets')).toBeInTheDocument();
		expect(screen.queryByText('Dashboard')).not.toBeInTheDocument();
	});

	it('keeps filter-only items hidden when only their parent matches', async () => {
		renderComponent();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'assets'
		);

		await waitFor(() =>
			expect(screen.queryByText('Dashboard')).not.toBeInTheDocument()
		);

		expect(screen.getByText('Assets')).toBeInTheDocument();
		expect(screen.queryByText('Categories')).not.toBeInTheDocument();
	});
});
