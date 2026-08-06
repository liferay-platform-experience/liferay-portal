/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, cleanup, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import domAlign from 'dom-align';
import {navigate} from 'frontend-js-web';
import React from 'react';

import Tour from '../../src/main/resources/META-INF/resources/components/Tour';
import {LOCAL_STORAGE_KEYS} from '../../src/main/resources/META-INF/resources/utils';
import {
	BOX_SHADOW_ELEMENT_MOCK,
	DOM_STRUCTURE_FOR_PLACING_STEPS,
	INVALID_CSS_SELECTOR_MOCK,
	INVALID_NODE_SELECTOR_MOCK,
	LOCALIZED_MOCK,
	MULTI_PAGES_MOCK,
	NO_RENDERABLE_STEP_MOCK,
	PAGE_MOCK,
	PAGE_WITH_PREVIOUS_MOCK,
} from '../__lib__/tourMock';

function setupDocument() {
	const domStructureReference = document.createElement('div');

	domStructureReference.innerHTML = DOM_STRUCTURE_FOR_PLACING_STEPS;

	domStructureReference.id = 'test-structure';

	document.body.appendChild(domStructureReference);

	return () => {
		window.localStorage.clear();

		const nodeToBeRemoved = document.getElementById('test-structure');

		document.body.removeChild(nodeToBeRemoved);

		nodeToBeRemoved.remove();
	};
}

function renderTour(props) {
	return render(<Tour {...props} />, {
		baseElement: document.getElementById('app_root'),
	});
}

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	navigate: jest.fn(),
}));

jest.mock('dom-align', () =>
	jest.fn((sourceElement, targetElement, config) => ({
		overflow: {adjustX: false, adjustY: false},
		points: config.points,
	}))
);

navigate.mockImplementation(jest.fn((url) => url));

function countPopoverAligns() {
	return domAlign.mock.calls.filter(([sourceElement]) =>
		sourceElement?.classList?.contains('lfr-tour-popover')
	).length;
}

/**
 * List of tuples containing as the first member
 * a layoutRelativeURL and the other one, if it
 * should render or not considering the given path `/home`
 */
const POSSIBLE_LAYOUT_RELATIVE_URLS_HOME = [
	['/undefined', false],
	['/es/web/home/undefined', false],
	['/es/web/home?redirectURL=/es/web/guest/home/abc', true],
	['/home', true],
	['/es/home', true],
	['/es/web/guest/home', true],
	['/home/abc/home', true],
	['/es/home/abc/home', true],
	['/es/web/guest/home/abc/home', true],
];

/**
 * List of strings containg a composed layoutRelativeURLs to be matched with
 * `/home/abc`
 */
const POSSIBLE_LAYOUT_RELATIVE_URLS_HOME_ABC = [
	'/home/abc',
	'/es/home/abc',
	'/es/web/guest/home/abc',
];

