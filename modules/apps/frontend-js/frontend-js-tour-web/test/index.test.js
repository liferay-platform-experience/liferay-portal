/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import {
	register,
	restart,
	start,
} from '../src/main/resources/META-INF/resources/index';
import {LOCAL_STORAGE_KEYS} from '../src/main/resources/META-INF/resources/utils';

jest.mock('@liferay/frontend-js-react-web', () => ({
	render: jest.fn(),
}));

describe('tour trigger API', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		delete Liferay.Tour;

		window.localStorage.clear();
	});

	it('registers the global Liferay.Tour API without starting a tour', () => {
		register();

		expect(Liferay.Tour.start).toBe(start);
		expect(Liferay.Tour.restart).toBe(restart);
		expect(render).not.toHaveBeenCalled();
	});

	it('does not restart before a tour is started', () => {
		restart();

		expect(render).not.toHaveBeenCalled();
	});

	it('starts the tour with the given definition and registers the API', () => {
		start({steps: []});

		expect(render).toHaveBeenCalledTimes(1);
		expect(Liferay.Tour.start).toBe(start);
		expect(Liferay.Tour.restart).toBe(restart);
	});

	it('clears the persisted state and starts the last tour again on restart', () => {
		start({steps: []});

		Object.values(LOCAL_STORAGE_KEYS).forEach((key) =>
			window.localStorage.setItem(key, 'true')
		);

		restart();

		Object.values(LOCAL_STORAGE_KEYS).forEach((key) =>
			expect(window.localStorage.getItem(key)).toBeNull()
		);

		expect(render).toHaveBeenCalledTimes(2);
	});
});
