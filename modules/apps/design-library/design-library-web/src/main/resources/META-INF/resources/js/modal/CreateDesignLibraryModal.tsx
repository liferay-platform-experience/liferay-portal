/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {useFormik} from 'formik';
import {openToast} from 'frontend-js-components-web';
import {navigate, sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {FieldText} from '../common/components/forms';
import {
	maxLength,
	required,
	validate,
} from '../common/components/forms/validations';
import ApiHelper from '../common/services/ApiHelper';
import {
	displayErrorToast,
	displayNameInUseErrorToast,
} from '../common/utils/toastUtil';

const FDS_EVENT_UPDATE_DISPLAY = 'fds-update-display';

export default function CreateDesignLibraryModal({
	closeModal,
	dataSetId,
	redirectURL,
}: {
	closeModal: () => void;
	dataSetId: string;
	redirectURL: URL;
}) {
	const [nameInputError, setNameInputError] = useState<string>('');
	const [close, setClose] = useState(false);

	const {
		errors,
		handleBlur,
		handleChange,
		handleSubmit,
		isSubmitting,
		resetForm,
		touched,
		values,
	} = useFormik({
		initialValues: {
			designLibraryDescription: '',
			designLibraryName: '',
		},
		onSubmit: (values) => {
			const url = '/o/headless-asset-library/v1.0/asset-libraries';
			const body = {
				description: values.designLibraryDescription,
				name: values.designLibraryName,
				settings: {},
				type: 'DesignLibrary',
			};

			ApiHelper.post(url, body).then((response) => {
				const {error, status} = response;
				console.log(response);
				if (error) {
					if (status === 'CONFLICT') {
						setNameInputError(
							Liferay.Language.get(
								'please-enter-a-unique-name.-this-one-is-already-in-use'
							)
						);

						displayNameInUseErrorToast();
					}
					else if (
						error === 'Keyword name cannot be an empty string'
					) {
						setNameInputError(
							Liferay.Language.get('this-field-is-required')
						);
					}
					else {
						displayErrorToast();

						resetForm();
						setNameInputError('');

						if (close) {
							closeModal();
						}
					}

					throw new Error(
						`POST request failed to create a new Design Library with name ${body.name}`
					);
				}
				else {
					openToast({
						message: sub(
							Liferay.Language.get('x-was-created-successfully'),
							`<strong>${Liferay.Util.escapeHTML(values.designLibraryName)}</strong>`
						),
						type: 'success',
					});

					Liferay.fire(FDS_EVENT_UPDATE_DISPLAY, {id: dataSetId});

					resetForm();
					setNameInputError('');

					if (close) {
						closeModal();
					}
					console.log(redirectURL);
					navigate(redirectURL);
				}
			});
		},
		validate: (values) => {
			const errors = validate(
				{
					designLibraryName: [required, maxLength(75)],
				},
				values
			);

			return errors;
		},
	});

	const shouldDisableSaveBtn = isSubmitting || !values.designLibraryName;

	const errorMessage = sub(
		Liferay.Language.get('the-x-field-is-required'),
		Liferay.Language.get('name')
	);

	const handleNameInputErrorMessage = () => {
		if (nameInputError) {
			return nameInputError;
		}

		if (
			values.designLibraryName.length !== 0 ||
			!touched.designLibraryName ||
			!values.designLibraryName.trim().length
		) {
			return errors.designLibraryName;
		}

		return errorMessage;
	};

	return (
		<ClayForm onSubmit={handleSubmit}>
			<div className="categorization-modal">
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('new-design-library')}
				</ClayModal.Header>

				<ClayModal.Body>
					<FieldText
						errorMessage={handleNameInputErrorMessage()}
						label={Liferay.Language.get('name')}
						name="designLibraryName"
						onBlur={handleBlur}
						onChange={(event) => {
							setNameInputError('');
							handleChange(event);
						}}
						required
						value={values.designLibraryName}
					/>

					<FieldText
						component="textarea"
						label={Liferay.Language.get('description')}
						name="designLibraryDescription"
						onBlur={handleBlur}
						onChange={(event) => {
							handleChange(event);
						}}
						value={values.designLibraryDescription}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={closeModal}
								type="button"
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={shouldDisableSaveBtn}
								displayType="primary"
								onClick={() => setClose(true)}
								type="submit"
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</div>
		</ClayForm>
	);
}
