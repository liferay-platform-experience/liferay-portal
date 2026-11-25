/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import CMSSidebar from '../../../src/main/resources/META-INF/resources/js/sidebar/CMSSidebar';

const SECTIONS: React.ComponentProps<typeof CMSSidebar>['sections'] = [
	{
		props: {
			active: '2',
			expandedKeys: ['1'],
			navItems: [
				{
					id: '1',
					items: [
						{
							href: '/web/cms/all',
							icon: 'folder',
							id: '2',
							label: 'All',
						},
						{
							href: '/web/cms/files',
							icon: 'documents-and-media',
							id: '3',
							label: 'Files',
						},
					],
					label: 'Assets',
				},
			],
		},
		type: 'navigation',
	},
	{
		props: {
			allSpacesActive: false,
			allSpacesURL: '/web/cms/all-spaces',
			assetLibraries: [
				{
					id: 4,
					name: 'Marketing',
					settings: {logoColor: 'outline-0'},
					url: '/web/cms/spaces/4',
				},
			],
			assetLibrariesCount: 1,
		},
		type: 'spaces',
	},
];

function renderComponent(
	props: Partial<React.ComponentProps<typeof CMSSidebar>> = {}
) {
	return render(
		<CMSSidebar
			elements={[]}
			expandedKeys={null}
			expandedKeysSessionKey="expandedKeysSessionKey"
			imageURL="imageURL"
			label="Liferay CMS"
			navigationId="navigationId"
			sections={SECTIONS}
			triggerElement={null}
			visible
			{...props}
		/>
	);
}

describe('CMSSidebar', () => {
	const util = Liferay.Util;

	let overlay = false;

	beforeEach(() => {
		overlay = false;

		(window as any).IntersectionObserver = class {
			disconnect() {}

			observe() {}

			unobserve() {}
		};

		(window as any).matchMedia = (query: string) => ({
			addEventListener: () => {},
			matches: overlay,
			media: query,
			removeEventListener: () => {},
		});

		document.body.classList.add('cms-sidebar-open');

		jest.spyOn(Liferay, 'fire');
		jest.spyOn(Liferay, 'on');

		Liferay.Util = {
			...Liferay.Util,
			Session: {
				get: jest.fn(),
				set: jest.fn(() => Promise.resolve()),
			},
		};
	});

	afterEach(() => {
		delete (window as any).IntersectionObserver;
		delete (window as any).matchMedia;

		document.body.classList.remove(
			'cms-sidebar-open',
			'sidenav-transition'
		);

		jest.restoreAllMocks();

		Liferay.Util = util;
	});

	it('renders the navigation with the active item', () => {
		renderComponent();

		const navigation = screen.getByRole('navigation', {
			name: 'Liferay CMS',
		});

		expect(within(navigation).getByText('Assets')).toHaveAttribute(
			'aria-expanded',
			'true'
		);
		expect(within(navigation).getByText('All')).toHaveClass('active');
		expect(within(navigation).getByText('Files')).toHaveAttribute(
			'href',
			'/web/cms/files'
		);
	});

	it('renders the spaces with a link to all of them', () => {
		renderComponent();

		expect(screen.getByText('spaces')).toHaveAttribute(
			'aria-expanded',
			'true'
		);
		expect(screen.getByText('Marketing').closest('a')).toHaveAttribute(
			'href',
			'/web/cms/spaces/4'
		);
		expect(screen.getByText('all-spaces-x')).toHaveAttribute(
			'href',
			'/web/cms/all-spaces'
		);
	});

	it('moves the elements rendered by the server into the sidebar', () => {
		const banner = document.createElement('div');

		banner.textContent = 'Banner';

		renderComponent({elements: [banner]});

		expect(
			within(screen.getByRole('complementary')).getByText('Banner')
		).toBe(banner);
	});

	it('expands the saved sections instead of the default ones', () => {
		renderComponent({expandedKeys: []});

		expect(screen.getByText('Assets')).toHaveAttribute(
			'aria-expanded',
			'false'
		);
		expect(screen.getByText('spaces')).toHaveAttribute(
			'aria-expanded',
			'false'
		);
	});

	it('announces that it listens for requests', () => {
		renderComponent();

		expect(Liferay.fire).toHaveBeenCalledWith('cmsSidebarReady', {
			visible: true,
		});
	});

	it('closes from the backdrop on a narrow screen', async () => {
		overlay = true;

		renderComponent();

		const [, handleStateRequest] = (
			Liferay.on as jest.Mock
		).mock.calls.find(
			([eventName]) => eventName === 'cmsSidebarStateRequested'
		);

		await act(() => handleStateRequest({visible: true}));

		expect(document.body).toHaveClass('cms-sidebar-open');

		await userEvent.click(
			document.body.querySelector('.cadmin > .c-slideout-backdrop')!
		);

		await waitFor(() =>
			expect(document.body).not.toHaveClass('cms-sidebar-open')
		);

		expect(Liferay.Util.Session.set).toHaveBeenLastCalledWith(
			'com.liferay.application.list_sidebarState',
			'closed'
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = renderComponent();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
