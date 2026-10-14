/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import VariationsSelector from '../../src/main/resources/META-INF/resources/js/style-book-editor/VariationsSelector';
import {
	StyleBookEditorContextProvider,
	useActiveVariation,
} from '../../src/main/resources/META-INF/resources/js/style-book-editor/contexts/StyleBookEditorContext';

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/config',
	() => ({
		config: {
			namespace: 'namespace',
		},
	})
);

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/saveDraft',
	() => jest.fn(() => Promise.resolve())
);

const ActiveVariation = () => (
	<span data-testid="active-variation">{useActiveVariation()}</span>
);

const renderComponent = () =>
	render(
		<StyleBookEditorContextProvider
			initialState={{
				activeVariation: 'default',
				frontendTokensValues: {},
				redoHistory: [],
				undoHistory: [],
				variations: {
					dark: {name: 'Dark', tokens: {}},
					default: {name: 'default', tokens: {}},
				},
			}}
		>
			<VariationsSelector />

			<ActiveVariation />
		</StyleBookEditorContextProvider>
	);

describe('VariationsSelector', () => {
	it('lists the default variation first and then the others', async () => {
		renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'variations'}));

		const items = screen
			.getAllByRole('menuitem')
			.map((item) => item.textContent);

		expect(items[0]).toBe('default');
		expect(items[1]).toContain('Dark');
		expect(items).toContain('create-variation');
		expect(items).not.toContain('delete-variation');
	});

	it('selects a variation and lets it be deleted', async () => {
		renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'variations'}));
		await userEvent.click(screen.getByRole('menuitem', {name: /Dark/}));

		expect(screen.getByTestId('active-variation')).toHaveTextContent(
			'dark'
		);

		await userEvent.click(screen.getByRole('button', {name: 'variations'}));

		expect(
			screen.getByRole('menuitem', {name: 'delete-variation'})
		).toBeInTheDocument();
	});
});
