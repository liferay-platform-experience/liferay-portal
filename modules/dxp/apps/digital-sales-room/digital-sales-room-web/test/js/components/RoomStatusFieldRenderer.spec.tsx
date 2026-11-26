/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {ROOM_STATUS} from '../../../src/main/resources/META-INF/resources/js/common/utils/roomStatus';
import RoomStatusFieldRenderer from '../../../src/main/resources/META-INF/resources/js/components/props_transformer/cell_renderers/RoomStatusFieldRenderer';

describe('RoomStatusFieldRenderer', () => {
	it.each([
		[ROOM_STATUS.ACTIVE, 'active', 'success'],
		[ROOM_STATUS.INACTIVE, 'archived', 'warning'],
	])(
		'renders status %i as an outline "%s" label',
		(status, label, displayType) => {
			render(<RoomStatusFieldRenderer value={status} />);

			const labelElement = screen.getByText(label).closest('.label');

			expect(labelElement).toHaveClass(`label-${displayType}`);
			expect(labelElement).not.toHaveClass(
				`label-inverse-${displayType}`
			);
		}
	);
});
