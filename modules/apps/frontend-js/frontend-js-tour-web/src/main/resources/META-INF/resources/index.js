/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';
import {localStorage} from 'frontend-js-web';
import React from 'react';

import Tour from './components/Tour';
import {LOCAL_STORAGE_KEYS} from './utils';

const DEFAULT_CONTAINER_ID = 'tourContainer';

let lastTourDefinition = null;

const getDefaultContainer = () => {
	let container = document.getElementById(DEFAULT_CONTAINER_ID);

	if (!container) {
		container = document.createElement('div');
		container.id = DEFAULT_CONTAINER_ID;
		document.body.appendChild(container);
	}

	return container;
};

function isDismissed() {
	return (
		localStorage.getItem(
			LOCAL_STORAGE_KEYS.SKIPPABLE,
			localStorage.TYPES.NECESSARY
		) === 'true'
	);
}

function Root(props) {
	if (isDismissed()) {
		return null;
	}

	return <Tour {...props} />;
}

export {Tour};

/**
 * Exposes the global `Liferay.Tour` API so any trigger (a "Take the tour"
 * button, an intro modal, or other code) can start or replay a tour. A tour is
 * triggered on demand rather than provisioned per site.
 */
export function register() {
	Liferay.Tour = {restart, start};
}

/**
 * Starts a tour from the given definition. The caller (the trigger) passes the
 * whole definition here, so no per-site configuration is needed.
 * @param {Object} tourDefinition the tour props (pages, steps, lockScroll, ...)
 */
export function start(tourDefinition = {}) {
	lastTourDefinition = tourDefinition;

	register();

	render(Root, tourDefinition, getDefaultContainer());
}

/**
 * Clears the per-user tour state (current step, popover visibility, and the
 * "do not show me this again" dismissal) and starts the last tour again, so a
 * completed or dismissed tour can be replayed.
 */
export function restart() {
	if (!lastTourDefinition) {
		return;
	}

	Object.values(LOCAL_STORAGE_KEYS).forEach((key) =>
		localStorage.removeItem(key)
	);

	const container = document.getElementById(DEFAULT_CONTAINER_ID);

	if (container) {
		container.remove();
	}

	start(lastTourDefinition);
}
