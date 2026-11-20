/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import UnifiedSidebar from '../../src/main/resources/META-INF/resources/unified_sidebar/UnifiedSidebar';
import UnifiedSidebarSiteSelector from '../../src/main/resources/META-INF/resources/unified_sidebar/UnifiedSidebarSiteSelector';

jest.mock('@clayui/shared', () => ({
	...jest.requireActual('@clayui/shared'),
	useIsMobileDevice: () => false,
}));

const DEFAULT_PROPS = {
	categoryImageUrl: '/images/test-icon.svg',
	label: 'Control Panel',
};

describe('UnifiedSidebar', () => {
	it('renders the label', () => {
		render(<UnifiedSidebar {...DEFAULT_PROPS} />);

		expect(screen.getByText('Control Panel')).toBeInTheDocument();
	});

	it('renders children inside the panel body', () => {
		render(
			<UnifiedSidebar {...DEFAULT_PROPS}>
				<UnifiedSidebar.Body>
					<p>Panel content</p>
				</UnifiedSidebar.Body>
			</UnifiedSidebar>
		);

		expect(screen.getByText('Panel content')).toBeInTheDocument();
	});

	it('applies testId as data-testid', () => {
		render(<UnifiedSidebar {...DEFAULT_PROPS} testId="my-sidebar" />);

		expect(screen.getByTestId('my-sidebar')).toBeInTheDocument();
	});

	it('renders headerActions inside the header', () => {
		render(
			<UnifiedSidebar
				{...DEFAULT_PROPS}
				headerActions={
					<UnifiedSidebarSiteSelector
						eventName="selectSite"
						url="/select-site"
					/>
				}
			/>
		);

		expect(screen.getByLabelText('go-to-other-site')).toBeInTheDocument();
	});

	it('does not render the site selector button when headerActions is absent', () => {
		render(<UnifiedSidebar {...DEFAULT_PROPS} />);

		expect(
			screen.queryByLabelText('go-to-other-site')
		).not.toBeInTheDocument();
	});

	describe('when LPD-57922 is enabled', () => {
		beforeEach(() => {
			Liferay.FeatureFlags['LPD-57922'] = true;
		});

		afterEach(() => {
			Liferay.FeatureFlags['LPD-57922'] = false;
		});

		it('hides the color scheme toggle button with a back button', () => {
			render(
				<UnifiedSidebar
					{...DEFAULT_PROPS}
					colorScheme="light"
					colorSchemeSessionKey="colorScheme"
					onBack={jest.fn()}
				/>
			);

			expect(screen.getByLabelText('go-back')).toBeInTheDocument();
			expect(
				screen.queryByLabelText('switch-to-dark-mode')
			).not.toBeInTheDocument();
		});

		it('shows the color scheme toggle button', () => {
			render(
				<UnifiedSidebar
					{...DEFAULT_PROPS}
					colorScheme="light"
					colorSchemeSessionKey="colorScheme"
				/>
			);

			expect(
				screen.getByLabelText('switch-to-dark-mode')
			).toBeInTheDocument();
		});
	});

	it('does not show the color scheme toggle button when LPD-57922 is disabled', () => {
		render(<UnifiedSidebar {...DEFAULT_PROPS} />);

		expect(
			screen.queryByLabelText('switch-to-dark-mode')
		).not.toBeInTheDocument();
	});

	describe('when the pointer is over the toggle', () => {
		function renderWithToggle(
			props: Partial<React.ComponentProps<typeof UnifiedSidebar>> = {}
		) {
			render(
				<>
					<button aria-controls="navigation">Toggle</button>

					<UnifiedSidebar
						{...DEFAULT_PROPS}
						navigationId="navigation"
						{...props}
					/>
				</>
			);
		}

		it('shows the peek as an overlay without the backdrop', async () => {
			renderWithToggle({backdrop: true});

			const panel = screen.getByTestId('unified-sidebar');

			expect(panel).not.toHaveClass('c-slideout-show');

			await userEvent.hover(screen.getByText('Toggle'));

			expect(panel.closest('.c-slideout')).toHaveClass(
				'c-slideout-overlay'
			);
			expect(panel).toHaveClass('c-slideout-show');
			expect(
				document.querySelector('.c-slideout-backdrop')
			).not.toBeInTheDocument();
		});

		it('hides the peek on Escape without unpinning the sidebar', async () => {
			const onOpenChange = jest.fn();

			renderWithToggle({onOpenChange});

			const panel = screen.getByTestId('unified-sidebar');

			await userEvent.hover(screen.getByText('Toggle'));

			expect(panel).toHaveClass('c-slideout-show');

			await userEvent.keyboard('{Escape}');

			await waitFor(() =>
				expect(panel).not.toHaveClass('c-slideout-show')
			);

			expect(onOpenChange).not.toHaveBeenCalled();
		});

		it('pins the sidebar from the header toggle of the peek', async () => {
			const onOpenChange = jest.fn();

			renderWithToggle({onOpenChange});

			await userEvent.hover(screen.getByText('Toggle'));

			const headerToggle = screen.getByLabelText('pin-product-menu');

			expect(headerToggle).toHaveAttribute('aria-expanded', 'false');
			expect(headerToggle.querySelector('img')).toHaveAttribute(
				'src',
				'/images/test-icon.svg'
			);

			await userEvent.click(headerToggle);

			expect(onOpenChange).toHaveBeenCalledWith(true);
		});
	});

	it('unpins the pinned sidebar from its header toggle', async () => {
		const onOpenChange = jest.fn();

		const {container} = render(
			<UnifiedSidebar
				{...DEFAULT_PROPS}
				onOpenChange={onOpenChange}
				open
			/>
		);

		const headerToggle = screen.getByLabelText('close-product-menu');

		expect(headerToggle).toHaveAttribute('aria-expanded', 'true');
		expect(container.querySelector('img')).not.toBeInTheDocument();

		await userEvent.click(headerToggle);

		expect(onOpenChange).toHaveBeenCalledWith(false);
	});
});
