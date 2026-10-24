/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import {start} from '../src/main/resources/META-INF/resources';
import Tour from '../src/main/resources/META-INF/resources/Tour';

jest.mock('@liferay/frontend-js-react-web', () => ({
	render: jest.fn(),
}));

const TOUR_DEFINITION = {
	steps: [{content: 'Content 1', selector: '#logo', title: 'Title 1'}],
};

describe('start', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		document.body.innerHTML = '';
	});

	it('renders the tour in a container appended to the body', () => {
		start(TOUR_DEFINITION);

		expect(render).toHaveBeenCalledWith(
			Tour,
			{...TOUR_DEFINITION, componentId: 'tour'},
			document.body.lastElementChild
		);
	});

	it('replaces the tour that is open', () => {
		start(TOUR_DEFINITION);

		const container = document.body.lastElementChild;

		start(TOUR_DEFINITION);

		expect(Liferay.destroyComponent).toHaveBeenCalledWith('tour');
		expect(container).not.toBeInTheDocument();
		expect(document.body.children).toHaveLength(1);
	});
});
