/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import {sessionStorage} from 'frontend-js-web';
import React, {useEffect} from 'react';

const PAUSED_STEP_KEY = 'frontend-js-tour-sample-web.pausedStep';

/**
 * The trigger owns the tour state: it stores the step a paused tour stopped
 * at, so the tour can be started again paused at that step after a reload.
 */
function startTour(options: {initialStep?: number; paused?: boolean} = {}) {
	Liferay.Tour.start({
		...options,
		dismissable: true,
		lockScroll: true,
		onClose: () => sessionStorage.removeItem(PAUSED_STEP_KEY),
		onPause: (stepIndex) =>
			sessionStorage.setItem(
				PAUSED_STEP_KEY,
				String(stepIndex),
				sessionStorage.TYPES.NECESSARY
			),
		pauseable: true,
		steps: [
			{
				content: 'Your account, notifications and settings live here.',
				selector: '.portlet-user-personal-bar',
				title: 'Your profile',
			},
			{
				content: 'Submit the form from here once you are done.',
				positioning: 'bottom',
				selector: '#tourSampleSubmit',
				title: 'Submit',
			},
			{
				content: 'Type your name in this field.',
				positioning: 'top',
				selector: '#tourSampleName',
				title: 'Name',
			},
		],
	});
}

export function App() {
	useEffect(() => {
		const pausedStep = sessionStorage.getItem(
			PAUSED_STEP_KEY,
			sessionStorage.TYPES.NECESSARY
		);

		if (pausedStep !== null) {
			startTour({initialStep: Number(pausedStep), paused: true});
		}
	}, []);

	return (
		<div>
			<h2>Tour Sample Portlet</h2>

			<p>
				This widget shows how a trigger starts a tour. Click the button
				below to start a tour of this widget and of the page header.
			</p>

			<ClayButton onClick={() => startTour()}>Take the Tour</ClayButton>

			<hr />

			<ClayButton.Group id="tourSampleSubmit" spaced>
				<ClayButton displayType="secondary" type="submit">
					Click Here
				</ClayButton>
			</ClayButton.Group>

			<ClayForm.Group>
				<label htmlFor="tourSampleName">Name</label>

				<ClayInput
					id="tourSampleName"
					placeholder="Insert your name here."
					type="text"
				/>
			</ClayForm.Group>
		</div>
	);
}
