/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-web';

import AddStyleBookModalContent from './AddStyleBookModalContent';

export default function openAddStyleBookModal({
	addStyleBookEntryURL,
	frontendTokenDefinitionProviders,
	namespace,
}: {
	addStyleBookEntryURL: string;
	frontendTokenDefinitionProviders: any;
	namespace: string;
}) {
	openModal({
		contentComponent: ({
			closeModal,
		}: {
			closeModal: () => void;
		}) =>  AddStyleBookModalContent({
			addStyleBookEntryURL,
			closeModal,
			frontendTokenDefinitionProviders,
			namespace,
		}),
	});
}
