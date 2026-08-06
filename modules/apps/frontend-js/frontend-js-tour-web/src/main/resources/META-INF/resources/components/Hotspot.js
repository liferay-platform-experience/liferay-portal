/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {forwardRef, useCallback, useEffect} from 'react';

import {useObserveRect} from '../hooks/useObserveRect';
import {clampToDocument, doAlign} from '../utils';

export const Hotspot = forwardRef(({onHotspotClick, trigger}, ref) => {
	const align = useCallback(() => {
		if (trigger && ref?.current) {
			doAlign({
				points: ['cc', 'tl'],
				sourceElement: ref.current,
				targetElement: trigger,
			});

			clampToDocument(ref.current);
		}
	}, [ref, trigger]);

	useEffect(() => {
		align();
	}, [align]);

	useObserveRect(align, trigger);

	return (
		<button
			aria-label={Liferay.Language.get('start-the-tour')}
			className="lfr-tour-hotspot"
			onClick={onHotspotClick}
			ref={ref}
			type="button"
		>
			<span className="lfr-tour-hotspot-inner" />
		</button>
	);
});
