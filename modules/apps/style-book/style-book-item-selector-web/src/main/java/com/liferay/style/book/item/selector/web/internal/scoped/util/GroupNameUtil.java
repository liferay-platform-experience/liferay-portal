/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.item.selector.web.internal.scoped.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;

import java.util.Locale;

/**
 * @author Gabriel Lima
 */
public class GroupNameUtil {

	public static String getName(Group group, Locale locale) {
		try {
			return group.getDescriptiveName(locale);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return group.getName(locale);
	}

	private static final Log _log = LogFactoryUtil.getLog(GroupNameUtil.class);

}