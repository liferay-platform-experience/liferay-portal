/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';

import '@testing-library/jest-dom';

import {useUnifiedSidebarPageVisibility} from '../../src/main/resources/META-INF/resources/unified_sidebar/useUnifiedSidebarPageVisibility';

function renderPageVisibility() {
	return renderHook(() =>
		useUnifiedSidebarPageVisibility({
			bodyClassName: 'sidebar-open sidebar-pushed',
			initialVisible: true,
			readyEventName: 'ready',
			sessionKey: 'sessionKey',
			stateChangedEventName: 'stateChanged',
			stateRequestedEventName: 'stateRequested',
		})
	);
}

describe('useUnifiedSidebarPageVisibility', () => {
	const util = Liferay.Util;

	let overlay = false;

	beforeEach(() => {
		jest.clearAllMocks();

		overlay = false;

		(window as any).matchMedia = (query: string) => ({
			addEventListener: () => {},
			matches: overlay,
			media: query,
			removeEventListener: () => {},
		});

		document.body.classList.add('sidebar-open', 'sidebar-pushed');

		Liferay.Util = {
			...util,
			Session: {
				get: jest.fn(),
				set: jest.fn(() => Promise.resolve()),
			},
		};
	});

	afterEach(() => {
		delete (window as any).matchMedia;

		document.body.className = '';

		Liferay.Util = util;
	});

	it('updates the body classes', () => {
		const {result} = renderPageVisibility();

		act(() => result.current.updateVisible(false));

		expect(document.body).not.toHaveClass('sidebar-open');
		expect(document.body).not.toHaveClass('sidebar-pushed');
		expect(document.body).toHaveClass('sidenav-transition');

		act(() => result.current.updateVisible(true));

		expect(document.body).toHaveClass('sidebar-open', 'sidebar-pushed');
	});

	it('announces that it listens for requests', () => {
		const {result} = renderPageVisibility();

		expect(result.current.overlay).toBe(false);
		expect(result.current.visible).toBe(true);
		expect(Liferay.fire).toHaveBeenCalledWith('ready', {visible: true});
	});

	it('closes on a narrow screen when the page loads', () => {
		overlay = true;

		const {result} = renderPageVisibility();

		expect(result.current.overlay).toBe(true);
		expect(result.current.visible).toBe(false);
		expect(document.body).not.toHaveClass('sidebar-open');
		expect(document.body).not.toHaveClass('sidebar-pushed');
		expect(Liferay.fire).toHaveBeenCalledWith('stateChanged', {
			visible: false,
		});
		expect(Liferay.fire).toHaveBeenCalledWith('ready', {visible: false});
	});
});
