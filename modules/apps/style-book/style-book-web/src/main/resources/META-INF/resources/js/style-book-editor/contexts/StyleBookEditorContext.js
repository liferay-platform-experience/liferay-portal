/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';
import React, {useContext, useMemo, useReducer} from 'react';

import {
	ADD_REDO_ACTION,
	ADD_UNDO_ACTION,
	ADD_VARIATION,
	DELETE_VARIATION,
	SET_ACTIVE_VARIATION,
	SET_DRAFT_STATUS,
	SET_TOKEN_VALUES,
	UPDATE_UNDO_REDO_HISTORY,
} from '../constants/actionTypes';
import {DRAFT_STATUS} from '../constants/draftStatusConstants';
import {UNDO_TYPES} from '../constants/undoTypes';
import editorReducer from '../reducers/editorReducer';
import saveDraft from '../saveDraft';
import {DEFAULT_VARIATION, getVariations} from '../utils/variations';

const StyleBookEditorDispatchContext = React.createContext(() => {});
export const StyleBookEditorStoreContext = React.createContext({
	activeVariation: DEFAULT_VARIATION,
	customFrontendTokenDefinition: {},
	draftStatus: null,
	frontendTokensValues: {},
	redoHistory: [],
	undoHistory: [],
	variations: {},
});

export function StyleBookEditorContextProvider({children, initialState}) {
	const [state, dispatch] = useReducer(editorReducer, initialState);

	return (
		<StyleBookEditorDispatchContext.Provider value={dispatch}>
			<StyleBookEditorStoreContext.Provider value={state}>
				{children}
			</StyleBookEditorStoreContext.Provider>
		</StyleBookEditorDispatchContext.Provider>
	);
}

export function useActiveVariation() {
	return (
		useContext(StyleBookEditorStoreContext).activeVariation ??
		DEFAULT_VARIATION
	);
}

export function useCustomFrontendTokenDefinition() {
	return useContext(StyleBookEditorStoreContext)
		.customFrontendTokenDefinition;
}

export function useDispatch() {
	return useContext(StyleBookEditorDispatchContext);
}

export function useDraftStatus() {
	return useContext(StyleBookEditorStoreContext).draftStatus;
}

export function useEffectiveFrontendTokensValues() {
	const {
		activeVariation = DEFAULT_VARIATION,
		frontendTokensValues,
		variations = {},
	} = useContext(StyleBookEditorStoreContext);

	return useMemo(() => {
		if (activeVariation === DEFAULT_VARIATION) {
			return frontendTokensValues;
		}

		// A variation only stores the tokens it overrides, the rest come from
		// the default variation

		return {
			...variations[DEFAULT_VARIATION]?.tokens,
			...frontendTokensValues,
		};
	}, [activeVariation, frontendTokensValues, variations]);
}

export function useFrontendTokensValues() {
	return useContext(StyleBookEditorStoreContext).frontendTokensValues;
}

export function useRedoHistory() {
	return useContext(StyleBookEditorStoreContext).redoHistory;
}

export function useUndoHistory() {
	return useContext(StyleBookEditorStoreContext).undoHistory;
}

export function useVariations() {
	const state = useContext(StyleBookEditorStoreContext);

	return useMemo(() => getVariations(state), [state]);
}

function useVariationsState() {
	const {activeVariation, variations} = useContext(
		StyleBookEditorStoreContext
	);

	return {activeVariation, variations};
}

export function useAddVariation() {
	const dispatch = useDispatch();
	const frontendTokensValues = useFrontendTokensValues();
	const {activeVariation, variations} = useVariationsState();

	return ({key, name}) =>
		saveVariations({
			dispatch,
			variations: {
				...getVariations({
					activeVariation,
					frontendTokensValues,
					variations,
				}),
				[key]: {name, tokens: {}},
			},
		}).then(() => dispatch({key, name, type: ADD_VARIATION}));
}

export function useDeleteVariation() {
	const dispatch = useDispatch();
	const frontendTokensValues = useFrontendTokensValues();
	const {activeVariation, variations} = useVariationsState();

	return (key) => {
		const nextVariations = getVariations({
			activeVariation,
			frontendTokensValues,
			variations,
		});

		delete nextVariations[key];

		return saveVariations({dispatch, variations: nextVariations}).then(() =>
			dispatch({key, type: DELETE_VARIATION})
		);
	};
}

export function useSelectVariation() {
	const dispatch = useDispatch();

	return (key) => dispatch({key, type: SET_ACTIVE_VARIATION});
}

function saveVariations({dispatch, variations}) {
	dispatch({
		type: SET_DRAFT_STATUS,
		value: DRAFT_STATUS.saving,
	});

	return saveDraft(variations[DEFAULT_VARIATION].tokens, {
		activeVariation: DEFAULT_VARIATION,
		variations,
	})
		.then(() => {
			dispatch({
				type: SET_DRAFT_STATUS,
				value: DRAFT_STATUS.draftSaved,
			});
		})
		.catch((error) => {
			dispatch({
				type: SET_DRAFT_STATUS,
				value: DRAFT_STATUS.notSaved,
			});

			openToast({
				message: escapeHTML(error.message),
				type: 'danger',
			});

			throw error;
		});
}

