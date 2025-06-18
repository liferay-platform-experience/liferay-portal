/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.display.context;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public class SegmentsCompanyConfigurationDisplayContext {

	public SegmentsCompanyConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		SegmentsConfigurationProvider segmentsConfigurationProvider) {

		_httpServletRequest = httpServletRequest;
		_segmentsConfigurationProvider = segmentsConfigurationProvider;
	}

	public String getSegmentsCompanyConfigurationURL() {
		try {
			return _segmentsConfigurationProvider.getConfigurationURL(
				_httpServletRequest);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return null;
		}
	}

	public boolean isRoleSegmentationChecked() throws ConfigurationException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _segmentsConfigurationProvider.isRoleSegmentationEnabled(
			themeDisplay.getCompanyId());
	}

	public boolean isRoleSegmentationEnabled() throws ConfigurationException {
		return _segmentsConfigurationProvider.isRoleSegmentationEnabled();
	}

	public boolean isSegmentationChecked() throws ConfigurationException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _segmentsConfigurationProvider.isSegmentationEnabled(
			themeDisplay.getCompanyId());
	}

	public boolean isSegmentationEnabled() throws ConfigurationException {
		return _segmentsConfigurationProvider.isSegmentationEnabled();
	}

	public boolean isSegmentsCompanyConfigurationDefined()
		throws ConfigurationException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _segmentsConfigurationProvider.
			isSegmentsCompanyConfigurationDefined(themeDisplay.getCompanyId());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsCompanyConfigurationDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;
	private final SegmentsConfigurationProvider _segmentsConfigurationProvider;

}