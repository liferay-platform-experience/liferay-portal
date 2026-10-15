/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ADD_REDO_ACTION,
	ADD_UNDO_ACTION,
	ADD_VARIATION,
	DELETE_VARIATION,
	SET_ACTIVE_VARIATION,
	SET_CUSTOM_FRONTEND_TOKEN_DEFINITION,
	SET_DEFAULT_VARIATION_TOKEN_VALUES,
	SET_DRAFT_STATUS,
	SET_TOKEN_VALUES,
	UPDATE_UNDO_REDO_HISTORY,
} from '../constants/actionTypes';
import {DEFAULT_VARIATION, getVariations} from '../utils/variations';

const MAX_UNDO_ACTIONS = 100;

export default function editorReducer(state, action) {
	switch (action.type) {
		case SET_DRAFT_STATUS: {
			const {value} = action;

			return {
				...state,
				draftStatus: value,
			};
		}

		case SET_TOKEN_VALUES: {
			const {tokens} = action;

			return {
				...state,
				frontendTokensValues: {
					...state.frontendTokensValues,
					...tokens,
				},
			};
		}

		case ADD_VARIATION: {
			const {key, name} = action;

			return {
				...state,
				activeVariation: key,
				frontendTokensValues: {},
				redoHistory: [],
				undoHistory: [],
				variations: {
					...getVariations(state),
					[key]: {name, tokens: {}},
				},
			};
		}

		case DELETE_VARIATION: {
			const {key} = action;

			const variations = getVariations(state);

			delete variations[key];

			return {
				...state,
				activeVariation: DEFAULT_VARIATION,
				frontendTokensValues: variations[DEFAULT_VARIATION].tokens,
				redoHistory: [],
				undoHistory: [],
				variations,
			};
		}

		case SET_ACTIVE_VARIATION: {
			const {key} = action;

			const variations = getVariations(state);

			return {
				...state,
				activeVariation: key,
				frontendTokensValues: variations[key]?.tokens ?? {},
				redoHistory: [],
				undoHistory: [],
				variations,
			};
		}

		case SET_DEFAULT_VARIATION_TOKEN_VALUES: {
			const {tokens} = action;

			if (state.activeVariation === DEFAULT_VARIATION) {
				return {
					...state,
					frontendTokensValues: {
						...state.frontendTokensValues,
						...tokens,
					},
				};
			}

			const defaultVariation = state.variations[DEFAULT_VARIATION];

			return {
				...state,
				variations: {
					...state.variations,
					[DEFAULT_VARIATION]: {
						...defaultVariation,
						tokens: {...defaultVariation.tokens, ...tokens},
					},
				},
			};
		}

		case SET_CUSTOM_FRONTEND_TOKEN_DEFINITION: {
			const {customFrontendTokenDefinition} = action;

			return {
				...state,
				customFrontendTokenDefinition,
			};
		}

		case ADD_UNDO_ACTION: {
			const {isRedo = false, label, name, value} = action;

			const nextRedoHistory = isRedo ? state.redoHistory : [];
			const nextUndoHistory = state.undoHistory || [];

			return {
				...state,
				redoHistory: nextRedoHistory,
				undoHistory: [
					{
						label,
						name,
						value,
					},
					...nextUndoHistory.slice(0, MAX_UNDO_ACTIONS - 1),
				],
			};
		}

		case ADD_REDO_ACTION: {
			const {label, name, value} = action;
			const nextRedoHistory = state.redoHistory || [];

			return {
				...state,
				redoHistory: [
					{
						label,
						name,
						value,
					},
					...nextRedoHistory,
				],
			};
		}

		case UPDATE_UNDO_REDO_HISTORY: {
			return {
				...state,
				redoHistory: action.redoHistory ?? state.redoHistory,
				undoHistory: action.undoHistory ?? state.undoHistory,
			};
		}

		default:
			return state;
	}
}
