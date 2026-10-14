/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Option, Picker} from '@clayui/core';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {FieldBase} from 'frontend-js-components-web';
import React, {useState} from 'react';

const COLOR_SCHEME_CUSTOM = 'custom';

const COLOR_SCHEMES = [
	{key: 'dark', label: Liferay.Language.get('dark')},
	{
		key: 'dark-high-contrast',
		label: Liferay.Language.get('dark-high-contrast'),
	},
	{key: 'light', label: Liferay.Language.get('light')},
	{
		key: 'light-high-contrast',
		label: Liferay.Language.get('light-high-contrast'),
	},
	{key: COLOR_SCHEME_CUSTOM, label: Liferay.Language.get('custom')},
];

const KEY_REGEXP = /^[a-z0-9]+(-[a-z0-9]+)*$/;

const PICKER_MESSAGES = {
	itemDescribedby: Liferay.Language.get(
		'you-are-currently-on-a-text-element,-inside-of-a-list-box'
	),
	itemSelected: Liferay.Language.get('x-selected'),
	scrollToBottomAriaLabel: Liferay.Language.get('scroll-to-bottom'),
	scrollToTopAriaLabel: Liferay.Language.get('scroll-to-top'),
};

interface Variation {
	key: string;
	name: string;
}

interface AddVariationModalContentProps {
	closeModal: () => void;
	namespace: string;
	onAdd: (variation: Variation) => Promise<unknown>;
	variationKeys: string[];
}

const getLabel = (colorScheme: React.Key) =>
	COLOR_SCHEMES.find((item) => item.key === colorScheme)?.label ?? '';

export default function AddVariationModalContent({
	closeModal,
	namespace,
	onAdd,
	variationKeys,
}: AddVariationModalContentProps) {
	const colorSchemes = COLOR_SCHEMES.filter(
		({key}) => key === COLOR_SCHEME_CUSTOM || !variationKeys.includes(key)
	);

	const [colorScheme, setColorScheme] = useState<React.Key>(
		colorSchemes[0].key
	);
	const [customKey, setCustomKey] = useState<string>('');
	const [customKeyErrorMessage, setCustomKeyErrorMessage] =
		useState<string>('');
	const [loading, setLoading] = useState(false);
	const [name, setName] = useState<string>(getLabel(colorSchemes[0].key));
	const [nameErrorMessage, setNameErrorMessage] = useState<string>('');

	const validateCustomKey = (customKey: string) => {
		let errorMessage = '';

		if (colorScheme === COLOR_SCHEME_CUSTOM) {
			if (!customKey) {
				errorMessage = Liferay.Language.get('this-field-is-required');
			}
			else if (!KEY_REGEXP.test(customKey)) {
				errorMessage = Liferay.Language.get(
					'the-key-must-contain-only-lowercase-letters,-numbers-and-hyphens'
				);
			}
			else if (variationKeys.includes(customKey)) {
				errorMessage = Liferay.Language.get(
					'a-variation-with-this-key-already-exists'
				);
			}
		}

		setCustomKeyErrorMessage(errorMessage);

		return errorMessage;
	};

	const validateName = (name: string) => {
		const errorMessage = !name.trim()
			? Liferay.Language.get('this-field-is-required')
			: '';

		setNameErrorMessage(errorMessage);

		return errorMessage;
	};

	const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();

		if (validateName(name) || validateCustomKey(customKey)) {
			return;
		}

		setLoading(true);

		onAdd({
			key:
				colorScheme === COLOR_SCHEME_CUSTOM
					? customKey
					: String(colorScheme),
			name: name.trim(),
		}).catch((error) => {
			setNameErrorMessage(
				error?.message ||
					Liferay.Language.get('an-unexpected-error-occurred')
			);
			setLoading(false);
		});
	};

	const colorSchemeId = `${namespace}variationColorScheme`;
	const customKeyId = `${namespace}variationKey`;
	const formId = `${namespace}variationForm`;
	const nameId = `${namespace}variationName`;

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('create-variation')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p className="text-secondary">
					{Liferay.Language.get(
						'a-variation-only-stores-the-tokens-you-change.-the-rest-are-inherited-from-the-default-variation'
					)}
				</p>

				<ClayForm id={formId} onSubmit={handleSubmit}>
					<FieldBase
						id={colorSchemeId}
						label={Liferay.Language.get('color-scheme')}
					>
						<Picker
							id={colorSchemeId}
							items={colorSchemes}
							messages={PICKER_MESSAGES}
							onSelectionChange={(key: React.Key) => {
								setColorScheme(key);
								setCustomKeyErrorMessage('');

								if (key !== COLOR_SCHEME_CUSTOM) {
									setName(getLabel(key));
								}
							}}
							selectedKey={colorScheme}
						>
							{(item) => (
								<Option key={item.key} textValue={item.label}>
									{item.label}
								</Option>
							)}
						</Picker>
					</FieldBase>

					{colorScheme === COLOR_SCHEME_CUSTOM && (
						<FieldBase
							errorMessage={customKeyErrorMessage}
							helpMessage={Liferay.Language.get(
								'the-key-is-the-value-of-the-data-color-scheme-attribute-that-activates-this-variation'
							)}
							id={customKeyId}
							label={Liferay.Language.get('key')}
							required
						>
							<ClayInput
								id={customKeyId}
								onChange={(event) => {
									const customKey = event.target.value;

									setCustomKey(customKey);
									validateCustomKey(customKey);
								}}
								value={customKey}
							/>
						</FieldBase>
					)}

					<FieldBase
						className="mb-0"
						errorMessage={nameErrorMessage}
						id={nameId}
						label={Liferay.Language.get('name')}
						required
					>
						<ClayInput
							id={nameId}
							onChange={(event) => {
								const name = event.target.value;

								setName(name);
								validateName(name);
							}}
							value={name}
						/>
					</FieldBase>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							aria-busy={loading}
							disabled={Boolean(
								loading ||
									nameErrorMessage ||
									customKeyErrorMessage
							)}
							displayType="primary"
							form={formId}
							type="submit"
						>
							{loading && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation loading-animation-sm"
									/>
								</span>
							)}

							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
