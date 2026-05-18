/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped;

/**
 * @author Gabriel Lima
 */
public enum Scope {

	DESIGN_LIBRARY("design-library", "design-libraries"), SITE("site", "sites");

	public static Scope fromKey(String key) {
		for (Scope scope : values()) {
			if (scope._key.equals(key)) {
				return scope;
			}
		}

		return null;
	}

	public String getKey() {
		return _key;
	}

	public String getLabel() {
		return _label;
	}

	private Scope(String key, String label) {
		_key = key;
		_label = label;
	}

	private final String _key;
	private final String _label;

}