/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.module.configuration.samples;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Thiago Buarque
 */
@Meta.OCD(
	id = "com.liferay.portal.configuration.module.configuration.samples.TestConfiguration"
)
public interface TestConfiguration {

	@Meta.AD
	public String key1();

	@Meta.AD
	public String key2();

}