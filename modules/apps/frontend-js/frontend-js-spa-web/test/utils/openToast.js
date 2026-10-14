/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from '../../src/main/resources/META-INF/resources/util/openToast';

jest.mock('@liferay/frontend-icons-web', () => ({
	getSpritemap: () => '/spritemap.svg',
}));

describe('openToast', () => {
	afterEach(() => {
		document.body.innerHTML = '';
	});

	it('shares one fixed column between toasts', () => {
		openToast('error', 'Error', 'First message');
		openToast('warn', 'Warning', 'Second message');

		const columns = document.querySelectorAll('.alert-notifications-fixed');

		expect(columns).toHaveLength(1);
		expect(document.querySelectorAll('#ToastAlertContainer')).toHaveLength(
			1
		);
		expect(columns[0].children).toHaveLength(2);
	});

	it('leaves the other toast in place when one is disposed', () => {
		const first = openToast('error', 'Error', 'First message');

		openToast('warn', 'Warning', 'Second message');

		first.dispose();

		const column = document.querySelector('.alert-notifications-fixed');

		expect(column.children).toHaveLength(1);
		expect(column).toHaveTextContent('Second message');
		expect(column).not.toHaveTextContent('First message');
	});
});
