/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import {IItemsActions, formatActionURL} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import React, {useId} from 'react';

import {DesignLibrary} from '../../types';
import MarkedAsDefaultIcon from './MarkedAsDefaultIcon';

export interface BaseLinkRendererProps {
	action?: IItemsActions;
	itemData: DesignLibrary;
	markedAsDefault?: boolean;
	value: string;
}

export function BaseLinkRenderer({
	action,
	itemData,
	markedAsDefault,
	stickerClassName,
	stickerStyle,
	symbol,
	value,
}: BaseLinkRendererProps & {
	stickerClassName: string;
	stickerStyle?: React.CSSProperties;
	symbol: string;
}) {
	const markedAsDefaultIconId = useId();

	if (!action?.href && !markedAsDefault) {
		return <>{value}</>;
	}

	return (
		<div className="align-items-center d-flex table-list-title">
			{action?.href ? (
				<>
					<ClaySticker
						className={classNames(
							'c-mr-2',
							'flex-shrink-0',
							'inline-item',
							'inline-item-before',
							stickerClassName
						)}
						style={stickerStyle}
					>
						<ClayIcon symbol={symbol} />
					</ClaySticker>

					<ClayLink
						aria-describedby={
							markedAsDefault ? markedAsDefaultIconId : undefined
						}
						aria-label={value}
						className="align-items-center d-inline-flex"
						data-senna-off
						href={formatActionURL(
							action.href,
							itemData,
							action.target
						)}
						onClick={(event) => event.stopPropagation()}
					>
						{value}

						{markedAsDefault && (
							<MarkedAsDefaultIcon id={markedAsDefaultIconId} />
						)}
					</ClayLink>
				</>
			) : (
				<>
					<span>{value}</span>

					<MarkedAsDefaultIcon />
				</>
			)}
		</div>
	);
}
