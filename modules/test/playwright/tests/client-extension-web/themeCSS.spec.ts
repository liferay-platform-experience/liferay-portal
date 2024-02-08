/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import * as path from 'path';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {knowledgeBasePages} from '../../fixtures/knowledgeBasePages';
import {loginTest} from '../../fixtures/loginTest';
import getRandomId from '../../utils/getRandomId';
import {clientExtensionsPageTest} from './fixtures/clientExtensionsPageTest';
import {ClientExtensionsPage} from './pages/ClientExtensionsPage';

export const test = mergeTests(
	apiHelpersTest,
	clientExtensionsPageTest,
	loginTest,
	knowledgeBasePages
);

const uploadAndValidateFile = async (
	clientExtensionsPage: ClientExtensionsPage,
	fileName: string,
	message: string,
	page: Page
) => {
	const fileChooserPromise = page.waitForEvent('filechooser');

	clientExtensionsPage.themeCSSFrontendTokenDefinitionSelectFileButton.click();

	const fileChooser = await fileChooserPromise;

	await fileChooser.setFiles(
		path.join(__dirname, '/dependencies/' + fileName)
	);

	await expect(page.getByText(message)).toBeVisible();
};

test('ThemeCSS Client Extension supports Frontend Token Definition file upload', async ({
	apiHelpers,
	clientExtensionsPage,
	page,
}) => {
	await apiHelpers.featureFlag.updateFeatureFlag('LPD-10773', true);

	await clientExtensionsPage.goto();

	await clientExtensionsPage.newClientExtensionButton.click();

	await clientExtensionsPage.addThemeCSSMenuItem.click();

	await uploadAndValidateFile(
		clientExtensionsPage,
		'invalid-frontend-token-definition.json',
		'The format is not valid. Please upload a valid Frontend Token Definition JSON file.',
		page
	);

	await uploadAndValidateFile(
		clientExtensionsPage,
		'empty-frontend-token-definition.json',
		'Frontend Token Definition uploaded. Contributing 0 Token Categories, 0 Token Sets, and 0 Tokens.',
		page
	);

	await uploadAndValidateFile(
		clientExtensionsPage,
		'empty-json-file.json',
		'Frontend Token Definition uploaded. Contributing 0 Token Categories, 0 Token Sets, and 0 Tokens.',
		page
	);

	await uploadAndValidateFile(
		clientExtensionsPage,
		'frontend-token-definition.json',
		'Frontend Token Definition uploaded. Contributing 1 Token Categories, 1 Token Sets, and 2 Tokens.',
		page
	);
});

test('ThemeCSS Client Extension Frontend Token Definition tokens appears on new stylebook', async ({
	apiHelpers,
	clientExtensionsPage,
	knowledgeBasePage: {productMenuPage},
	page,
}) => {
	await apiHelpers.featureFlag.updateFeatureFlag('LPD-10773', true);

	// Create Theme CSS Client Extension

	await clientExtensionsPage.goto();

	await clientExtensionsPage.newClientExtensionButton.click();

	await clientExtensionsPage.addThemeCSSMenuItem.click();

	const clientExtensionName = getRandomId();

	await page
		.locator(
			'#_com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet_name'
		)
		.fill(clientExtensionName);

	await uploadAndValidateFile(
		clientExtensionsPage,
		'frontend-token-definition.json',
		'Frontend Token Definition uploaded. Contributing 1 Token Categories, 1 Token Sets, and 2 Tokens.',
		page
	);

	// Save Client Extension

	await page.click(
		'#_com_liferay_client_extension_web_internal_portlet_ClientExtensionAdminPortlet_editClientExtensionEntrySubmitButton'
	);

	// Navigate to Pages in Site Builder

	await productMenuPage.goToPagesMenuItem();

	await page
		.locator(
			`//*[@id="_com_liferay_layout_admin_web_portlet_GroupPagesPortlet_controlMenu"]/div[4]/ul/li[1]/div/div[1]/button`
		)
		.click();

	await page
		.locator('.dropdown-item')
		.and(page.getByText('Configuration'))
		.click();

	await page
		.locator(
			'#_com_liferay_layout_admin_web_portlet_GroupPagesPortlet_themeCSSReplacementExtension'
		)
		.click();

	await page.waitForSelector('#selectThemeCSSClientExtension_iframe_', {
		state: 'visible',
	});

	// Wait for the Select Theme CSS Client Extension iFrame to load and set the click events.

	await page.waitForTimeout(1000);

	await page
		.frameLocator('#selectThemeCSSClientExtension_iframe_')
		.getByText(clientExtensionName)
		.click();

	await page.waitForSelector('#selectThemeCSSClientExtension_iframe_', {
		state: 'hidden',
	});

	await page.getByText('Save', {exact: true}).click();

	// Navigate to Style Books in Design

	await productMenuPage.goToStyleBooksMenuItem();

	await page.getByText('New', {exact: true}).click();

	const styleBookName = getRandomId();

	await page.fill(
		'#_com_liferay_style_book_web_internal_portlet_StyleBookPortlet_name',
		styleBookName
	);

	await page.getByText('Save').click();

	// Assert the Token Set defined on the Frontend Token Definition file is available for the theme

	await expect(page.getByText('primary-buttons')).toBeVisible();

	// Delete Style Book

	await page.getByText('Go to Style Books').click();

	await page.getByPlaceholder('Search for').fill(styleBookName);

	await page.keyboard.press('Enter');

	await page.waitForLoadState('domcontentloaded');

	await page.getByLabel('More actions').click();

	await page.getByRole('menuitem', {name: 'Delete'}).click();

	await page.getByText('Delete').and(page.locator('.btn.btn-danger')).click();

	// Delete Theme CSS Client Extension

	await clientExtensionsPage.goto();

	await page.getByPlaceholder('Search').fill(clientExtensionName);

	await page.keyboard.press('Enter');

	await page.waitForLoadState('domcontentloaded');

	await page.getByText('Actions').click();

	page.on('dialog', (dialog) => dialog.accept());

	await page.getByRole('menuitem', {name: 'Delete'}).click();
});
