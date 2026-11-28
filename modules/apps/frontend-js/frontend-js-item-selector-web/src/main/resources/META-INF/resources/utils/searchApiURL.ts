/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const DL_FILE_ENTRY_CLASS_NAME =
	'com.liferay.document.library.kernel.model.DLFileEntry';

export function buildSpacesApiURL({
	filters = [],
	folderId,
}: {
	filters?: string[];
	folderId?: number | null;
}) {
	const url = new URL('/o/search/v1.0/search', location.origin);

	const scopePredicates = folderId
		? [`(folderId eq ${folderId})`]
		: [
				'(cmsRoot eq true)',
				"(cmsSection eq 'files')",
				'(rootDescendantNode eq false)',
			];

	const filter = [...scopePredicates, '(status in (0, 2, 3))']
		.concat(filters.filter(Boolean))
		.join(' and ');

	url.search = new URLSearchParams({
		emptySearch: 'true',
		filter,
		nestedFields: 'description,embedded,file.mimeType,file.thumbnailURL',
	}).toString();

	return url.toString();
}

export function buildDLApiURL(extensions: string[], siteId: number): string {
	const url = new URL('/o/search/v1.0/search', location.origin);

	const predicates = [`(scopeGroupId eq ${siteId})`, '(status eq 0)'];

	if (extensions.length) {
		const extList = extensions.map((ext) => `'${ext}'`).join(',');

		predicates.push(`(extension in (${extList}))`);
	}

	url.search = new URLSearchParams({
		emptySearch: 'true',
		entryClassNames: DL_FILE_ENTRY_CLASS_NAME,
		filter: predicates.join(' and '),
		nestedFields: 'embedded',
	}).toString();

	return url.toString();
}
