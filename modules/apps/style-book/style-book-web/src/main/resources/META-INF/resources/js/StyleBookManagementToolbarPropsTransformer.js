/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getCheckedCheckboxes} from 'frontend-js-web';

import openDeleteStyleBookModal from './openDeleteStyleBookModal';
import openStyleBookModal from './openStyleBookModal';

export default function propsTransformer({
	additionalProps: {addStyleBookEntryURL, copyStyleBookEntryURL, exportStyleBookEntriesURL, tokenDefinitions},
	portletNamespace,
	...otherProps
}) {
	const copySelectedStyleBookEntries = () => {
		const form = document.getElementById(`${portletNamespace}fm`);

		const styleBookEntryIds = document.getElementById(
			`${portletNamespace}styleBookEntryIds`
		);

		if (!form || !styleBookEntryIds) {
			return;
		}

		styleBookEntryIds.setAttribute(
			'value',
			getCheckedCheckboxes(form, `${portletNamespace}allRowIds`)
		);

		const styleBookEntryFm = document.getElementById(
			`${portletNamespace}styleBookEntryFm`
		);

		if (styleBookEntryFm) {
			submitForm(styleBookEntryFm, copyStyleBookEntryURL);
		}
	};

	const deleteSelectedStyleBookEntries = () => {
		openDeleteStyleBookModal({
			multiple: true,
			onDelete: () => {
				const form = document.getElementById(`${portletNamespace}fm`);

				if (form) {
					submitForm(form);
				}
			},
		});
	};

	const exportSelectedStyleBookEntries = () => {
		const form = document.getElementById(`${portletNamespace}fm`);

		if (form) {
			submitForm(form, exportStyleBookEntriesURL);
		}
	};

	return {
		...otherProps,
		onActionButtonClick: (event, {item}) => {
			const action = item?.data?.action;

			if (action === 'copySelectedStyleBookEntries') {
				copySelectedStyleBookEntries();
			}
			else if (action === 'deleteSelectedStyleBookEntries') {
				deleteSelectedStyleBookEntries();
			}
			else if (action === 'exportSelectedStyleBookEntries') {
				exportSelectedStyleBookEntries();
			}
		},
		onCreateButtonClick(event, {item}) {
			const action = item?.data?.action;

			if (action === 'addStyleBookEntry') {
				openStyleBookModal({
					addStyleBookEntryURL,
					namespace: `${portletNamespace}`,
					tokenDefinitions,
				});
			}
		},
	};
}
