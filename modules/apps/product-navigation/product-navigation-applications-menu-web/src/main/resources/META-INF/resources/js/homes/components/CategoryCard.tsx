/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCardWithNavigation} from '@clayui/card';
import ClayIcon from '@clayui/icon';
import React from 'react';

import {CategoryItem} from '../types';

const CategoryCard = ({
	item: {href, label, leadingIcon},
}: {
	item: CategoryItem;
}) => {
	return (
		<ClayCardWithNavigation className="home-card" href={href} title={label}>
			<ClayIcon symbol={leadingIcon} />
		</ClayCardWithNavigation>
	);
};

export default CategoryCard;
