/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageViewModePagesTest} from '../../../fixtures/pageViewModePagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import getRandomString from '../../../utils/getRandomString';
import {pagesPagesTest} from '../../layout-admin-web/main/fixtures/pagesPagesTest';

export const test = mergeTests(
	apiHelpersTest,
	isolatedSiteTest,
	pageEditorPagesTest,
	loginTest(),
	pagesPagesTest,
	pagesAdminPagesTest,
	pageViewModePagesTest
);

const PAGE_NAME = getRandomString();
const PORTLET_NAME = 'Clay Sample';
const CUSTOM_BACKGROUND_COLOR = 'rgb(66, 244, 197)'

const goToPortletOptions = async ({page}: {page: Page}) => {
	await page
		.locator(`header[class*="portlet-topper"]:has-text("${PORTLET_NAME}")`)
		.locator('button:has(svg[class*="icon-ellipsis-v"])')
		.click();
};

test(
	'Verify custom look and feel Settings can be applied to page.',
	{tag: '@LPD-70289'},
	async ({
		apiHelpers,
		page,
		pageConfigurationPage,
		pagesAdminPage,
		site,
		widgetPagePage,
	}) => {
		const layout =
			await test.step('Given a page with classic theme applied and Clay Sample portlet added', async () => {
				const layout = await apiHelpers.jsonWebServicesLayout.addLayout(
					{
						groupId: site.id,
						options: {
							type: 'portlet',
						},
						title: PAGE_NAME,
					}
				);

				await page.goto(
					`/web${site.friendlyUrlPath}${layout.friendlyURL}`
				);

				await widgetPagePage.addPortlet(PORTLET_NAME);

				return layout;
			});

		await test.step('Given access the page design', async () => {
			await pagesAdminPage.goto(site.friendlyUrlPath);

			await pagesAdminPage.goToDesignTabConfiguration(PAGE_NAME);
		});

		await test.step('When Look and Feel/CSS is edited', async () => {
			await page.getByText('Define a custom theme for this page').click();

			await page
				.getByRole('checkbox', {
					exact: true,
					name: 'Show Header Search',
				})
				.uncheck();

			await page
				.getByRole('checkbox', {
					exact: true,
					name: 'Show Maximize/Minimize Application Links',
				})
				.check();

			await page
				.getByRole('textbox', {exact: true, name: 'CSS'})
				.fill(`body, #wrapper{background-color: ${CUSTOM_BACKGROUND_COLOR};}`);

			await pageConfigurationPage.save();
		});

		await test.step('Assert that the Custom CSS is present', async () => {
			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await expect(page.locator('body')).toHaveCSS(
				'background-color',
				CUSTOM_BACKGROUND_COLOR
			);

			await expect(page.locator('#wrapper')).toHaveCSS(
				'background-color',
				CUSTOM_BACKGROUND_COLOR
			);
		});

		await test.step('And go to Clay Sample portlet page', async () => {
			await goToPortletOptions({page});

			const dropdown = page.locator('.dropdown-menu.show');

			await expect(dropdown.getByText('Maximize')).toBeVisible();

			await expect(dropdown.getByText('Minimize')).toBeVisible();

			await dropdown.getByText('Minimize').click();
		});

		await test.step('Then assert Clay Sample portet can be minimized/maximized', async () => {
			await expect(
				page.locator('div.portlet-header').getByText(PORTLET_NAME)
			).toBeVisible();

			await expect(
				page
					.locator(`#main-content`)
					.locator(`section:has-text("${PORTLET_NAME}")`)
					.locator('.portlet-body')
			).not.toBeVisible();
		});

		await test.step('Then restore Clay Sample', async () => {
			await goToPortletOptions({page});

			const portletContent = page
				.locator('#content')
				.locator('.portlet-content');

			const dropdown = page.locator('.dropdown-menu.show');

			await expect(dropdown.getByText('Restore')).toBeVisible();

			await dropdown.getByText('Restore').click();

			await expect(
				portletContent.getByText(
					'Embedded alerts are thought to be used inside context as forms'
				)
			).toBeVisible();
		});
	}
);
