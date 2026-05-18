/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped;

/**
 * @author Gabriel Lima
 */
public enum Level {

	ENTRIES("entries"), GROUPS("groups"), SCOPES("scopes");

	public static Level fromKey(String key) {
		for (Level level : values()) {
			if (level._key.equals(key)) {
				return level;
			}
		}

		return null;
	}

	public String getKey() {
		return _key;
	}

	private Level(String key) {
		_key = key;
	}

	private final String _key;

}