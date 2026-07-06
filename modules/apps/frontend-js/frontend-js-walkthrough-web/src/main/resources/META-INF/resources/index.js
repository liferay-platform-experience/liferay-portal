/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';
import {localStorage} from 'frontend-js-web';
import React from 'react';

import Walkthrough from './components/Walkthrough';
import {LOCAL_STORAGE_KEYS} from './utils';

const DEFAULT_CONTAINER_ID = 'walkthroughContainer';

let lastProps = null;

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

	return <Walkthrough {...props} />;
}

export {Walkthrough};

export function main(props = {}) {
	lastProps = props;

	Liferay.Walkthrough = {restart};

	render(Root, props, getDefaultContainer());
}

/**
 * Clears the per-user walkthrough state (current step, popover visibility,
 * and the "do not show me this again" dismissal) and mounts the walkthrough
 * again, so a completed or dismissed tour can be replayed.
 */
export function restart() {
	if (!lastProps) {
		return;
	}

	Object.values(LOCAL_STORAGE_KEYS).forEach((key) =>
		localStorage.removeItem(key)
	);

	const container = document.getElementById(DEFAULT_CONTAINER_ID);

	if (container) {
		container.remove();
	}

	main(lastProps);
}
