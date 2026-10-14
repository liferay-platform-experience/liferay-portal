/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import AddVariationModalContent from '../../src/main/resources/META-INF/resources/js/style-book-editor/AddVariationModalContent';

const renderComponent = (props = {}) =>
	render(
		<AddVariationModalContent
			closeModal={jest.fn()}
			namespace="_com_liferay_style_book_web_"
			onAdd={jest.fn(() => Promise.resolve())}
			variationKeys={['default']}
			{...props}
		/>
	);

describe('AddVariationModalContent', () => {
	it('adds the first color scheme that is not used yet', async () => {
		const onAdd = jest.fn(() => Promise.resolve());

		renderComponent({onAdd, variationKeys: ['default', 'dark']});

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(onAdd).toHaveBeenCalledWith({
				key: 'dark-high-contrast',
				name: 'dark-high-contrast',
			})
		);
	});

	it('requires a name', () => {
		renderComponent();

		fireEvent.change(screen.getByLabelText('name', {exact: false}), {
			target: {value: ''},
		});

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();
		expect(screen.getByRole('button', {name: 'save'})).toBeDisabled();
	});

	it('shows the error returned when the variation cannot be saved', async () => {
		renderComponent({
			onAdd: jest.fn(() => Promise.reject(new Error('Unable to save'))),
		});

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(await screen.findByText('Unable to save')).toBeInTheDocument();
	});
});
