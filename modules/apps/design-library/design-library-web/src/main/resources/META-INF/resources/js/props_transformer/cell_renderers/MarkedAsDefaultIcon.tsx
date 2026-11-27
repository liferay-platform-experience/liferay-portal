/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

export default function MarkedAsDefaultIcon({id}: {id?: string}) {
	return (
		<span
			className="c-ml-2 lfr-portal-tooltip text-primary"
			data-title={Liferay.Language.get('default')}
		>
			<ClayIcon symbol="check-circle" />

			<span className="sr-only" id={id}>
				{Liferay.Language.get('default')}
			</span>
		</span>
	);
}
