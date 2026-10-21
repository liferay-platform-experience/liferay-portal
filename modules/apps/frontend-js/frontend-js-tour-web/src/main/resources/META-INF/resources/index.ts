/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';

import Tour from './Tour';

import type {TourDefinition} from './types';

export type {TourCloseReason, TourDefinition, TourStep} from './types';

const COMPONENT_ID = 'tour';

const CONTAINER_ID = 'tourContainer';

/**
 * Starts a tour from the given definition, replacing the tour that is open,
 * if any. The trigger that calls it (a "Take the tour" button, an intro modal,
 * or other code) owns the definition, so no per site configuration is needed.
 */
export function start(tourDefinition: TourDefinition) {
	let container = document.getElementById(CONTAINER_ID);

	if (container) {
		Liferay.destroyComponent(COMPONENT_ID);

		container.remove();
	}

	container = document.createElement('div');

	container.id = CONTAINER_ID;

	document.body.appendChild(container);

	render(Tour, {...tourDefinition, componentId: COMPONENT_ID}, container);
}
