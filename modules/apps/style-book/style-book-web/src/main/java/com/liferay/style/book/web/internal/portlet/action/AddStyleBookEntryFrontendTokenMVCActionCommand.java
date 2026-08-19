/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryService;
import com.liferay.style.book.web.internal.handler.StyleBookEntryExceptionRequestHandlerUtil;
import com.liferay.style.book.web.internal.util.StyleBookEntryFrontendTokenDefinitionUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gabriel Lima
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/add_style_book_entry_frontend_token"
	},
	service = MVCActionCommand.class
)
public class AddStyleBookEntryFrontendTokenMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			StyleBookEntry styleBookEntry = _addStyleBookEntryFrontendToken(
				actionRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			JSONObject jsonObject = JSONUtil.put(
				"frontendTokenDefinitions",
				StyleBookEntryFrontendTokenDefinitionUtil.
					getFrontendTokenDefinitionsJSONObjects(
						_frontendTokenDefinitionRegistry, styleBookEntry,
						themeDisplay.getLocale()));

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse, jsonObject);
		}
		catch (PortalException portalException) {
			hideDefaultErrorMessage(actionRequest);

			StyleBookEntryExceptionRequestHandlerUtil.handlePortalException(
				actionRequest, actionResponse, portalException);
		}
	}

	private StyleBookEntry _addStyleBookEntryFrontendToken(
			ActionRequest actionRequest)
		throws PortalException {

		String label = ParamUtil.getString(actionRequest, "label");

		String name = TextFormatter.format(
			label.replaceAll("[^A-Za-z0-9\\s]", StringPool.BLANK),
			TextFormatter.F);

		if (name == null) {
			name = StringPool.BLANK;
		}

		return _styleBookEntryService.addStyleBookEntryFrontendToken(
			ParamUtil.getLong(actionRequest, "styleBookEntryId"),
			ParamUtil.getString(actionRequest, "categoryName"),
			_getCssVariableMappingValue(name),
			ParamUtil.getString(actionRequest, "description"),
			ParamUtil.getString(actionRequest, "editorType"), label, name,
			ParamUtil.getString(actionRequest, "tokenSetName"),
			ParamUtil.getString(actionRequest, "value"));
	}

	private String _getCssVariableMappingValue(String name) {
		String value = TextFormatter.format(name, TextFormatter.K);

		if (value == null) {
			return StringPool.BLANK;
		}

		return value.replaceAll("([a-zA-Z])([0-9])", "$1-$2");
	}

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private StyleBookEntryService _styleBookEntryService;

}