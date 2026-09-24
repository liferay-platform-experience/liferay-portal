/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';
import warning from 'warning';

const ClayIconSpriteContext = React.createContext('');

interface IProps extends React.SVGAttributes<SVGSVGElement> {
	className?: string;

	/**
	 * Flag to include `lexicon-icon lexicon-icon-*` classes on the svg element.
	 * Set it to `false` for spritemaps that are drawn at their own size, like
	 * the illustrations in `empty_states.svg`, so the `lexicon-icon` classes do
	 * not force them to the 1em glyph sizing.
	 */
	lexiconIcon?: boolean;

	/**
	 * Path to the location of the spritemap resource.
	 */
	spritemap?: string;

	/**
	 * The id of the icon in the spritemap.
	 */
	symbol: string;
}

const Icon = React.forwardRef<SVGSVGElement, IProps>(
	(
		{
			className,
			lexiconIcon = true,
			spritemap,
			symbol,
			...otherProps
		}: IProps,
		ref
	) => {
		let spriteMapVal = React.useContext(ClayIconSpriteContext);

		if (spritemap) {
			spriteMapVal = spritemap;
		}

		warning(
			spriteMapVal,
			'ClayIcon requires a `spritemap` via prop or ClayIconSpriteContext'
		);

		return (
			<svg
				{...otherProps}
				className={
					classNames(
						lexiconIcon && `lexicon-icon lexicon-icon-${symbol}`,
						className
					) || undefined
				}
				key={symbol}
				ref={ref}
				role="presentation"
			>
				<use href={`${spriteMapVal}#${symbol}`} />
			</svg>
		);
	}
);

Icon.displayName = 'ClayIcon';

export default Icon;
export {ClayIconSpriteContext};
