/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import React from 'react';

import AppIcon from '../../../src/main/resources/META-INF/resources/js/homes/components/AppIcon';

describe('AppIcon', () => {
	it('renders a link named after the app', () => {
		render(
			<AppIcon
				href="/design-libraries"
				imageSrc="/design-libraries.svg"
				label="Design Libraries"
			/>
		);

		expect(
			screen.getByRole('link', {name: 'Design Libraries'})
		).toHaveAttribute('href', '/design-libraries');
	});

	it('renders the image as decoration when imageSrc is set', () => {
		const {container} = render(
			<AppIcon
				href="/design-libraries"
				imageSrc="/design-libraries.svg"
				label="Design Libraries"
				symbol="books"
			/>
		);

		const image = container.querySelector('img');

		expect(image).toHaveAttribute('alt', '');
		expect(image).toHaveAttribute('src', '/design-libraries.svg');
		expect(container.querySelector('.lexicon-icon')).toBeNull();
	});

	it('renders the icon when imageSrc is not set', () => {
		const {container} = render(
			<AppIcon
				href="/batch-planner"
				label="Batch Planner"
				symbol="import-export"
			/>
		);

		expect(
			container.querySelector('.lexicon-icon-import-export')
		).toBeInTheDocument();
		expect(container.querySelector('img')).toBeNull();
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<AppIcon
				href="/design-libraries"
				imageSrc="/design-libraries.svg"
				label="Design Libraries"
			/>
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
