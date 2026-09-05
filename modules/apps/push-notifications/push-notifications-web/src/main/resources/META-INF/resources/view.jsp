<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "devices");

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setTabs1(
	tabs1
).buildPortletURL();
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				for (String tabs1Name : PushNotificationsNavigationConstants.TABS1_NAMES) {
					add(
						navigationItem -> {
							navigationItem.setActive(tabs1.equals(tabs1Name));
							navigationItem.setHref(renderResponse.createRenderURL(), "tabs1", tabs1Name);
							navigationItem.setLabel(LanguageUtil.get(httpServletRequest, tabs1Name));
						});
				}
			}
		}
	%>'
/>

<c:choose>
	<c:when test='<%= tabs1.equals("test") %>'>
		<%@ include file="/test.jspf" %>
	</c:when>
	<c:otherwise>
		<%@ include file="/devices.jspf" %>
	</c:otherwise>
</c:choose>