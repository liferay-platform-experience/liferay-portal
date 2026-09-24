/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon, {ClayIconSpriteContext} from '..';
import React from 'react';
import TestRenderer from 'react-test-renderer';

describe('ClayIcon', () => {
	it('renders', () => {
		const testRenderer = TestRenderer.create(
			<ClayIcon
				spritemap="/path/to/some/resource.svg"
				symbol="cool-icon"
			/>
		);

		expect(testRenderer.toJSON()).toMatchSnapshot();
	});

	it('renders with context spritemap', () => {
		const testRenderer = TestRenderer.create(
			<ClayIconSpriteContext.Provider value="foo/bar.svg">
				<ClayIcon symbol="cool-icon" />
			</ClayIconSpriteContext.Provider>
		);

		expect(testRenderer.toJSON()).toMatchSnapshot();
	});

	it('renders without the lexicon-icon classes when `lexiconIcon` is false', () => {
		const testRenderer = TestRenderer.create(
			<ClayIcon
				lexiconIcon={false}
				spritemap="/path/to/some/empty_states.svg"
				symbol="success-state"
			/>
		);

		expect(testRenderer.toJSON()).toMatchSnapshot();
	});

	it('renders the className when `lexiconIcon` is false', () => {
		const testRenderer = TestRenderer.create(
			<ClayIcon
				className="custom-icon"
				lexiconIcon={false}
				spritemap="/path/to/some/empty_states.svg"
				symbol="success-state"
			/>
		);

		expect(testRenderer.toJSON()).toMatchSnapshot();
	});
});
