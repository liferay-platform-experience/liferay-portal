/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewSpacesDisplayContext;
import com.liferay.site.cms.site.initializer.internal.util.CMSSidebarUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = FragmentRenderer.class)
public class ViewSpacesComponentSectionFragmentRenderer
	extends BaseComponentSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (fragmentRendererContext.isDisablePortletRender() ||
			!CMSSidebarUtil.isUnifiedSidebar(httpServletRequest)) {

			super.render(
				fragmentRendererContext, httpServletRequest,
				httpServletResponse);

			return;
		}

		// The unified sidebar renders the spaces with the rest of the
		// navigation, so it only needs their data.

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject();

			Map<String, Object> props = getProps(
				fragmentRendererContext, httpServletRequest);

			props.forEach(jsonObject::put);

			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write(
				StringBundler.concat(
					"<script class=\"cms-spaces-props\" ",
					"type=\"application/json\">", jsonObject, "</script>"));
		}
		catch (IOException | RuntimeException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

	@Override
	protected String getComponentName() {
		return "SpacesNavigation";
	}

	@Override
	protected String getLabelKey() {
		return "spaces";
	}

	@Override
	protected Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception {

		ViewSpacesDisplayContext viewSpacesDisplayContext =
			new ViewSpacesDisplayContext(
				_assetLibraryResourceFactory, httpServletRequest, _jsonFactory,
				_portletResourcePermission);

		return viewSpacesDisplayContext.getProps();
	}

	@Reference
	private AssetLibraryResource.Factory _assetLibraryResourceFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference(target = "(resource.name=" + DepotConstants.RESOURCE_NAME + ")")
	private PortletResourcePermission _portletResourcePermission;

}