/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {doAlign} from '@clayui/shared';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import Tour from '../src/main/resources/META-INF/resources/Tour';

import type {TourStep} from '../src/main/resources/META-INF/resources/types';

jest.mock('@liferay/accessibility-settings-state-web', () => ({
	isReducedMotion: () => false,
}));

jest.mock('@clayui/shared', () => ({
	...(jest.requireActual('@clayui/shared') as object),
	doAlign: jest.fn(({points}) => ({points})),
}));

const STEPS: TourStep[] = [
	{content: 'Content 1', selector: '#logo', title: 'Title 1'},
	{
		content: 'Content 2',
		positioning: 'top',
		selector: '#search',
		title: 'Title 2',
	},
	{content: 'Content 3', selector: '#footer', title: 'Title 3'},
];

function renderTour(props = {}) {
	return render(<Tour steps={STEPS} {...props} />);
}

describe('Tour', () => {
	beforeAll(() => {
		Element.prototype.scrollIntoView = jest.fn();
	});

	beforeEach(() => {
		jest.clearAllMocks();

		document.body.innerHTML = `
			<button id="trigger">Take the tour</button>
			<div id="logo"></div>
			<div id="search"></div>
			<div id="footer"></div>
		`;
	});

	it('shows the first step as a dialog named after the step', () => {
		renderTour();

		expect(screen.getByRole('dialog')).toHaveAccessibleName(/Title 1$/);
		expect(screen.getByRole('dialog')).toHaveAccessibleDescription(
			'Content 1'
		);
	});

	it('has no accessibility violations', async () => {
		renderTour();

		await checkAccessibility({
			bestPractices: true,
			context: screen.getByRole('dialog'),
		});
	});

	it('moves through the steps with the next and previous buttons', async () => {
		renderTour();

		await userEvent.click(screen.getByRole('button', {name: 'next'}));

		expect(screen.getByRole('dialog')).toHaveAccessibleName(/Title 2$/);

		await userEvent.click(screen.getByRole('button', {name: 'previous'}));

		expect(screen.getByRole('dialog')).toHaveAccessibleName(/Title 1$/);
		expect(
			screen.queryByRole('button', {name: 'previous'})
		).not.toBeInTheDocument();
	});

	it('finishes the tour from the last step', async () => {
		const onClose = jest.fn();

		renderTour({onClose});

		await userEvent.click(screen.getByRole('button', {name: 'next'}));
		await userEvent.click(screen.getByRole('button', {name: 'next'}));
		await userEvent.click(screen.getByRole('button', {name: 'done'}));

		expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
		expect(onClose).toHaveBeenCalledWith('finished');
	});

	it('closes the tour with the close button', async () => {
		const onClose = jest.fn();

		renderTour({onClose});

		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
		expect(onClose).toHaveBeenCalledWith('closed');
	});

	it('closes the tour with the Escape key', async () => {
		const onClose = jest.fn();

		renderTour({onClose});

		await userEvent.keyboard('{Escape}');

		expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
		expect(onClose).toHaveBeenCalledWith('closed');
	});

	it('hides the close button when the tour is not closeable', () => {
		renderTour({closeable: false});

		expect(
			screen.queryByRole('button', {name: 'close'})
		).not.toBeInTheDocument();
	});

	it('keeps the focus inside the popover', async () => {

		// Clay's focus manager treats an element without offsetParent as
		// hidden, and jsdom never computes it, so give it one for this test.

		const offsetParentDescriptor = Object.getOwnPropertyDescriptor(
			HTMLElement.prototype,
			'offsetParent'
		)!;

		Object.defineProperty(HTMLElement.prototype, 'offsetParent', {
			configurable: true,
			get() {
				return this.parentNode;
			},
		});

		try {
			renderTour();

			await userEvent.tab();
			await userEvent.tab();
			await userEvent.tab();

			expect(screen.getByRole('button', {name: 'close'})).toHaveFocus();

			await userEvent.tab({shift: true});

			expect(screen.getByRole('button', {name: 'next'})).toHaveFocus();
		}
		finally {
			Object.defineProperty(
				HTMLElement.prototype,
				'offsetParent',
				offsetParentDescriptor
			);
		}
	});

	it('moves the focus to the popover and returns it to the trigger on close', async () => {
		document.getElementById('trigger')!.focus();

		renderTour();

		expect(screen.getByRole('dialog')).toHaveFocus();

		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(document.getElementById('trigger')).toHaveFocus();
	});

	it('skips the steps whose element is not in the DOM', async () => {
		jest.spyOn(console, 'warn').mockImplementation(() => {});

		document.getElementById('search')!.remove();

		renderTour();

		await userEvent.click(screen.getByRole('button', {name: 'next'}));

		expect(screen.getByRole('dialog')).toHaveAccessibleName(/Title 3$/);
		expect(console.warn).toHaveBeenCalledWith(
			expect.stringContaining('#search')
		);
	});

	it('renders nothing when no step element is in the DOM', () => {
		jest.spyOn(console, 'warn').mockImplementation(() => {});

		document.body.innerHTML = '';

		renderTour();

		expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
	});

	it('scrolls the highlighted element into view once per step', async () => {
		renderTour();

		expect(Element.prototype.scrollIntoView).toHaveBeenCalledTimes(1);

		await userEvent.click(screen.getByRole('button', {name: 'next'}));

		expect(Element.prototype.scrollIntoView).toHaveBeenCalledTimes(2);
	});

	it('locks the page scroll while the tour is open when lockScroll is set', async () => {
		renderTour({lockScroll: true});

		expect(document.documentElement.style.overflow).toBe('hidden');
		expect(document.body.style.overflow).toBe('hidden');

		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(document.documentElement.style.overflow).toBe('');
		expect(document.body.style.overflow).toBe('');
	});

	it('does not lock the page scroll by default', () => {
		renderTour();

		expect(document.documentElement.style.overflow).toBe('');
	});

	it('ends the tour on close by default', async () => {
		renderTour();

		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(
			screen.queryByRole('button', {name: 'resume-the-tour'})
		).not.toBeInTheDocument();
	});

	it('pauses a pauseable tour on close and resumes it from the same step', async () => {
		const onClose = jest.fn();

		renderTour({onClose, pauseable: true});

		await userEvent.click(screen.getByRole('button', {name: 'next'}));
		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
		expect(onClose).not.toHaveBeenCalled();

		const hotspot = screen.getByRole('button', {name: 'resume-the-tour'});

		expect(hotspot).toHaveFocus();
		expect(doAlign).toHaveBeenLastCalledWith(
			expect.objectContaining({
				points: ['cc', 'tl'],
				sourceElement: hotspot,
				targetElement: document.getElementById('search'),
			})
		);

		await userEvent.click(hotspot);

		expect(screen.getByRole('dialog')).toHaveAccessibleName(/Title 2$/);
		expect(
			screen.queryByRole('button', {name: 'resume-the-tour'})
		).not.toBeInTheDocument();
	});

	it('pauses a pauseable tour with the Escape key', async () => {
		renderTour({pauseable: true});

		await userEvent.keyboard('{Escape}');

		expect(
			screen.getByRole('button', {name: 'resume-the-tour'})
		).toBeInTheDocument();
	});

	it('does not leave a hotspot when a pauseable tour is finished', async () => {
		const onClose = jest.fn();

		renderTour({onClose, pauseable: true, steps: [STEPS[0]]});

		await userEvent.click(screen.getByRole('button', {name: 'done'}));

		expect(
			screen.queryByRole('button', {name: 'resume-the-tour'})
		).not.toBeInTheDocument();
		expect(onClose).toHaveBeenCalledWith('finished');
	});

	it('aligns the popover to the side requested by the step', async () => {
		renderTour();

		expect(doAlign).toHaveBeenCalledWith(
			expect.objectContaining({
				points: ['tl', 'tr'],
				sourceElement: screen.getByRole('dialog'),
				targetElement: document.getElementById('logo'),
			})
		);

		await userEvent.click(screen.getByRole('button', {name: 'next'}));

		expect(doAlign).toHaveBeenCalledWith(
			expect.objectContaining({
				points: ['bc', 'tc'],
				sourceElement: screen.getByRole('dialog'),
				targetElement: document.getElementById('search'),
			})
		);
		expect(screen.getByRole('dialog')).toHaveClass('clay-popover-top');
	});

	it('points the arrow at the element when dom-align flips the popover', () => {
		(doAlign as jest.Mock).mockReturnValueOnce({points: ['tc', 'bc']});

		renderTour({
			steps: [{...STEPS[0], positioning: 'top'}],
		});

		expect(screen.getByRole('dialog')).toHaveClass('clay-popover-bottom');
	});
});
