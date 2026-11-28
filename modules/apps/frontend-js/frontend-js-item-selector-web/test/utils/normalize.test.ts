/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	normalizeDLItem,
	normalizeExtensions,
	parseExtensions,
} from '../../src/main/resources/META-INF/resources/utils/normalize';

describe('normalizeDLItem', () => {
	it('maps a DL item to the CMS file shape', () => {
		const result = normalizeDLItem({
			embedded: {
				contentUrl: 'http://example.com/video.mp4',
				encodingFormat: 'video/mp4',
				id: 1,
				title: 'My Video',
			},
			title: 'My Video',
		});

		expect(result.embedded.id).toBe(1);
		expect(result.embedded.title).toBe('My Video');
		expect(result.embedded.file?.mimeType).toBe('video/mp4');
		expect(result.embedded.file?.link?.href).toBe(
			'http://example.com/video.mp4'
		);
		expect(result.embedded.file?.thumbnailURL).toBe(
			'http://example.com/video.mp4'
		);
	});

	it('falls back to embedded.title then embedded.name when top-level title is empty', () => {
		const withEmbeddedTitle = normalizeDLItem({
			embedded: {id: 2, name: 'file.pdf', title: 'Embedded Title'},
			title: '',
		});

		expect(withEmbeddedTitle.title).toBe('Embedded Title');

		const withNameOnly = normalizeDLItem({
			embedded: {id: 3, name: 'file.pdf'},
			title: '',
		});

		expect(withNameOnly.title).toBe('file.pdf');
	});
});

describe('parseExtensions', () => {
	it('splits on comma and strips leading dots', () => {
		expect(parseExtensions('.jpg, .png')).toEqual(['jpg', 'png']);
	});

	it('filters out empty entries', () => {
		expect(parseExtensions(' , , ')).toEqual([]);
	});
});

describe('normalizeExtensions', () => {
	it('builds an OData filter that includes extensions and folders', () => {
		const filter = normalizeExtensions('jpg, .png');

		expect(filter).toContain("extension in ('jpg','png')");
		expect(filter).toContain(
			"objectDefinitionExternalReferenceCode eq 'L_OBJECT_ENTRY_FOLDER'"
		);
	});

	it('strips leading dots from extension names', () => {
		const filter = normalizeExtensions('.jpg,.png');

		expect(filter).toContain("extension in ('jpg','png')");
	});

	it('returns an empty string when given only whitespace or empty input', () => {
		expect(normalizeExtensions('')).toBe('');
		expect(normalizeExtensions('  , , ')).toBe('');
	});
});
