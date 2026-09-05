<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String tabs2 = ParamUtil.getString(request, "tabs2", "portlets");

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setTabs2(
	tabs2
).setParameter(
	"struts_action", "/plugins_admin/view"
).buildPortletURL();

PortletURL marketplaceURL = null;

boolean showEditPluginHREF = true;
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				for (String tabs2Name : PluginsAdminNavigationConstants.TABS2_NAMES) {
					add(
						navigationItem -> {
							navigationItem.setActive(tabs2.equals(tabs2Name));
							navigationItem.setHref(renderResponse.createRenderURL(), "tabs2", tabs2Name);
							navigationItem.setLabel(LanguageUtil.get(httpServletRequest, tabs2Name));
						});
				}
			}
		}
	%>'
/>

<clay:container-fluid>
	<c:choose>
		<c:when test='<%= tabs2.equals("themes") %>'>
			<%@ include file="/themes.jspf" %>
		</c:when>
		<c:when test='<%= tabs2.equals("layout-templates") %>'>
			<%@ include file="/layout_templates.jspf" %>
		</c:when>
		<c:when test='<%= tabs2.equals("hook-plugins") %>'>
		</c:when>
		<c:when test='<%= tabs2.equals("web-plugins") %>'>
		</c:when>
		<c:otherwise>
			<%@ include file="/portlets.jspf" %>
		</c:otherwise>
	</c:choose>
</clay:container-fluid>