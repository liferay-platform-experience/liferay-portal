/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

import {Site} from '../item_selector/types';

let sitesPromise: Promise<Site[]> | null = null;

export function clearSitesCache(): void {
	sitesPromise = null;
}

export function fetchSites(): Promise<Site[]> {
	if (!sitesPromise) {
		sitesPromise = doFetchSites();
	}

	return sitesPromise;
}

async function doFetchSites(): Promise<Site[]> {
	try {
		const companyGroupId = Number(
			(
				Liferay.ThemeDisplay as unknown as {
					getCompanyGroupId: () => number;
				}
			).getCompanyGroupId()
		);

		const response = await fetch(
			'/o/headless-admin-user/v1.0/my-user-account/sites'
		);

		const data: Record<string, unknown> | null = response.ok
			? await response.json()
			: null;

		return [
			...(
				(data?.items as Array<{
					descriptiveName?: string;
					id: number;
					name: string;
				}>) ?? []
			)
				.filter((site) => site.id !== companyGroupId)
				.map((site) => ({
					id: site.id,
					kind: 'site' as const,
					name: site.descriptiveName || site.name,
				})),
			{
				id: companyGroupId,
				kind: 'global' as const,
				name: Liferay.Language.get('global'),
			},
		];
	}
	catch {
		return [];
	}
}
