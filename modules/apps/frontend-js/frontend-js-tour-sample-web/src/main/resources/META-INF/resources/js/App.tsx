/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import React from 'react';

export function App() {
	return (
		<div>
			<h2>Tour Sample Portlet</h2>

			<p>
				This widget shows how a trigger starts a tour. Click the button
				below to start a tour of this widget and of the page header.
			</p>

			<ClayButton
				onClick={() =>
					Liferay.Tour.start({
						lockScroll: true,
						steps: [
							{
								content:
									'Your account, notifications and settings live here.',
								selector: '.portlet-user-personal-bar',
								title: 'Your profile',
							},
							{
								content:
									'Submit the form from here once you are done.',
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
					})
				}
			>
				Take the Tour
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