/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {FieldBase} from 'frontend-js-components-web';
import {fetch, navigate, objectToFormData} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

type frontendTokenDefinitionProvider = {
	name: string;
	themeId: string;
};

interface AddStyleBookModalProps {
	addStyleBookEntryURL: string;
	closeModal: () => void;
	frontendTokenDefinitionProviders?: Array<frontendTokenDefinitionProvider>;
	namespace: string;
}

const AddStyleBookModalContent = ({
	addStyleBookEntryURL,
	closeModal,
	frontendTokenDefinitionProviders = [],
	namespace,
}: AddStyleBookModalProps) =>{

	const [loading, setLoading] = useState<boolean>(false);

	const [errorMessage, setErrorMessage] = useState<string>();

	const [name, setName] = useState<string>('');
	const [themeId, setThemeId] =
		useState(frontendTokenDefinitionProviders[0]);

	const formRef = useRef(null);

	const handleFormError = (responseContent: any) => {
		setErrorMessage(responseContent.error || '');
	};

	const handleSubmit = (event: any) => {
		event.preventDefault();

		const error = name.trim().length
			? ''
			: Liferay.Language.get('this-field-is-required');

		if (error) {
			setErrorMessage(error);

			return;
		}

		setLoading(true);

		const body = Liferay.Util.ns(namespace, {
			name,
			themeId: themeId.themeId,
		});

		fetch(addStyleBookEntryURL, {
			body: objectToFormData(body),
			method: 'POST',
		})
			.then((response) => response.json())
			.then((responseContent) => {
				if (responseContent.error) {
					setLoading(false);
					setErrorMessage(responseContent.error);
				}
				else if (responseContent.redirectURL) {
					navigate(responseContent.redirectURL, {
						beforeScreenFlip: closeModal,
					});
				}
			})
			.catch((response) =>
				handleFormError(response)
			);
	};

	const nameId = `${namespace}name`;
	const themeIdId = `${namespace}tokenDefinition`;

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('add-style-book')}
			</ClayModal.Header>

			<ClayModal.Body>

				{errorMessage && (
					<ClayAlert
						displayType="danger"
						onClose={() => {}}
						title={Liferay.Language.get('error')}
					>
						{errorMessage}
					</ClayAlert>
				)}

				<FieldBase
					className="themeId"
					helpMessage={Liferay.Language.get(
						'the-style-book-will-be-created-based-on-the-selected-token-definition'
					)}
					id={themeIdId}
					label={Liferay.Language.get('create-style-book-for')}
				>
					<ClaySelect
						id={themeIdId}
						onChange={(event) => {
							const value = event.target.value;

							setThemeId(frontendTokenDefinitionProviders[value]);
						}}
					>
						{frontendTokenDefinitionProviders.map(
							(frontendTokenDefinitionProvider, index) => (
								<ClaySelect.Option
									key={frontendTokenDefinitionProvider.themeId}
									label={frontendTokenDefinitionProvider.name}
									value={index}
								/>
							)
						)}
					</ClaySelect>
				</FieldBase>

				<ClayForm onSubmit={handleSubmit} ref={formRef}>
					<div
						className={`form-group ${
							errorMessage ? 'has-error' : ''
						}`}
					>
						<label
							className="control-label"
							htmlFor={`${namespace}${nameId}`}
						>
							{Liferay.Language.get('name')}

							<span className="reference-mark">
								<ClayIcon symbol="asterisk"/>
							</span>
						</label>

						<ClayInput
							id={nameId}
							onChange={(event) => {
								setName(event.target.value);

								setErrorMessage(
									event.target.value
										? ''
										: Liferay.Language.get(
											'this-field-is-required'
										)
								);
							}}
							value={name}
						/>
					</div>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={closeModal}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={handleSubmit}
						>
							{loading && (
								<span
									className="inline-item inline-item-before">
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
		</>
	);
}

export default AddStyleBookModalContent;
