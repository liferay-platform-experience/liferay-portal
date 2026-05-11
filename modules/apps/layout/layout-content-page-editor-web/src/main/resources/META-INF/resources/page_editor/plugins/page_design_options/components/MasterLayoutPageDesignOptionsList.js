/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {Option, PageDesignOptionsList} from './PageDesignOptionsList';

export default function MasterLayoutPageDesignOptionsList({
	masterLayoutPageTemplateEntryERC,
	masterLayouts,
	onSelectMasterLayout,
}) {
	return (
		<PageDesignOptionsList options={masterLayouts}>
			{(masterLayout) => (
				<Option
					{...masterLayout}
					icon="page"
					isActive={
						masterLayoutPageTemplateEntryERC ===
						masterLayout.masterLayoutPageTemplateEntryERC
					}
					onClick={() => onSelectMasterLayout(masterLayout)}
				/>
			)}
		</PageDesignOptionsList>
	);
}
