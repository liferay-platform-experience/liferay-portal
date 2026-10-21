/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.tour.web.internal.servlet.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Registers the global <code>Liferay.Tour</code> API on every page so a
 * trigger (a "Take the tour" button, an intro modal, or other code) can start
 * a tour on demand. The module itself is only loaded the first time a tour is
 * started.
 *
 * @author Marcos Castro
 */
@Component(service = DynamicInclude.class)
public class TourBottomJSPDynamicInclude implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		ScriptData scriptData = new ScriptData();

		scriptData.append(
			null,
			new JSFragment(
				StringBundler.concat(
					"Liferay.Tour = {start(tourDefinition) {return import('",
					absolutePortalURLBuilder.forESModule(
						"frontend-js-tour-web", "index.js"
					).build(),
					"').then(({start}) => start(tourDefinition));}};")));

		scriptData.writeTo(httpServletResponse.getWriter());
	}

	@Override
	public void register(
		DynamicInclude.DynamicIncludeRegistry dynamicIncludeRegistry) {

		dynamicIncludeRegistry.register("/html/common/themes/bottom.jsp#post");
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

}