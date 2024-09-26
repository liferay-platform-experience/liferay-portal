/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class AddUserPage {
	readonly addUserAlert: Locator;
	readonly addUserEmailAddressInput: Locator;
	readonly addUserFirstNameInput: Locator;
	readonly addUserLastNameInput: Locator;
	readonly addUserSaveButton: Locator;
	readonly addUserScreenNameInput: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly creationMenuNewButton: Locator;
	readonly page: Page;

	constructor(page: Page) {
		this.addUserAlert = page.locator('div.alert');
		this.addUserEmailAddressInput = page.locator('[id="_com_liferay_users_admin_web_portlet_UsersAdminPortlet_emailAddress"]');
		this.addUserFirstNameInput = page.locator('[id="_com_liferay_users_admin_web_portlet_UsersAdminPortlet_firstName"]');
		this.addUserLastNameInput = page.locator('[id="_com_liferay_users_admin_web_portlet_UsersAdminPortlet_lastName"]');
		this.addUserSaveButton = page.locator('button.btn-primary').filter({hasText: 'Save'});
		this.addUserScreenNameInput = page.locator('[id="_com_liferay_users_admin_web_portlet_UsersAdminPortlet_screenName"]');
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.creationMenuNewButton = page.getByTestId('creationMenuNewButtonResponsive');
		this.page = page;
	}

	async goto(forceReload?: boolean) {
		await this.applicationsMenuPage.goToUsersAndOrganizations(forceReload);
	}

	async goToAddUser(forceReload?: boolean) {
		await this.goto(forceReload);
		await Promise.all([
			this.creationMenuNewButton.click(),
			this.page.waitForResponse(
				(resp) =>
					resp.status() === 200 &&
					resp.url().includes('mvcRenderCommandName=%2Fusers_admin%2Fedit_user')
			),
		]);
	}

	async addUser(requiredFields?: any) {
		const fields = {...{
			email: 'user_a_sn@liferay.com',
			firstName: 'user_a_fn',
			lastName: 'user_a_ln',
			screenName: 'user_a_sn',
		}, ...requiredFields};

		await this.addUserScreenNameInput.fill(fields.screenName);

		await this.addUserEmailAddressInput.fill(fields.email);

		await this.addUserFirstNameInput.fill(fields.firstName);

		await this.addUserLastNameInput.fill(fields.lastName);

		await this.addUserSaveButton.click();
	}
}
