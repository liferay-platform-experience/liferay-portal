/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CMSFile, DLFile} from '../item_selector/types';

export function normalizeDLItem(item: DLFile): CMSFile {
	const title = item.title || item.embedded.title || item.embedded.name || '';

	return {
		description: '',
		embedded: {
			file: {
				link: {href: item.embedded.contentUrl ?? ''},
				mimeType: item.embedded.encodingFormat ?? '',
				thumbnailURL: item.embedded.contentUrl ?? '',
			},
			id: item.embedded.id,
			title,
		},
		title,
	};
}

export function parseExtensions(allowedExtensions: string): string[] {
	return allowedExtensions
		.split(',')
		.map((item) => item.trim().replace(/^\./, ''))
		.filter(Boolean);
}

export function normalizeExtensions(allowedExtensions: string) {
	const cleanExtensions = parseExtensions(allowedExtensions);

	if (cleanExtensions.length < 1) {
		return '';
	}

	const extensions = cleanExtensions.map((item) => `'${item}'`).join(',');

	return `(extension in (${extensions}) or objectDefinitionExternalReferenceCode eq 'L_OBJECT_ENTRY_FOLDER')`;
}
