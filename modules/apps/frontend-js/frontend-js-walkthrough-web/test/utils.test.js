/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	clampToDocument,
	getLocalizedText,
	querySelectorSafe,
} from '../src/main/resources/META-INF/resources/utils';

describe('clampToDocument', () => {
	function createPositionedElement({left, rect, top}) {
		const element = document.createElement('div');

		element.style.left = left;
		element.style.top = top;

		element.getBoundingClientRect = () => ({
			height: 32,
			width: 32,
			...rect,
		});

		return element;
	}

	it('shifts an element that overflows the top-left corner back inside', () => {
		const element = createPositionedElement({
			left: '-16px',
			rect: {left: -26, top: -26},
			top: '-16px',
		});

		clampToDocument(element);

		expect(element.style.left).toBe('10px');
		expect(element.style.top).toBe('10px');
	});

	it('shifts an element that overflows the document width back inside', () => {
		Object.defineProperty(document.documentElement, 'scrollWidth', {
			configurable: true,
			value: 800,
		});
		Object.defineProperty(document.documentElement, 'scrollHeight', {
			configurable: true,
			value: 600,
		});

		const element = createPositionedElement({
			left: '790px',
			rect: {left: 790, top: 100},
			top: '100px',
		});

		clampToDocument(element);

		expect(element.style.left).toBe('768px');
		expect(element.style.top).toBe('100px');

		delete document.documentElement.scrollWidth;
		delete document.documentElement.scrollHeight;
	});

	it('leaves an element that already fits alone', () => {
		Object.defineProperty(document.documentElement, 'scrollWidth', {
			configurable: true,
			value: 800,
		});
		Object.defineProperty(document.documentElement, 'scrollHeight', {
			configurable: true,
			value: 600,
		});

		const element = createPositionedElement({
			left: '100px',
			rect: {left: 100, top: 100},
			top: '100px',
		});

		clampToDocument(element);

		expect(element.style.left).toBe('100px');
		expect(element.style.top).toBe('100px');

		delete document.documentElement.scrollWidth;
		delete document.documentElement.scrollHeight;
	});
});

describe('getLocalizedText', () => {
	afterEach(() => {
		themeDisplay.getLanguageId = jest.fn(() => 'en_US');
	});

	it('returns plain strings as is', () => {
		expect(getLocalizedText('plain')).toBe('plain');
	});

	it('returns empty values as is', () => {
		expect(getLocalizedText(undefined)).toBeUndefined();
		expect(getLocalizedText('')).toBe('');
	});

	it('returns the translation for the user language', () => {
		themeDisplay.getLanguageId = jest.fn(() => 'es_ES');

		expect(getLocalizedText({en_US: 'Hello', es_ES: 'Hola'})).toBe('Hola');
	});

	it('falls back to the site default language', () => {
		themeDisplay.getLanguageId = jest.fn(() => 'fr_FR');

		expect(getLocalizedText({en_US: 'Hello', es_ES: 'Hola'})).toBe('Hello');
	});

	it('falls back to the first available translation', () => {
		themeDisplay.getDefaultLanguageId = jest.fn(() => 'fr_FR');
		themeDisplay.getLanguageId = jest.fn(() => 'fr_FR');

		expect(getLocalizedText({es_ES: 'Hola'})).toBe('Hola');

		themeDisplay.getDefaultLanguageId = jest.fn(() => 'en_US');
	});
});

describe('querySelectorSafe', () => {
	afterEach(() => {
		document.body.innerHTML = '';
	});

	it('returns the matched node', () => {
		document.body.innerHTML = '<div class="logo"></div>';

		expect(querySelectorSafe('.logo')).toBe(
			document.querySelector('.logo')
		);
	});

	it('returns null when the selector matches nothing', () => {
		expect(querySelectorSafe('.missing')).toBeNull();
	});

	it('returns null when the selector is empty', () => {
		expect(querySelectorSafe(undefined)).toBeNull();
		expect(querySelectorSafe('')).toBeNull();
	});

	it('warns and returns null when the selector is not valid CSS', () => {
		const warnSpy = jest.spyOn(console, 'warn').mockImplementation();

		expect(querySelectorSafe(':::not-a-selector')).toBeNull();

		expect(warnSpy).toHaveBeenCalled();

		warnSpy.mockRestore();
	});
});
