/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import '../../../css/AppIcon.scss';

type Props = {
	href: string;
	imageSrc?: string;
	label: string;
	symbol?: string;
};

const AppIcon = ({href, imageSrc, label, symbol}: Props) => {
	return (
		<a className="app-icon" href={href}>
			<span className="app-icon-tile">
				{imageSrc ? (
					<img alt="" src={imageSrc} />
				) : (
					symbol && <ClayIcon symbol={symbol} />
				)}
			</span>

			<span className="app-icon-label">{label}</span>
		</a>
	);
};

export default AppIcon;
