/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import Sidebar from '../../src/main/resources/META-INF/resources/js/style-book-editor/Sidebar';
import {StyleBookEditorContextProvider} from '../../src/main/resources/META-INF/resources/js/style-book-editor/contexts/StyleBookEditorContext';

jest.mock(
	'../../src/main/resources/META-INF/resources/js/style-book-editor/config',
	() => ({
		config: {
			frontendTokenDefinitions: [
				{
					frontendTokenCategories: [
						{
							frontendTokenSets: [
								{
									frontendTokens: [
										{
											defaultValue: '#000',
											label: 'Token 1',
											mappings: [
												{
													type: 'cssVariable',
													value: 'token-1',
												},
											],
											name: 'token1',
											type: 'color',
										},
										{
											defaultValue: '#fff',
											editorType: 'ColorPicker',
											label: 'Button Link Color',
											mappings: [
												{
													type: 'cssVariable',
													value: 'btn-link-color',
												},
											],
											name: 'btnLinkColor',
											type: 'color',
										},
									],
									label: 'Set 1',
									name: 'set1',
								},
							],
							label: 'Category 1',
							name: 'category1',
						},
					],
					id: 'theme',
					name: 'Theme Tokens',
				},
				{
					frontendTokenCategories: [
						{
							frontendTokenSets: [
								{
									frontendTokens: [
										{
											defaultValue: '#fff',
											label: 'Clay Token',
											mappings: [
												{
													type: 'cssVariable',
													value: 'clay-token',
												},
											],
											name: 'clayToken',
											type: 'color',
										},
									],
									label: 'Clay Set',
									name: 'claySet',
								},
							],
							label: 'Clay Category',
							name: 'clayCategory',
						},
					],
					id: 'clay',
					name: 'Clay Tokens',
				},
			],
			frontendTokens: {
				'clay:clayToken': {
					defaultValue: '#fff',
					label: 'Clay Token',
					mappings: [{type: 'cssVariable', value: 'clay-token'}],
					name: 'clay:clayToken',
					type: 'color',
				},
				'theme:brandColor1': {
					defaultValue: '#fff',
					editorType: 'ColorPicker',
					label: 'Brand Color 1',
					mappings: [{type: 'cssVariable', value: 'brand-color-1'}],
					name: 'theme:brandColor1',
					tokenCategoryLabel: 'Category 1',
					tokenSetLabel: 'Set 1',
					type: 'color',
				},
				'theme:btnLinkColor': {
					defaultValue: '#fff',
					editorType: 'ColorPicker',
					label: 'Button Link Color',
					mappings: [{type: 'cssVariable', value: 'btn-link-color'}],
					name: 'theme:btnLinkColor',
					tokenCategoryLabel: 'Category 1',
					tokenSetLabel: 'Set 1',
					type: 'color',
				},
				'theme:token1': {
					defaultValue: '#000',
					label: 'Token 1',
					mappings: [{type: 'cssVariable', value: 'token-1'}],
					name: 'theme:token1',
					type: 'color',
				},
			},
			namespace: '_namespace_',
			saveDraftURL: '/save-draft',
			sortFrontendTokenValues: (frontendTokensValues) =>
				Object.values(frontendTokensValues),
			themeFrontendTokenDefinitionId: 'theme',
			themeName: 'Classic',
		},
	})
);

const renderComponent = ({frontendTokensValues = {}} = {}) => {
	render(
		<StyleBookEditorContextProvider
			initialState={{
				frontendTokensValues,
			}}
		>
			<Sidebar />
		</StyleBookEditorContextProvider>
	);
};

describe('Sidebar', () => {
	it('renders Sidebar with definition selector when multiple definitions are present', () => {
		renderComponent();

		expect(
			screen.getByText('frontend-token-definition-provided-by')
		).toBeInTheDocument();
		expect(screen.getAllByText('Classic')[0]).toBeInTheDocument();
	});

	it('switches between definitions using the dropdown', () => {
		renderComponent();

		const triggers = screen.getAllByText('Classic');
		fireEvent.click(triggers[0]);

		const clayOption = screen.getByText('Clay Tokens');
		fireEvent.click(clayOption);

		expect(screen.getAllByText('Clay Tokens')[0]).toBeInTheDocument();
		expect(screen.getAllByText('Clay Category')[0]).toBeInTheDocument();
		expect(screen.getByText('Clay Set')).toBeInTheDocument();
		expect(screen.getByText('Clay Token')).toBeInTheDocument();
	});

	it('resets selected category when switching definitions', () => {
		renderComponent();

		// Initially in Classic, Category 1 is selected

		expect(screen.getAllByText('Category 1')[0]).toBeInTheDocument();

		// Switch to Clay Tokens

		fireEvent.click(screen.getAllByText('Classic')[0]);
		fireEvent.click(screen.getByText('Clay Tokens'));

		// Should show Clay Category now

		expect(screen.getAllByText('Clay Category')[0]).toBeInTheDocument();
		expect(screen.queryByText('Category 1')).not.toBeInTheDocument();
	});

	it('links a token to another token through its CSS variable', async () => {
		fetch.mockResponseOnce(JSON.stringify({}));

		renderComponent({
			frontendTokensValues: {
				'theme:brandColor1': {
					cssVariableMapping: 'brand-color-1',
					tokenDefinitionId: 'theme',
					value: '#ff0000',
				},
			},
		});

		const buttonLinkColor = screen.getByLabelText('Button Link Color');

		await userEvent.click(
			within(buttonLinkColor).getByLabelText('select-color')
		);
		await userEvent.click(screen.getByText('value-from-stylebook'));
		await userEvent.click(screen.getByTitle('Brand Color 1'));

		await waitFor(() => expect(fetch).toHaveBeenCalled());

		const [, {body}] = fetch.mock.calls[0];

		expect(
			JSON.parse(body.get('_namespace_frontendTokensValues'))[
				'theme:btnLinkColor'
			]
		).toEqual({
			cssVariableMapping: 'btn-link-color',
			name: 'theme:brandColor1',
			tokenDefinitionId: 'theme',
			value: 'var(--brand-color-1)',
		});
	});

	it('shows the color at the end of a token link chain', () => {
		renderComponent({
			frontendTokensValues: {
				'theme:brandColor1': {
					cssVariableMapping: 'brand-color-1',
					name: 'theme:token1',
					tokenDefinitionId: 'theme',
					value: 'var(--token-1)',
				},
				'theme:btnLinkColor': {
					cssVariableMapping: 'btn-link-color',
					name: 'theme:brandColor1',
					tokenDefinitionId: 'theme',
					value: 'var(--brand-color-1)',
				},
				'theme:token1': {
					cssVariableMapping: 'token-1',
					tokenDefinitionId: 'theme',
					value: '#00ff00',
				},
			},
		});

		const tokenButton = within(
			screen.getByLabelText('Button Link Color')
		).getByRole('button', {name: /select-color/});

		expect(tokenButton).toHaveTextContent('Brand Color 1');
		expect(tokenButton.firstChild).toHaveStyle({background: '#00ff00'});
	});
});
