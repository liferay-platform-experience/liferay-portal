/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getLocalizedText,
	querySelectorSafe,
} from '../src/main/resources/META-INF/resources/utils';

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
