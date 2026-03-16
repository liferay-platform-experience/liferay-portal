/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {createPortletURL} from 'frontend-js-web';

import {openDesignLibraryModal} from '../common/utils/openDesignLibraryModal';
import CreateDesignLibraryModal from '../modal/CreateDesignLibraryModal';

export default function DesignLibraryAdminFDSPropsTransformer({
	additionalProps,
	id,
	...props
}: {
	additionalProps: {
		redirectURL: string;
	};
	id: string;
	props: Record<string, unknown>;
}) {
	const redirectURL = createPortletURL(additionalProps.redirectURL, {
		entryId: '40028922',
		name: '/view',
	});

	console.log({...props, additionalProps, id, redirectURL});

	const creationMenu = {
		primaryItems: [
			{
				label: Liferay.Language.get('new-design-library'),
				onClick: () => {
					openDesignLibraryModal({
						contentComponent: ({
							closeModal,
						}: {
							closeModal: () => void;
						}) =>
							CreateDesignLibraryModal({
								closeModal,
								dataSetId: id,
								redirectURL,
							}),
						size: 'md',
					});
				},
			},
		],
	};

	return {
		...props,
		creationMenu,
		hideManagementBarInEmptyState: true,
		views: [
			{
				contentRenderer: 'table',
				label: 'Table',
				name: 'table',
				schema: {
					fields: [
						{
							actionId: 'edit',
							contentRenderer: 'actionLink',
							fieldName: 'name',
							label: Liferay.Language.get('title'),
							localizeLabel: true,
							sortable: true,
						},
						{
							fieldName: 'creatorUserId',
							label: Liferay.Language.get('author'),
							localizeLabel: true,
							truncate: true,
						},
						{
							contentRenderer: 'dateTime',
							fieldName: 'dateModified',
							label: Liferay.Language.get('last-updated'),
							localizeLabel: true,
							sortable: true,
						},
					],
				},
				thumbnail: 'table',
			},
		],
	};
}
