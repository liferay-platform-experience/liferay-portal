/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';
import path from 'path';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {styleBookPageTest} from '../../../fixtures/styleBookPageTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {getTempDir} from '../../../utils/temp';
import {waitForSPAToBeLoaded} from '../../../utils/waitForSPAToBeLoaded';

const test = mergeTests(
	apiHelpersTest,
	loginTest(),
	isolatedSiteTest,
	styleBookPageTest
);

test(
	'Assert that the style books can be exported and imported by the user.',
	{tag: '@LPS-134860'},
	async ({page, site, styleBooksPage}) => {
		const styleBookName = getRandomString();

		await test.step('Create a style book with times as Font Family Base', async () => {
			await styleBooksPage.goto();

			await styleBooksPage.create(styleBookName);

			await styleBooksPage.selectTokenCategory('Typography');

			await styleBooksPage.updateTokenInput(
				'Font Family Base',
				'times',
				'Font Family'
			);

			await styleBooksPage.waitForAutoSave();

			await styleBooksPage.publish();
		});

		const {fileName, filePath} =
			await test.step('Export the style book', async () => {
				const downloadPromise = page.waitForEvent('download');

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('menuitem', {name: 'Export'}),
					trigger: page.getByRole('button', {name: 'Actions'}),
				});

				const download = await downloadPromise;

				const filePath = path.join(
					getTempDir(),
					download.suggestedFilename()
				);

				await download.saveAs(filePath);

				return {fileName: download.suggestedFilename(), filePath};
			});

		await test.step('Import the style book into a new site', async () => {
			await styleBooksPage.goto(site.friendlyUrlPath);

			await waitForSPAToBeLoaded(page);

			await styleBooksPage.importStyleBookFile(fileName, filePath);
		});

		await test.step('Check that the Font Family is times', async () => {
			await styleBooksPage.goto(site.friendlyUrlPath);

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {name: 'Edit'}),
				trigger: page.getByRole('button', {name: 'Actions'}),
			});

			await styleBooksPage.selectTokenCategory('Typography');

			await styleBooksPage.assertTokenInputValue(
				'Font Family Base',
				'times',
				'Font Family'
			);
		});
	}
);
