/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	MultipleFileUploader,
	UploadRequestCallback,
} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React from 'react';

import {FilesUploaderComponent} from '../item_selector/ItemSelectorModal';

const DLFileUploaderComponent: FilesUploaderComponent<{
	parentObjectEntryFolderId?: number | null;
}> = function ({
	allowedExtensions,
	files,
	groupId: siteId,
	maxFileSize,
	onCloseUploadView,
	parentObjectEntryFolderId,
}) {
	const uploadRequest: UploadRequestCallback = async ({fileData}) => {
		const formData = new FormData();

		formData.append('file', fileData.file, fileData.name);
		formData.append('document', JSON.stringify({title: fileData.name}));

		const url = parentObjectEntryFolderId
			? `/o/headless-delivery/v1.0/document-folders/${parentObjectEntryFolderId}/documents`
			: `/o/headless-delivery/v1.0/sites/${siteId}/documents`;

		const response = await fetch(url, {
			body: formData,
			method: 'POST',
		});

		if (!response.ok) {
			throw new Error(`Upload failed with status ${response.status}`);
		}

		return response.json();
	};

	return (
		<MultipleFileUploader
			filesToUpload={files}
			maxFileSize={maxFileSize}
			onModalClose={onCloseUploadView}
			onUploadComplete={({failedFiles}) => {
				if (!failedFiles.length) {
					onCloseUploadView();
				}
			}}
			uploadRequest={uploadRequest}
			validExtensions={allowedExtensions}
		/>
	);
};

export default DLFileUploaderComponent;
