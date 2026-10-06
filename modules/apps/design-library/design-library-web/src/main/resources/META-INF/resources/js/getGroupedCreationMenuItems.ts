/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayDropDownWithItems} from '@clayui/drop-down';
import {ComponentProps} from 'react';

import getCreationMenuItems from './getCreationMenuItems';
import {DesignLibraryResourceType} from './types';

export default function getGroupedCreationMenuItems(
	resourceTypes: DesignLibraryResourceType[]
): ComponentProps<typeof ClayDropDownWithItems>['items'] {
	return resourceTypes.flatMap<
		ComponentProps<typeof ClayDropDownWithItems>['items'][number]
	>((resourceType) => {
		const items = getCreationMenuItems([resourceType]);

		if (items.length > 1) {
			return [
				{
					items,
					label: resourceType.creationItemsGroupLabel,
					type: 'contextual',
				},
			];
		}

		return items;
	});
}
