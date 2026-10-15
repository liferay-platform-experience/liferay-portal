/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getVariations,
	parseVariations,
} from '../../../src/main/resources/META-INF/resources/js/style-book-editor/utils/variations';

const TOKENS = {primary: {cssVariableMapping: 'primary', value: '#000'}};

describe('variations', () => {
	it('reads a flat map of tokens as the default variation', () => {
		expect(parseVariations(TOKENS)).toEqual({
			default: {name: 'default', tokens: TOKENS},
		});
	});

	it('keeps values that already hold variations', () => {
		const variations = {
			dark: {name: 'Dark', tokens: {}},
			default: {name: 'default', tokens: TOKENS},
		};

		expect(parseVariations(variations)).toBe(variations);
	});

	it('writes the edited tokens into the active variation', () => {
		expect(
			getVariations({
				activeVariation: 'dark',
				frontendTokensValues: TOKENS,
				variations: {
					dark: {name: 'Dark', tokens: {}},
					default: {name: 'default', tokens: {}},
				},
			})
		).toEqual({
			dark: {name: 'Dark', tokens: TOKENS},
			default: {name: 'default', tokens: {}},
		});
	});
});
