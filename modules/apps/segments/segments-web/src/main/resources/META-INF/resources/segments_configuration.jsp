<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SegmentsCompanyConfigurationDisplayContext segmentsCompanyConfigurationDisplayContext = (SegmentsCompanyConfigurationDisplayContext)request.getAttribute(SegmentsCompanyConfigurationDisplayContext.class.getName());
%>

<liferay-util:html-top
	outputKey="com.liferay.segments.web#/segments_configuration.jsp"
>
	<aui:link href='<%= PortalUtil.getStaticResourceURL(request, PortalUtil.getPathProxy() + application.getContextPath() + "/css/configuration.css") %>' rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<c:if test="<%= !segmentsCompanyConfigurationDisplayContext.isSegmentationEnabled() %>">
	<clay:alert
		cssClass="c-my-4"
		defaultTitleDisabled="<%= true %>"
		displayType="warning"
	>
		<strong><liferay-ui:message key="segmentation-is-disabled-in-system-settings" /></strong>

		<%
		String segmentsConfigurationURL = segmentsCompanyConfigurationDisplayContext.getSegmentsCompanyConfigurationURL();
		%>

		<c:choose>
			<c:when test="<%= segmentsConfigurationURL != null %>">
				<clay:link
					href="<%= segmentsConfigurationURL %>"
					label="to-enable,-go-to-system-settings"
				/>
			</c:when>
			<c:otherwise>
				<span><liferay-ui:message key="contact-your-system-administrator-to-enable-it" /></span>
			</c:otherwise>
		</c:choose>
	</clay:alert>
</c:if>

<c:if test="<%= !segmentsCompanyConfigurationDisplayContext.isRoleSegmentationEnabled() %>">
	<clay:alert
		cssClass="c-my-4"
		defaultTitleDisabled="<%= true %>"
		displayType="warning"
	>
		<strong><liferay-ui:message key="assign-roles-by-segment-is-disabled-in-system-settings" /></strong>

		<%
		String segmentsConfigurationURL = segmentsCompanyConfigurationDisplayContext.getSegmentsCompanyConfigurationURL();
		%>

		<c:choose>
			<c:when test="<%= segmentsConfigurationURL != null %>">
				<clay:link
					href="<%= segmentsConfigurationURL %>"
					label="to-enable,-go-to-system-settings"
				/>
			</c:when>
			<c:otherwise>
		<span><%=
		LanguageUtil.get(
			request, "contact-your-system-administrator-to-enable-it") %></span>
			</c:otherwise>
		</c:choose>
	</clay:alert>
</c:if>

<div class="row <%= (!segmentsCompanyConfigurationDisplayContext.isRoleSegmentationEnabled() || !segmentsCompanyConfigurationDisplayContext.isSegmentationEnabled()) ? "c-mt-5" : "" %>">
	<div class="col-sm-12 form-group">
		<div class="form-group__inner">
			<clay:checkbox
				checked="<%= segmentsCompanyConfigurationDisplayContext.isSegmentationChecked() %>"
				disabled="<%= !segmentsCompanyConfigurationDisplayContext.isSegmentationEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "segmentationEnabled" %>'
				label="segmentation-enabled-name"
				name='<%= liferayPortletResponse.getNamespace() + "segmentationEnabled" %>'
			/>

			<div aria-hidden="true" class="form-feedback-group">
				<div class="form-text text-weight-normal"><liferay-ui:message key="segmentation-enabled-description" /></div>
			</div>
		</div>
	</div>
</div>

<div class="row">
	<div class="col-sm-12 form-group">
		<div class="form-group__inner">
			<clay:checkbox
				checked="<%= segmentsCompanyConfigurationDisplayContext.isRoleSegmentationChecked() %>"
				disabled="<%= !segmentsCompanyConfigurationDisplayContext.isRoleSegmentationEnabled() %>"
				id='<%= liferayPortletResponse.getNamespace() + "roleSegmentationEnabled" %>'
				label="role-segmentation-enabled-name"
				name='<%= liferayPortletResponse.getNamespace() + "roleSegmentationEnabled" %>'
			/>

			<div aria-hidden="true" class="form-feedback-group">
				<div class="form-text text-weight-normal">
					<liferay-ui:message key="role-segmentation-enabled-description" />
				</div>
			</div>
		</div>
	</div>
</div>