<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
com.liferay.portal.kernel.portlet.LiferayPortletURL omniSearchContentURL = com.liferay.portal.kernel.portlet.PortletURLFactoryUtil.create(request, com.liferay.product.navigation.omni.search.web.internal.constants.ProductNavigationOmniSearchPortletKeys.PRODUCT_NAVIGATION_OMNI_SEARCH, jakarta.portlet.PortletRequest.RESOURCE_PHASE);

omniSearchContentURL.setResourceID("/omni_search/omni_search_content");

com.liferay.portal.kernel.portlet.LiferayPortletURL omniSearchDirectoryURL = com.liferay.portal.kernel.portlet.PortletURLFactoryUtil.create(request, com.liferay.product.navigation.omni.search.web.internal.constants.ProductNavigationOmniSearchPortletKeys.PRODUCT_NAVIGATION_OMNI_SEARCH, jakarta.portlet.PortletRequest.RESOURCE_PHASE);

omniSearchDirectoryURL.setResourceID("/omni_search/omni_search_directory");
%>

<react:component
	module="{CmsToolbarOmniSearch} from product-navigation-omni-search-web"
	props='<%=
		com.liferay.portal.kernel.util.HashMapBuilder.<String, Object>put(
			"contentURL", omniSearchContentURL.toString()
		).put(
			"directoryURL", omniSearchDirectoryURL.toString()
		).build()
	%>'
/>