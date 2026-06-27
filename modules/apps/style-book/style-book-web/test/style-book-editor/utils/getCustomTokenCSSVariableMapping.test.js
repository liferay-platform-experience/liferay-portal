/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getCustomTokenCSSVariableMapping} from '../../../src/main/resources/META-INF/resources/js/style-book-editor/utils/getCustomTokenCSSVariableMapping';

describe('getCustomTokenCSSVariableMapping', () => {
	it('lowercases and hyphenates the name', () => {
		expect(getCustomTokenCSSVariableMapping('My Token')).toBe('my-token');
	});

	it('collapses runs of non-alphanumeric characters into one hyphen', () => {
		expect(getCustomTokenCSSVariableMapping('Brand   Accent!!')).toBe(
			'brand-accent'
		);
	});

	it('returns an empty string when the name has no alphanumeric characters', () => {
		expect(getCustomTokenCSSVariableMapping('!!!')).toBe('');
	});
});
