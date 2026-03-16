<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewDesignLibraryAdminDisplayContext viewDesignLibraryAdminDisplayContext = new ViewDesignLibraryAdminDisplayContext(request);
%>

<%-- <portlet:renderURL var="designLibraryURL">
	<portlet:param name="name" value="/view" />
	<portlet:param name="entryid" value="000" />
</portlet:renderURL> --%>

<portlet:renderURL var="designLibraryURL" />

<div>
	<frontend-data-set:headless-display
		additionalProps='<%=
			HashMapBuilder.<String, Object>put(
				"redirectURL", designLibraryURL
			).build()
		%>'
		apiURL="<%= viewDesignLibraryAdminDisplayContext.getAPIURL() %>"
		emptyState="<%= viewDesignLibraryAdminDisplayContext.getEmptyState() %>"
		formName="fm"
		id="<%= DesignLibraryAdminFDSNames.DESIGN_LIBRARIES %>"
		propsTransformer="{DesignLibraryAdminFDSPropsTransformer} from design-library-web"
	/>
</div>