/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCardWithNavigation} from '@clayui/card';
import React from 'react';

import {CategoryItem} from '../types';

const CategoryCardHorizontal = ({
	item: {href, label, leadingIcon},
}: {
	item: CategoryItem;
}) => {
	return (
		<ClayCardWithNavigation
			className="c-mb-2 c-mb-lg-3 home-card"
			horizontal
			horizontalSymbol={leadingIcon}
			href={href}
			stickerClassName="sticker-sm"
			title={label}
		/>
	);
};

export default CategoryCardHorizontal;
