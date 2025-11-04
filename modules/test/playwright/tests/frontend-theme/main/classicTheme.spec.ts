/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

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

const CUSTOM_BACKGROUND_COLOR = 'rgb(66, 244, 197)';
const PAGE_NAME = getRandomString();
const PORTLET_NAME = 'Clay Sample';

test('Verify custom look and feel settings can be applied to page.', async ({
	apiHelpers,
	page,
	pageConfigurationPage,
	pagesAdminPage,
	site,
	widgetPagePage,
}) => {
	const layout =
		await test.step('Given a page with classic theme applied and Clay Sample portlet added', async () => {
			const layout = await apiHelpers.jsonWebServicesLayout.addLayout({
				groupId: site.id,
				options: {
					type: 'portlet',
				},
				title: PAGE_NAME,
			});

			await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyURL}`);

			await widgetPagePage.addPortlet(PORTLET_NAME);

			return layout;
		});

	await test.step('When look and feel settings and CSS is edited', async () => {
		await pagesAdminPage.goto(site.friendlyUrlPath);

		await pagesAdminPage.goToDesignTabConfiguration(PAGE_NAME);

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
			.fill(
				`body, #wrapper{background-color: ${CUSTOM_BACKGROUND_COLOR};}`
			);

		await pageConfigurationPage.save();
	});

	await test.step('Assert that the custom CSS is present', async () => {
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

	await test.step('Then assert Clay Sample portet can be minimized/maximized', async () => {
		await widgetPagePage.assertPortletOptionsAvailable(PORTLET_NAME, [
			'Maximize',
			'Minimize',
		]);

		await widgetPagePage.clickOnAction(PORTLET_NAME, 'Minimize');

		await expect(
			page.locator('div.portlet-header').getByText(PORTLET_NAME)
		).toBeVisible();

		await expect(
			page
				.locator('#main-content')
				.locator('section', {hasText: PORTLET_NAME})
				.locator('.portlet-body')
		).toBeHidden();
	});

	await test.step('Then restore Clay Sample', async () => {
		await widgetPagePage.clickOnAction(PORTLET_NAME, 'Restore');

		await expect(
			page.getByText(
				'Embedded alerts are thought to be used inside context as forms'
			)
		).toBeVisible();
	});
});
