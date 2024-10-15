/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {
	ClayInput,
	ClaySelect
} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {fetch, navigate, objectToFormData} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useReducer, useRef, useState} from 'react';

import StyleBookField from './StyleBookField';

const DEFAULT_OPTION = {
	label: `- ${Liferay.Language.get('not-selected')} -`,
	value: '-1',
};

export default function StyleBookModal({
										  addStyleBookEntryURL,
										  frontendTokenDefinitions  = [],
										  namespace,
										  onModalClose,
									   }) {
	const {observer, onClose} = useModal({onClose: () => onModalClose()});

	const [loading, setLoading] = useState(false);

	const [errors, setErrors] = useReducer(
		(value, nextValue) => ({...value, ...nextValue}),
		{name: null, tokenDefinition: null}
	);

	const [name, setName] = useState('');
	const [frontendTokenDefinition] = useState(null);

	const formRef = useRef(null);

	const handleSubmit = (event) => {
		event.preventDefault();

		const errors = validateFields(name, frontendTokenDefinition);

		if (Object.keys(errors).length) {
			setErrors(errors);

			return;
		}

		setLoading(true);

		const body = Liferay.Util.ns(namespace, {
			infoItemClassName: frontendTokenDefinition.value
		});

		fetch(addStyleBookEntryURL, {
			body: objectToFormData(body),
			method: 'POST',
		})
			.then((response) => response.json())
			.then((responseContent) => {
				if (responseContent.error) {
					setLoading(false);
					setErrors(responseContent.error);
				}
				else if (responseContent.redirectURL) {
					navigate(responseContent.redirectURL, {
						beforeScreenFlip: onClose,
					});
				}
			})
			.catch(() =>
				setErrors({
					other: Liferay.Language.get(
						'an-unexpected-error-occurred-while-creating-the-style-book'
					),
				})
			);
	};

	const nameId = `${namespace}name`;
	const frontendTokenDefinitionId = `${namespace}tokenDefinition`;

	return (
		<ClayModal observer={observer} size="md">
			<ClayModal.Header>
				{Liferay.Language.get('add-style-book')}
			</ClayModal.Header>

			<ClayModal.Body>
				{errors.other && (
					<ClayAlert
						displayType="danger"
						onClose={() => {}}
						title={Liferay.Language.get('error')}
					>
						{errors.other}
					</ClayAlert>
				)}

				<StyleBookField
					errors={errors}
					id={frontendTokenDefinitionId}
					label={Liferay.Language.get('based-on-token-definition')}
					name="itemType"
				>
					<ClaySelect
						id={frontendTokenDefinitionId}
						onChange={(event) => {
							const value = event.target.value;

							const tokenDefinition =
								value === -1 ? null : frontendTokenDefinitions[value];

							frontendTokenDefinitions(tokenDefinition);
							setErrors({tokenDefinition: null});
						}}
					>
						<ClaySelect.Option
							label={DEFAULT_OPTION.label}
							value={DEFAULT_OPTION.value}
						/>

						{frontendTokenDefinitions.map((tokenDefinition, index) => (
							<ClaySelect.Option
								key={tokenDefinition.value}
								label={tokenDefinition.label}
								value={index}
							/>
						))}
					</ClaySelect>

					<p className="text-secondary">
						{Liferay.Language.get(
							'the-style-book-will-be-based-on-the-selected-token-definition'
						)}
					</p>
				</StyleBookField>

				<ClayForm onSubmit={handleSubmit} ref={formRef}>
					<StyleBookField
						errors={errors}
						id={nameId}
						label={Liferay.Language.get('name')}
						name="name"
					>
						<ClayInput
							id={nameId}
							onChange={(event) => {
								setName(event.target.value);

								setErrors({name: null});
							}}
							value={name}
						/>
					</StyleBookField>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={handleSubmit}
						>
							{loading && (
								<span className="inline-item inline-item-before">
									<span
										aria-hidden="true"
										className="loading-animation"
									></span>
								</span>
							)}

							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
StyleBookModal.propTypes = {
	addStyleBookEntryUrl: PropTypes.string.isRequired,
	frontendTokenDefinitions: PropTypes.object,
	namespace: PropTypes.string.isRequired,
	onModalClose: PropTypes.func,
};

const validateFields = (name, frontendTokenDefinition) => {
	const errors = {};

	const errorMessage = Liferay.Language.get('this-field-is-required');

	if (!name) {
		errors.name = errorMessage;
	}

	if (!frontendTokenDefinition) {
		errors.frontendTokenDefinitions = errorMessage;
	}

	return errors;
};
