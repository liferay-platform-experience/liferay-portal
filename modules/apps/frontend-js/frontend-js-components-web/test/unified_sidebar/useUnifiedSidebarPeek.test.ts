/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useIsMobileDevice} from '@clayui/shared';
import {act, renderHook} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import {useUnifiedSidebarPeek} from '../../src/main/resources/META-INF/resources/unified_sidebar/useUnifiedSidebarPeek';

jest.mock('@clayui/shared', () => ({
	...jest.requireActual('@clayui/shared'),
	useIsMobileDevice: jest.fn(),
}));

function movePointer(from: Element, to: Element) {
	for (const [type, target, relatedTarget] of [
		['pointerout', from, to],
		['pointerover', to, from],
	] as const) {
		act(() => {
			target.dispatchEvent(
				Object.assign(new Event(type, {bubbles: true}), {
					pointerType: 'mouse',
					relatedTarget,
				})
			);
		});
	}
}

function renderPeek({open = false}: {open?: boolean} = {}) {
	const toggle = document.createElement('button');

	toggle.setAttribute('aria-controls', 'navigation');

	document.body.appendChild(toggle);

	const panel = document.createElement('div');

	panel.id = 'navigation';

	document.body.appendChild(panel);

	const hook = renderHook(
		(props: {open: boolean}) =>
			useUnifiedSidebarPeek({
				...props,
				navigationId: 'navigation',
			}),
		{initialProps: {open}}
	);

	return {...hook, panel, toggle};
}

describe('useUnifiedSidebarPeek', () => {
	let user: ReturnType<typeof userEvent.setup>;

	beforeEach(() => {
		jest.useFakeTimers();

		(useIsMobileDevice as jest.Mock).mockReturnValue(false);

		user = userEvent.setup({advanceTimers: jest.advanceTimersByTime});
	});

	afterEach(() => {
		document.body.innerHTML = '';

		jest.useRealTimers();
	});

	it.each(['panel', 'toggle'] as const)(
		'peeks while the pointer is over the %s',
		async (target) => {
			const peek = renderPeek();

			expect(peek.result.current.peek).toBe(false);

			await user.hover(peek[target]);

			expect(peek.result.current.peek).toBe(true);
		}
	);

	it('hides 250 ms after the pointer leaves', async () => {
		const {result, toggle} = renderPeek();

		await user.hover(toggle);
		await user.hover(document.body);

		act(() => jest.advanceTimersByTime(249));

		expect(result.current.peek).toBe(true);

		act(() => jest.advanceTimersByTime(1));

		expect(result.current.peek).toBe(false);
	});

	it('keeps peeking when the pointer comes back within the grace period', async () => {
		const {panel, result, toggle} = renderPeek();

		await user.hover(toggle);
		await user.hover(panel);
		await user.hover(document.body);

		act(() => jest.advanceTimersByTime(200));

		await user.hover(toggle);

		act(() => jest.advanceTimersByTime(1000));

		expect(result.current.peek).toBe(true);
	});

	it('keeps peeking while a popup opened from the panel shows', async () => {
		const {panel, result, toggle} = renderPeek();

		const popupTrigger = document.createElement('button');

		popupTrigger.setAttribute('aria-expanded', 'true');
		popupTrigger.setAttribute('aria-haspopup', 'menu');

		panel.appendChild(popupTrigger);

		await user.hover(toggle);
		await user.hover(document.body);

		act(() => jest.advanceTimersByTime(1000));

		expect(result.current.peek).toBe(true);
	});

	it('keeps a dismissed peek closed until the mouse moves out', async () => {
		const {panel, result, toggle} = renderPeek();

		await user.hover(toggle);

		act(() => result.current.dismiss());

		expect(result.current.peek).toBe(false);

		await user.hover(panel);

		expect(result.current.peek).toBe(false);

		movePointer(panel, document.body);
		movePointer(document.body, toggle);

		expect(result.current.peek).toBe(false);

		await user.hover(document.body);
		await user.hover(toggle);

		expect(result.current.peek).toBe(true);
	});

	it('follows the pinned state while the pointer is over the toggle', async () => {
		const {rerender, result, toggle} = renderPeek({open: true});

		await user.hover(toggle);

		expect(result.current.peek).toBe(false);

		rerender({open: false});

		expect(result.current.peek).toBe(true);

		rerender({open: true});

		expect(result.current.peek).toBe(false);
	});

	it('does nothing on a narrow screen', async () => {
		(useIsMobileDevice as jest.Mock).mockReturnValue(true);

		const {result, toggle} = renderPeek();

		await user.hover(toggle);

		expect(result.current.peek).toBe(false);
	});

	it('does nothing with touch', async () => {
		const {result, toggle} = renderPeek();

		await user.pointer({keys: '[TouchA]', target: toggle});

		expect(result.current.peek).toBe(false);
	});
});
