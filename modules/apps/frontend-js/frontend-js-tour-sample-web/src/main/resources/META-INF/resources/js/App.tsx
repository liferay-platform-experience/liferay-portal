/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import {sessionStorage} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

const DISMISSED_KEY = 'frontend-js-tour-sample-web.dismissed';

const PAUSED_STEP_KEY = 'frontend-js-tour-sample-web.pausedStep';

const STEPS = [
	{
		content: 'Your account, notifications and settings live here.',
		selector: '.portlet-user-personal-bar',
		title: 'Your profile',
	},
	{
		content: 'Submit the form from here once you are done.',
		positioning: 'bottom' as const,
		selector: '#tourSampleSubmit',
		title: 'Submit',
	},
	{
		content: 'Type your name in this field.',
		positioning: 'top' as const,
		selector: '#tourSampleName',
		title: 'Name',
	},
];

type Scenario = {
	description: string;
	name: string;
	start: (log: (event: string) => void) => void;
};

/**
 * One scenario per option combination. The trigger (this widget) owns
 * whatever has to survive a reload: the paused step and the dismissal are
 * kept in the session storage.
 */
const SCENARIOS: Scenario[] = [
	{
		description:
			'No options. Closing with the X or Escape ends the tour, and so does Done on the last step.',
		name: 'Basic',
		start: (log) =>
			Liferay.Tour.start({
				onClose: (reason) => log(`onClose(${reason})`),
				steps: STEPS,
			}),
	},
	{
		description:
			'The page cannot be scrolled while a step is open; the tour still scrolls between steps.',
		name: 'Locked scroll',
		start: (log) =>
			Liferay.Tour.start({
				lockScroll: true,
				onClose: (reason) => log(`onClose(${reason})`),
				steps: STEPS,
			}),
	},
	{
		description:
			'No close button. Escape still ends the tour, so the user is never trapped.',
		name: 'Not closeable',
		start: (log) =>
			Liferay.Tour.start({
				closeable: false,
				onClose: (reason) => log(`onClose(${reason})`),
				steps: STEPS,
			}),
	},
	{
		description:
			'Closing pauses the tour and leaves a hotspot on the element. The paused step is stored, so reloading the page restores the hotspot.',
		name: 'Pauseable',
		start: (log) => startPauseable(log),
	},
	{
		description:
			'Shows the "Do not show me this again" checkbox. Once checked, this scenario stops starting the tour until you reset it.',
		name: 'Dismissable',
		start: (log) => {
			if (
				sessionStorage.getItem(
					DISMISSED_KEY,
					sessionStorage.TYPES.NECESSARY
				)
			) {
				log('not started: the user asked not to see it again');

				return;
			}

			Liferay.Tour.start({
				dismissable: true,
				onClose: (reason) => {
					log(`onClose(${reason})`);

					if (reason === 'dismissed') {
						sessionStorage.setItem(
							DISMISSED_KEY,
							'true',
							sessionStorage.TYPES.NECESSARY
						);
					}
				},
				steps: STEPS,
			});
		},
	},
	{
		description:
			'Starts directly on the second step, the way a trigger resumes a tour it stored.',
		name: 'From the second step',
		start: (log) =>
			Liferay.Tour.start({
				initialStep: 1,
				onClose: (reason) => log(`onClose(${reason})`),
				steps: STEPS,
			}),
	},
];

function startPauseable(
	log: (event: string) => void,
	options: {initialStep?: number; paused?: boolean} = {}
) {
	Liferay.Tour.start({
		...options,
		lockScroll: true,
		onClose: (reason) => {
			log(`onClose(${reason})`);

			sessionStorage.removeItem(PAUSED_STEP_KEY);
		},
		onPause: (stepIndex) => {
			log(`onPause(${stepIndex})`);

			sessionStorage.setItem(
				PAUSED_STEP_KEY,
				String(stepIndex),
				sessionStorage.TYPES.NECESSARY
			);
		},
		pauseable: true,
		steps: STEPS,
	});
}

export function App() {
	const [lastEvent, setLastEvent] = useState('none yet');

	useEffect(() => {
		const pausedStep = sessionStorage.getItem(
			PAUSED_STEP_KEY,
			sessionStorage.TYPES.NECESSARY
		);

		if (pausedStep !== null) {
			startPauseable(setLastEvent, {
				initialStep: Number(pausedStep),
				paused: true,
			});
		}
	}, []);

	return (
		<div>
			<h2>Tour Sample Portlet</h2>

			<p>
				Each button starts the same three step tour with a different
				option combination. The widget plays the role of the trigger: it
				decides when to start the tour and stores what has to survive a
				reload.
			</p>

			<p>
				Last event received: <code>{lastEvent}</code>
			</p>

			<ul className="list-unstyled">
				{SCENARIOS.map((scenario) => (
					<li className="mb-3" key={scenario.name}>
						<ClayButton
							displayType="secondary"
							onClick={() => scenario.start(setLastEvent)}
							size="sm"
						>
							{scenario.name}
						</ClayButton>

						<span className="ml-2 text-secondary">
							{scenario.description}
						</span>
					</li>
				))}
			</ul>

			<ClayButton
				displayType="link"
				onClick={() => {
					sessionStorage.removeItem(DISMISSED_KEY);
					sessionStorage.removeItem(PAUSED_STEP_KEY);

					setLastEvent('stored state reset');
				}}
				size="sm"
			>
				Reset the stored state
			</ClayButton>

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
