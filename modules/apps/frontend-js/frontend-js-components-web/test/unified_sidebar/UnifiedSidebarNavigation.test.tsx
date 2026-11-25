/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayVerticalNav} from '@clayui/nav';
import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import UnifiedSidebar from '../../src/main/resources/META-INF/resources/unified_sidebar/UnifiedSidebar';
import UnifiedSidebarNavigation from '../../src/main/resources/META-INF/resources/unified_sidebar/UnifiedSidebarNavigation';

const ITEMS = [
	{
		href: '/web/guest/pages',
		id: 'pages',
		label: 'Pages',
	},
	{
		href: '/web/guest/documents',
		id: 'documents',
		label: 'Documents',
	},
];

function renderComponent(
	props: Partial<React.ComponentProps<typeof UnifiedSidebarNavigation>> = {}
) {
	return render(
		<UnifiedSidebar categoryImageUrl="imageURL" label="Site Menu" open>
			<UnifiedSidebarNavigation
				active="pages"
				expandedKeys={new Set()}
				initialExpandedKeys={new Set()}
				items={ITEMS}
				label="Site Menu"
				numberOfResults={ITEMS.length}
				onExpandedChange={() => {}}
				renderItem={(item) => (
					<ClayVerticalNav.Item
						href={item.href}
						key={item.id}
						textValue={item.label}
					>
						{item.label}
					</ClayVerticalNav.Item>
				)}
				{...props}
			/>
		</UnifiedSidebar>
	);
}

describe('UnifiedSidebarNavigation', () => {
	beforeEach(() => {
		(window as any).IntersectionObserver = class {
			disconnect() {}

			observe() {}

			unobserve() {}
		};
	});

	afterEach(() => {
		delete (window as any).IntersectionObserver;
	});

	it('renders the items with the active one', () => {
		renderComponent();

		const navigation = screen.getByRole('navigation', {name: 'Site Menu'});

		expect(navigation).toContainElement(screen.getByText('Pages'));
		expect(screen.getByText('Pages')).toHaveClass('active');
		expect(screen.getByText('Documents')).toHaveAttribute(
			'href',
			'/web/guest/documents'
		);
	});

	it('renders an empty state when nothing matches', () => {
		renderComponent({numberOfResults: 0});

		expect(screen.getByText('no-matching-items')).toBeInTheDocument();
		expect(screen.queryByText('Pages')).not.toBeInTheDocument();
	});

	it('renders the children after the items', () => {
		renderComponent({children: <p>Banner</p>});

		expect(screen.getByText('Banner')).toBeInTheDocument();
	});
});
