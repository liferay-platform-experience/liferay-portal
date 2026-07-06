/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {navigate} from 'frontend-js-web';
import React from 'react';

import Walkthrough from '../../src/main/resources/META-INF/resources/components/Walkthrough';
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
} from '../__lib__/walkthroughMock';

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

function renderWalkthrough(props) {
	return render(<Walkthrough {...props} />, {
		baseElement: document.getElementById('app_root'),
	});
}

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	navigate: jest.fn(),
}));

navigate.mockImplementation(jest.fn((url) => url));

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

describe('Walkthrough', () => {
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
		const {container, getByLabelText} = renderWalkthrough(PAGE_MOCK);

		expect(container).toBeInTheDocument();
		expect(getByLabelText('start-the-walkthrough')).toBeInTheDocument();

		expect(warnSpy).not.toHaveBeenCalled();
	});

	it('renders nothing when there are no steps', () => {
		render(<Walkthrough pages={{}} steps={[]} />);

		expect(
			screen.queryByLabelText('start-the-walkthrough')
		).not.toBeInTheDocument();
	});

	it(`when clicking on Next, it navigates to the given URL of 'next'`, async () => {
		const {getByLabelText} = renderWalkthrough(MULTI_PAGES_MOCK);

		const hotspot = getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		await userEvent.click(screen.getByText('ok'));

		expect(navigate).toBeCalledWith(MULTI_PAGES_MOCK.steps[0].next);
	});

	it(`when clicking on Previous, it navigates to the given URL of 'previous'`, async () => {
		const {getByLabelText} = renderWalkthrough(PAGE_WITH_PREVIOUS_MOCK);

		const hotspot = getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		await userEvent.click(screen.getByText('ok'));

		await userEvent.click(screen.getByText('previous'));

		expect(navigate).toBeCalledWith(
			PAGE_WITH_PREVIOUS_MOCK.steps[1].previous
		);
	});

	it(`when clicking on Next, without passing 'next', it shows the next Walkthrough without navigating`, async () => {
		const {getByLabelText} = renderWalkthrough(PAGE_MOCK);

		const hotspot = getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		await userEvent.click(screen.getByText('ok'));

		expect(screen.queryByText('Hello2')).toBeInTheDocument();
	});

	it(`when clicking on Previous, without passing 'previous', it shows the previous Walkthrough without navigating`, async () => {
		const {getByLabelText} = renderWalkthrough(PAGE_MOCK);

		const hotspot = getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		await userEvent.click(screen.getByText('ok'));

		expect(screen.queryByText('Hello2')).toBeInTheDocument();

		await userEvent.click(screen.getByText('previous'));

		expect(screen.queryByText('Hello1')).toBeInTheDocument();
	});

	it('warns and falls back to a renderable step when the current selector does not exist', async () => {
		renderWalkthrough(INVALID_NODE_SELECTOR_MOCK);

		expect(warnSpy).toHaveBeenCalled();

		const hotspot = screen.getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		expect(screen.queryByText('Hello2')).toBeInTheDocument();
	});

	it('warns and renders nothing when no step of the page has an existing element', () => {
		renderWalkthrough(NO_RENDERABLE_STEP_MOCK);

		expect(warnSpy).toHaveBeenCalled();

		expect(
			screen.queryByLabelText('start-the-walkthrough')
		).not.toBeInTheDocument();
	});

	it('warns and falls back to a renderable step when a selector is not valid CSS', async () => {
		renderWalkthrough(INVALID_CSS_SELECTOR_MOCK);

		expect(warnSpy).toHaveBeenCalled();

		const hotspot = screen.getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		expect(screen.queryByText('Hello2')).toBeInTheDocument();
	});

	it('recovers from a corrupted persisted step by starting from the first step', async () => {
		window.localStorage.setItem(
			LOCAL_STORAGE_KEYS.CURRENT_STEP,
			'{corrupted'
		);

		const errorSpy = jest.spyOn(console, 'error').mockImplementation();

		renderWalkthrough(PAGE_MOCK);

		expect(errorSpy).toHaveBeenCalled();

		expect(
			window.localStorage.getItem(LOCAL_STORAGE_KEYS.CURRENT_STEP)
		).toBeNull();

		await userEvent.click(screen.getByLabelText('start-the-walkthrough'));

		expect(screen.queryByText('Hello1')).toBeInTheDocument();
	});

	it('recovers from an out-of-range persisted step by starting from the first step', async () => {
		window.localStorage.setItem(LOCAL_STORAGE_KEYS.CURRENT_STEP, '7');

		renderWalkthrough(PAGE_MOCK);

		expect(
			window.localStorage.getItem(LOCAL_STORAGE_KEYS.CURRENT_STEP)
		).toBe('0');

		await userEvent.click(screen.getByLabelText('start-the-walkthrough'));

		expect(screen.queryByText('Hello1')).toBeInTheDocument();
	});

	it('shows the step title and content in the user language', async () => {
		themeDisplay.getLanguageId = jest.fn(() => 'es_ES');

		renderWalkthrough(LOCALIZED_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-walkthrough'));

		expect(
			screen.queryByText('Título localizado', {exact: false})
		).toBeInTheDocument();
		expect(screen.queryByText('Contenido localizado')).toBeInTheDocument();

		themeDisplay.getLanguageId = jest.fn(() => 'en_US');
	});

	it('falls back to the default language when there is no translation for the user language', async () => {
		themeDisplay.getLanguageId = jest.fn(() => 'fr_FR');

		renderWalkthrough(LOCALIZED_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-walkthrough'));

		expect(
			screen.queryByText('Localized title', {exact: false})
		).toBeInTheDocument();
		expect(screen.queryByText('Localized content')).toBeInTheDocument();

		themeDisplay.getLanguageId = jest.fn(() => 'en_US');
	});

	it(`when 'darkbg' is set to false adds a 'lfr-walkthrough-element-shadow' to the nodeToHighlight'`, async () => {
		const {getByLabelText} = renderWalkthrough(BOX_SHADOW_ELEMENT_MOCK);

		const hotspot = getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		const elementToBeHighlighted = await screen.findByTestId(
			'external-react-tree-button'
		);

		expect(elementToBeHighlighted).toHaveClass(
			'lfr-walkthrough-element-shadow'
		);
	});

	it.each(POSSIBLE_LAYOUT_RELATIVE_URLS_HOME)(
		`given the following layoutRelativeURL: '%s' and the value of '/home' the component needs to be rendered`,
		(url, shouldRender) => {
			window.themeDisplay.getLayoutRelativeURL = jest.fn(() => url);

			const cleanUp = setupDocument();

			const {getByLabelText, queryByLabelText} =
				renderWalkthrough(PAGE_MOCK);

			if (shouldRender) {
				expect(
					getByLabelText('start-the-walkthrough')
				).toBeInTheDocument();
			}
			else {
				expect(
					queryByLabelText('start-the-walkthrough')
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

			const {getByLabelText} = renderWalkthrough(PAGE_MOCK);

			expect(getByLabelText('start-the-walkthrough')).toBeInTheDocument();

			cleanUp();
		}
	);

	it('persists the current step of a determined page on localStorage', async () => {
		const {getByLabelText} = renderWalkthrough(PAGE_MOCK);

		const hotspot = getByLabelText('start-the-walkthrough');

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
		const {getByLabelText} = renderWalkthrough(PAGE_MOCK);

		const hotspot = getByLabelText('start-the-walkthrough');

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
		renderWalkthrough(PAGE_MOCK);

		await userEvent.click(screen.getByLabelText('start-the-walkthrough'));

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
		const {getByLabelText} = renderWalkthrough({
			closeOnClickOutside: true,
			...PAGE_MOCK,
		});

		const hotspot = getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		expect(screen.getByText('ok')).toBeInTheDocument();

		await userEvent.click(document.body);

		expect(screen.queryByText('ok')).not.toBeInTheDocument();

		expect(getByLabelText('start-the-walkthrough')).toBeInTheDocument();
	});

	it('when `closeable` property is set to false, it should not be able to close the popover using the X button', async () => {
		const {getByLabelText} = renderWalkthrough({
			closeable: false,
			...PAGE_MOCK,
		});

		const hotspot = getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		expect(screen.queryByLabelText('close')).not.toBeInTheDocument();
	});

	it('when `closeable` property is set to true, it should be able to close the popover using the X button', async () => {
		const {getByLabelText} = renderWalkthrough({
			closeable: true,
			...PAGE_MOCK,
		});

		const hotspot = getByLabelText('start-the-walkthrough');

		await userEvent.click(hotspot);

		expect(screen.queryByLabelText('close')).toBeInTheDocument();
	});
});
