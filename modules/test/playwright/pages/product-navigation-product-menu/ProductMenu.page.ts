/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

export class ProductMenuPage {
	readonly closeProductMenuButton: Locator;
	readonly documentsAndMediaMenuItem: Locator;
	readonly designMenuItem: Locator;
	readonly contentAndDataMenuItem: Locator;
	readonly knowledgeBaseMenuItem: Locator;
	readonly openProductMenuButton: Locator;
	readonly page: Page;
	readonly pagesMenuItem: Locator;
	readonly siteBuilderMenuItem: Locator;
	readonly styleBooksMenuItem: Locator;

	constructor(page: Page) {
		this.closeProductMenuButton = page.getByLabel('Close Product Menu');
		this.contentAndDataMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Content & Data',
		});
		this.knowledgeBaseMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Knowledge Base',
		});
		this.documentsAndMediaMenuItem = page.getByRole('menuitem', {
			name: 'Documents and Media',
		});
		this.designMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Design',
		});
		this.styleBooksMenuItem = page.getByRole('menuitem', {
			name: 'Style Books',
		});

		this.siteBuilderMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Site Builder',
		});
		this.pagesMenuItem = page.getByRole('menuitem', {
			exact: true,
			name: 'Pages',
		});

		this.openProductMenuButton = page.getByLabel('Open Product Menu');
		this.page = page;
	}

	async goto() {
		await this.page.goto('/');
	}

	async openProductMenu() {
		await this.goto();

		if (await this.openProductMenuButton.isVisible()) {
			await this.openProductMenuButton.click();
		}
	}

	async closeProductMenu() {
		await this.goto();

		if (await this.closeProductMenuButton.isVisible()) {
			await this.closeProductMenuButton.click();
		}
	}

	async goToKnowledgeBaseMenuItem() {
		await this.goToContentAndData();
		await this.knowledgeBaseMenuItem.click();
	}

	async goToDocumentsAndMediaMenuItem() {
		await this.goToContentAndData();
		await this.documentsAndMediaMenuItem.click();
	}

	async goToStyleBooksMenuItem() {
		await this.goToDesign();
		await this.styleBooksMenuItem.click();
	}

	async goToDesign() {
		await this.openProductMenu();
		const isClosed =
			(await this.designMenuItem.getAttribute('aria-expanded')) ===
			'false';

		if (isClosed) {
			await this.designMenuItem.click();
		}
	}

	async goToPagesMenuItem() {
		await this.goToSiteBuilder();
		await this.pagesMenuItem.click();
	}

	async goToSiteBuilder() {
		await this.openProductMenu();
		const isClosed =
			(await this.siteBuilderMenuItem.getAttribute('aria-expanded')) ===
			'false';

		if (isClosed) {
			await this.siteBuilderMenuItem.click();
		}
	}

	async goToContentAndData() {
		await this.openProductMenu();
		const isClosed =
			(await this.contentAndDataMenuItem.getAttribute(
				'aria-expanded'
			)) === 'false';

		if (isClosed) {
			await this.contentAndDataMenuItem.click();
		}
	}
}
