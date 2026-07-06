/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import {main, restart} from '../src/main/resources/META-INF/resources/index';
import {LOCAL_STORAGE_KEYS} from '../src/main/resources/META-INF/resources/utils';

jest.mock('@liferay/frontend-js-react-web', () => ({
	render: jest.fn(),
}));

describe('main', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		window.localStorage.clear();
	});

	it('does not restart before the walkthrough is mounted', () => {
		restart();

		expect(render).not.toHaveBeenCalled();
	});

	it('mounts the walkthrough and registers the global restart API', () => {
		main({steps: []});

		expect(render).toHaveBeenCalledTimes(1);
		expect(Liferay.Walkthrough.restart).toBe(restart);
	});

	it('clears the persisted state and mounts the walkthrough again on restart', () => {
		main({steps: []});

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