function internalSaveTokenValues({
	activeVariation,
	dispatch,
	frontendTokensValues,
	tokens,
	variations,
}) {
	dispatch({
		type: SET_DRAFT_STATUS,
		value: DRAFT_STATUS.saving,
	});

	return saveDraft(
		{...frontendTokensValues, ...tokens},
		{activeVariation, variations}
	)
		.then(() => {
			dispatch({
				type: SET_DRAFT_STATUS,
				value: DRAFT_STATUS.draftSaved,
			});

			dispatch({
				tokens,
				type: SET_TOKEN_VALUES,
			});
		})
		.catch((error) => {
			if (process.env.NODE_ENV === 'development') {
				console.error(error);
			}

			dispatch({
				type: SET_DRAFT_STATUS,
				value: DRAFT_STATUS.notSaved,
			});

			openToast({
				message: error.message,
				type: 'danger',
			});
		});
}

export function useSaveTokenValue() {
	const dispatch = useDispatch();
	const variationsState = useVariationsState();
	const frontendTokensValues = useFrontendTokensValues();

	return ({label, name, value}) => {
		const previousValue = frontendTokensValues[name];

		internalSaveTokenValues({
			...variationsState,
			dispatch,
			frontendTokensValues,
			tokens: {[name]: value},
		}).then(() => {
			dispatch({
				label,
				name,
				type: ADD_UNDO_ACTION,
				value: previousValue,
			});
		});
	};
}

export function useOnUndo() {
	const dispatch = useDispatch();
	const variationsState = useVariationsState();
	const frontendTokensValues = useFrontendTokensValues();
	const undoHistory = useUndoHistory();

	return () => {
		const [lastUndo, ...undos] = undoHistory;
		const previousValue = frontendTokensValues[lastUndo.name];

		if (!lastUndo.value) {
			delete frontendTokensValues[lastUndo.name];
		}

		internalSaveTokenValues({
			...variationsState,
			dispatch,
			frontendTokensValues,
			tokens: lastUndo.value ? {[lastUndo.name]: lastUndo.value} : {},
		}).then(() => {
			dispatch({
				type: UPDATE_UNDO_REDO_HISTORY,
				undoHistory: undos,
			});
			dispatch({
				label: lastUndo.label,
				name: lastUndo.name,
				type: ADD_REDO_ACTION,
				value: previousValue,
			});
		});
	};
}

export function useOnRedo() {
	const dispatch = useDispatch();
	const variationsState = useVariationsState();
	const frontendTokensValues = useFrontendTokensValues();
	const redoHistory = useRedoHistory();

	return () => {
		const [lastRedo, ...redos] = redoHistory;
		const previousValue = frontendTokensValues[lastRedo.name];

		internalSaveTokenValues({
			...variationsState,
			dispatch,
			frontendTokensValues,
			tokens: {[lastRedo.name]: lastRedo.value},
		}).then(() => {
			dispatch({
				redoHistory: redos,
				type: UPDATE_UNDO_REDO_HISTORY,
			});
			dispatch({
				isRedo: true,
				label: lastRedo.label,
				name: lastRedo.name,
				type: ADD_UNDO_ACTION,
				value: previousValue,
			});
		});
	};
}

export function useMultipleUndo() {
	const dispatch = useDispatch();
	const variationsState = useVariationsState();
	const undoHistory = useUndoHistory();
	const redoHistory = useRedoHistory();
	const frontendTokensValues = useFrontendTokensValues();

	return ({numberOfActions, type}) => {
		let remainingUndos;
		let undosToUndo;

		if (type === UNDO_TYPES.undo) {
			undosToUndo = undoHistory.slice(0, numberOfActions);

			remainingUndos = undoHistory.slice(
				numberOfActions,
				undoHistory.length
			);
		}
		else {
			undosToUndo = redoHistory.slice(0, numberOfActions);

			remainingUndos = redoHistory.slice(
				numberOfActions,
				redoHistory.length
			);
		}

		const nextHistory = [];
		let tokens = {};

		for (const undo of undosToUndo) {
			nextHistory.push({
				label: undo.label,
				name: undo.name,
				value: tokens[undo.name] ?? frontendTokensValues[undo.name],
			});

			if (undo.value) {
				tokens = {...tokens, [undo.name]: undo.value};
			}
			else {
				delete frontendTokensValues[undo.name];
				delete tokens[undo.name];
			}
		}

		const updateHistoryAction =
			type === UNDO_TYPES.undo
				? {
						redoHistory: [...nextHistory.reverse(), ...redoHistory],
						type: UPDATE_UNDO_REDO_HISTORY,
						undoHistory: remainingUndos,
					}
				: {
						redoHistory: remainingUndos,
						type: UPDATE_UNDO_REDO_HISTORY,
						undoHistory: [...nextHistory.reverse(), ...undoHistory],
					};

		return internalSaveTokenValues({
			...variationsState,
			dispatch,
			frontendTokensValues,
			tokens,
		}).then(() => {
			dispatch(updateHistoryAction);
		});
	};
}
