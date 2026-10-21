/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {observeRect} from '../observeRect';

describe('observeRect', () => {
	let frames: Array<() => void>;

	function runFrame() {
		const pendingFrames = frames;

		frames = [];

		pendingFrames.forEach((frame) => frame());
	}

	function mockRect(node: Element, width: number) {
		jest.spyOn(node, 'getBoundingClientRect').mockImplementation(
			() => ({width}) as DOMRect
		);
	}

	beforeEach(() => {
		frames = [];

		jest.spyOn(window, 'requestAnimationFrame').mockImplementation(
			(frame) => frames.push(frame as () => void)
		);
		jest.spyOn(window, 'cancelAnimationFrame').mockImplementation((id) => {
			frames.splice(id - 1, 1);
		});
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('calls the callback when the rect changes', () => {
		const node = document.createElement('div');
		const callback = jest.fn();

		mockRect(node, 10);

		observeRect(node, callback);

		expect(callback).toHaveBeenCalledTimes(1);

		runFrame();

		expect(callback).toHaveBeenCalledTimes(1);

		mockRect(node, 20);

		runFrame();

		expect(callback).toHaveBeenCalledTimes(2);
	});

	it('keeps observing the other nodes when one node stops being observed', () => {
		const firstNode = document.createElement('div');
		const firstCallback = jest.fn();
		const secondNode = document.createElement('div');
		const secondCallback = jest.fn();

		mockRect(firstNode, 10);
		mockRect(secondNode, 10);

		const unobserveFirstNode = observeRect(firstNode, firstCallback);

		observeRect(secondNode, secondCallback);

		unobserveFirstNode();

		mockRect(firstNode, 20);
		mockRect(secondNode, 20);

		runFrame();

		expect(firstCallback).toHaveBeenCalledTimes(1);
		expect(secondCallback).toHaveBeenCalledTimes(2);
	});
});