describe('Tour', () => {
	let cleanUpDocument;
	let warnSpy;

	beforeEach(() => {
		cleanUpDocument = setupDocument();
		warnSpy = jest.spyOn(console, 'warn').mockImplementation();
	});

	afterEach(() => {
		cleanUpDocument();
		cleanup();
		jest.restoreAllMocks();
		themeDisplay.getLayoutRelativeURL = jest.fn(() => '/home');
	});

	it('renders', () => {
		const {container, getByLabelText} = renderTour(PAGE_MOCK);

		expect(container).toBeInTheDocument();
		expect(getByLabelText('start-the-tour')).toBeInTheDocument();

		expect(warnSpy).not.toHaveBeenCalled();
	});

	it('renders nothing when there are no steps', () => {
		render(<Tour pages={{}} steps={[]} />);

		expect(
			screen.queryByLabelText('start-the-tour')
		).not.toBeInTheDocument();
	});

	it(`when clicking on Next, it navigates to the given URL of 'next'`, async () => {
		const {getByLabelText} = renderTour(MULTI_PAGES_MOCK);

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		await userEvent.click(screen.getByText('ok'));

		expect(navigate).toBeCalledWith(MULTI_PAGES_MOCK.steps[0].next);
	});

	it(`when clicking on Previous, it navigates to the given URL of 'previous'`, async () => {
		const {getByLabelText} = renderTour(PAGE_WITH_PREVIOUS_MOCK);

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		await userEvent.click(screen.getByText('ok'));

		await userEvent.click(screen.getByText('previous'));

		expect(navigate).toBeCalledWith(
			PAGE_WITH_PREVIOUS_MOCK.steps[1].previous
		);
	});

	it(`when clicking on Next, without passing 'next', it shows the next Tour without navigating`, async () => {
		const {getByLabelText} = renderTour(PAGE_MOCK);

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		await userEvent.click(screen.getByText('ok'));

		expect(screen.queryByText('Hello2')).toBeInTheDocument();
	});

	it(`when clicking on Previous, without passing 'previous', it shows the previous Tour without navigating`, async () => {
		const {getByLabelText} = renderTour(PAGE_MOCK);

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		await userEvent.click(screen.getByText('ok'));

		expect(screen.queryByText('Hello2')).toBeInTheDocument();

		await userEvent.click(screen.getByText('previous'));

		expect(screen.queryByText('Hello1')).toBeInTheDocument();
	});

	it('has no accessibility violations while a step popover is open', async () => {
		const {container} = renderTour(PAGE_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('traps Tab and Shift+Tab focus inside the open popover', async () => {
		renderTour(PAGE_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		const popover = document.querySelector('.lfr-tour-popover');

		const focusableElements = popover.querySelectorAll(
			'a[href], button:not([disabled]), input:not([disabled]), [tabindex]:not([tabindex="-1"])'
		);

		const firstElement = focusableElements[0];

		const lastElement = focusableElements[focusableElements.length - 1];

		lastElement.focus();

		await userEvent.tab();

		expect(document.activeElement).toBe(firstElement);

		firstElement.focus();

		await userEvent.tab({shift: true});

		expect(document.activeElement).toBe(lastElement);
	});

	it('pulls focus back into the popover when it escapes to the dimmed area', async () => {
		renderTour(PAGE_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		const popover = document.querySelector('.lfr-tour-popover');

		const outsideElement = document.createElement('button');

		document.body.appendChild(outsideElement);

		outsideElement.focus();

		expect(popover.contains(document.activeElement)).toBe(true);

		outsideElement.remove();
	});

	it('exposes the hotspot as a keyboard-focusable button that opens on Enter', async () => {
		renderTour(PAGE_MOCK);

		const hotspot = screen.getByLabelText('start-the-tour');

		expect(hotspot.tagName).toBe('BUTTON');

		hotspot.focus();

		expect(hotspot).toHaveFocus();

		await userEvent.keyboard('{Enter}');

		expect(screen.getByText('ok')).toBeInTheDocument();
	});

	it('realigns the popover after it is closed and reopened from the hotspot', async () => {
		renderTour(PAGE_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(countPopoverAligns()).toBeGreaterThan(0);

		await userEvent.click(screen.getByLabelText('close'));

		const alignsAfterClose = countPopoverAligns();

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(countPopoverAligns()).toBeGreaterThan(alignsAfterClose);
	});

	it('realigns the popover when advancing to a step that shares the anchor', async () => {
		const sameAnchorMock = {
			pages: {'/home': ['step-1', 'step-2']},
			steps: [
				{
					content: '<span>Short</span>',
					darkbg: true,
					id: 'step-1',
					nodeToHighlight: '.logo',
					positioning: 'top',
					title: 'Title 1',
				},
				{
					content:
						'<span>A much longer body that resizes the popover</span>',
					darkbg: true,
					id: 'step-2',
					nodeToHighlight: '.logo',
					positioning: 'top',
					title: 'Title 2',
				},
			],
		};

		renderTour(sameAnchorMock);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		const alignsOnFirstStep = countPopoverAligns();

		await userEvent.click(screen.getByText('ok'));

		expect(countPopoverAligns()).toBeGreaterThan(alignsOnFirstStep);
	});

	it('realigns the popover when the window is resized', async () => {
		renderTour(PAGE_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		const alignsBeforeResize = countPopoverAligns();

		act(() => {
			window.dispatchEvent(new Event('resize'));
		});

		await waitFor(() =>
			expect(countPopoverAligns()).toBeGreaterThan(alignsBeforeResize)
		);
	});

	it('locks the page scroll while the popover is open when `lockScroll` is enabled', async () => {
		renderTour({lockScroll: true, ...PAGE_MOCK});

		expect(document.documentElement.style.overflow).not.toBe('hidden');

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(document.documentElement.style.overflow).toBe('hidden');
		expect(document.body.style.overflow).toBe('hidden');

		await userEvent.click(screen.getByLabelText('close'));

		expect(document.documentElement.style.overflow).not.toBe('hidden');
		expect(document.body.style.overflow).not.toBe('hidden');
	});

	it('does not lock the page scroll when `lockScroll` is not enabled', async () => {
		renderTour(PAGE_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(document.documentElement.style.overflow).not.toBe('hidden');
		expect(document.body.style.overflow).not.toBe('hidden');
	});

	it('scrolls the highlighted element into view once per step when the popover opens', async () => {
		const scrollIntoViewMock = jest.fn();

		window.HTMLElement.prototype.scrollIntoView = scrollIntoViewMock;

		renderTour(PAGE_MOCK);

		expect(scrollIntoViewMock).not.toHaveBeenCalled();

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(scrollIntoViewMock).toHaveBeenCalledTimes(1);
		expect(scrollIntoViewMock.mock.instances[0]).toBe(
			document.querySelector('.logo')
		);

		await userEvent.click(screen.getByText('ok'));

		expect(scrollIntoViewMock).toHaveBeenCalledTimes(2);
		expect(scrollIntoViewMock.mock.instances[1]).toBe(
			document.querySelector('#footer')
		);

		delete window.HTMLElement.prototype.scrollIntoView;
	});

	it('warns and falls back to a renderable step when the current selector does not exist', async () => {
		renderTour(INVALID_NODE_SELECTOR_MOCK);

		expect(warnSpy).toHaveBeenCalled();

		const hotspot = screen.getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		expect(screen.queryByText('Hello2')).toBeInTheDocument();
	});

	it('warns and renders nothing when no step of the page has an existing element', () => {
		renderTour(NO_RENDERABLE_STEP_MOCK);

		expect(warnSpy).toHaveBeenCalled();

		expect(
			screen.queryByLabelText('start-the-tour')
		).not.toBeInTheDocument();
	});

	it('warns and falls back to a renderable step when a selector is not valid CSS', async () => {
		renderTour(INVALID_CSS_SELECTOR_MOCK);

		expect(warnSpy).toHaveBeenCalled();

		const hotspot = screen.getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		expect(screen.queryByText('Hello2')).toBeInTheDocument();
	});

	it('recovers from a corrupted persisted step by starting from the first step', async () => {
		window.localStorage.setItem(
			LOCAL_STORAGE_KEYS.CURRENT_STEP,
			'{corrupted'
		);

		const errorSpy = jest.spyOn(console, 'error').mockImplementation();

		renderTour(PAGE_MOCK);

		expect(errorSpy).toHaveBeenCalled();

		expect(
			window.localStorage.getItem(LOCAL_STORAGE_KEYS.CURRENT_STEP)
		).toBeNull();

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(screen.queryByText('Hello1')).toBeInTheDocument();
	});

	it('recovers from an out-of-range persisted step by starting from the first step', async () => {
		window.localStorage.setItem(LOCAL_STORAGE_KEYS.CURRENT_STEP, '7');

		renderTour(PAGE_MOCK);

		expect(
			window.localStorage.getItem(LOCAL_STORAGE_KEYS.CURRENT_STEP)
		).toBe('0');

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(screen.queryByText('Hello1')).toBeInTheDocument();
	});

	it('does not render "undefined" when a step has no title or content', async () => {
		renderTour({
			pages: {'/home': ['step-1']},
			steps: [{darkbg: true, id: 'step-1', nodeToHighlight: '.logo'}],
		});

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(
			document.querySelector('.lfr-tour-popover').textContent
		).not.toContain('undefined');
	});

	it('shows the step title and content in the user language', async () => {
		themeDisplay.getLanguageId = jest.fn(() => 'es_ES');

		renderTour(LOCALIZED_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(
			screen.queryByText('Título localizado', {exact: false})
		).toBeInTheDocument();
		expect(screen.queryByText('Contenido localizado')).toBeInTheDocument();

		themeDisplay.getLanguageId = jest.fn(() => 'en_US');
	});

	it('falls back to the default language when there is no translation for the user language', async () => {
		themeDisplay.getLanguageId = jest.fn(() => 'fr_FR');

		renderTour(LOCALIZED_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		expect(
			screen.queryByText('Localized title', {exact: false})
		).toBeInTheDocument();
		expect(screen.queryByText('Localized content')).toBeInTheDocument();

		themeDisplay.getLanguageId = jest.fn(() => 'en_US');
	});

	it(`when 'darkbg' is set to false adds a 'lfr-tour-element-shadow' to the nodeToHighlight'`, async () => {
		const {getByLabelText} = renderTour(BOX_SHADOW_ELEMENT_MOCK);

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		const elementToBeHighlighted = await screen.findByTestId(
			'external-react-tree-button'
		);

		expect(elementToBeHighlighted).toHaveClass('lfr-tour-element-shadow');
	});

	it.each(POSSIBLE_LAYOUT_RELATIVE_URLS_HOME)(
		`given the following layoutRelativeURL: '%s' and the value of '/home' the component needs to be rendered`,
		(url, shouldRender) => {
			window.themeDisplay.getLayoutRelativeURL = jest.fn(() => url);

			const cleanUp = setupDocument();

			const {getByLabelText, queryByLabelText} = renderTour(PAGE_MOCK);

			if (shouldRender) {
				expect(getByLabelText('start-the-tour')).toBeInTheDocument();
			}
			else {
				expect(
					queryByLabelText('start-the-tour')
				).not.toBeInTheDocument();
			}

			cleanUp();
		}
	);

	it.each(POSSIBLE_LAYOUT_RELATIVE_URLS_HOME_ABC)(
		`given the following layoutRelativeURL: '%s' and the value of '/home/abc' the component needs to be rendered`,
		(url) => {
			window.themeDisplay.getLayoutRelativeURL = jest.fn(() => url);

			const cleanUp = setupDocument();

			const {getByLabelText} = renderTour(PAGE_MOCK);

			expect(getByLabelText('start-the-tour')).toBeInTheDocument();

			cleanUp();
		}
	);

	it('persists the current step of a determined page on localStorage', async () => {
		const {getByLabelText} = renderTour(PAGE_MOCK);

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		await userEvent.click(screen.getByText('ok'));

		expect(
			window.localStorage.getItem(LOCAL_STORAGE_KEYS.POPOVER_VISIBILITY)
		).toBe('true');

		expect(
			window.localStorage.getItem(LOCAL_STORAGE_KEYS.CURRENT_STEP)
		).toBe('1');
	});

	it('when clicking on "Do not show me this again" makes the Hotspot not render when closing the popover', async () => {
		const {getByLabelText} = renderTour(PAGE_MOCK);

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		expect(
			window.localStorage.getItem(LOCAL_STORAGE_KEYS.SKIPPABLE)
		).not.toBe('true');

		await userEvent.click(
			screen.getByLabelText('do-not-show-me-this-again')
		);

		expect(
			screen.getByLabelText('do-not-show-me-this-again')
		).toBeChecked();

		expect(window.localStorage.getItem(LOCAL_STORAGE_KEYS.SKIPPABLE)).toBe(
			'true'
		);
	});

	it('unchecking "Do not show me this again" removes the dismissal', async () => {
		renderTour(PAGE_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-tour'));

		const checkbox = screen.getByLabelText('do-not-show-me-this-again');

		await userEvent.click(checkbox);

		expect(window.localStorage.getItem(LOCAL_STORAGE_KEYS.SKIPPABLE)).toBe(
			'true'
		);

		await userEvent.click(checkbox);

		expect(
			window.localStorage.getItem(LOCAL_STORAGE_KEYS.SKIPPABLE)
		).toBeNull();
	});

	it('when `closeOnClickOutside` is enabled, it should close when clicking outside the popover', async () => {
		const {getByLabelText} = renderTour({
			closeOnClickOutside: true,
			...PAGE_MOCK,
		});

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		expect(screen.getByText('ok')).toBeInTheDocument();

		await userEvent.click(document.body);

		expect(screen.queryByText('ok')).not.toBeInTheDocument();

		expect(getByLabelText('start-the-tour')).toBeInTheDocument();
	});

	it('when `closeable` property is set to false, it should not be able to close the popover using the X button', async () => {
		const {getByLabelText} = renderTour({
			closeable: false,
			...PAGE_MOCK,
		});

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		expect(screen.queryByLabelText('close')).not.toBeInTheDocument();
	});

	it('when `closeable` property is set to true, it should be able to close the popover using the X button', async () => {
		const {getByLabelText} = renderTour({
			closeable: true,
			...PAGE_MOCK,
		});

		const hotspot = getByLabelText('start-the-tour');

		await userEvent.click(hotspot);

		expect(screen.queryByLabelText('close')).toBeInTheDocument();
	});
});
