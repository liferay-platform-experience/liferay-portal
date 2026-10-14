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
	SET_DEFAULT_VARIATION_TOKEN_VALUES,
	SET_DRAFT_STATUS,
	SET_TOKEN_VALUES,
	UPDATE_UNDO_REDO_HISTORY,
} from '../../../src/main/resources/META-INF/resources/js/style-book-editor/constants/actionTypes';
import editorReducer from '../../../src/main/resources/META-INF/resources/js/style-book-editor/reducers/editorReducer';

const STATE = {
	draftStatus: null,
	frontendTokensValues: {
		testColor1: {
			cssVariableMapping: 'test-color-1',
			label: 'Test Color 1',
			value: '#AAAAAA',
		},
	},
	loading: true,
	previewLayout: {},
	previewLayoutType: null,
	redoHistory: [
		{
			name: 'testColor3',
			value: {
				cssVariableMapping: 'test-color-3',
				label: 'Test Color 3',
				value: '#111111',
			},
		},
	],
	undoHistory: [
		{
			name: 'testColor1',
			value: {
				cssVariableMapping: 'test-color-1',
				label: 'Test Color 1',
				value: '#BBBBBB',
			},
		},
	],
};

describe('editorReducer', () => {
	it('saves needed state when dispatching SET_DRAFT_STATUS action', () => {
		const {draftStatus} = editorReducer(STATE, {
			type: SET_DRAFT_STATUS,
			value: 'saving',
		});

		expect(draftStatus).toBe('saving');
	});

	it('saves needed state when dispatching SET_TOKEN_VALUES action', () => {
		const tokens = {
			testColor2: {
				cssVariableMapping: 'test-color-2',
				label: 'Test Color 2',
				value: '#BBBBBB',
			},
		};

		const {frontendTokensValues} = editorReducer(STATE, {
			tokens,
			type: SET_TOKEN_VALUES,
		});

		expect(frontendTokensValues).toEqual({
			...frontendTokensValues,
			...tokens,
		});
	});

	it('saves needed state when dispatching ADD_UNDO_ACTION action when is a redo action', () => {
		const token = {
			name: 'testColor2',
			value: {
				cssVariableMapping: 'test-color-2',
				label: 'Test Color 2',
				value: '#CCCCCC',
			},
		};

		const {redoHistory, undoHistory} = editorReducer(STATE, {
			isRedo: true,
			type: ADD_UNDO_ACTION,
			...token,
		});

		expect(redoHistory).toEqual(STATE.redoHistory);
		expect(undoHistory).toEqual([token, ...STATE.undoHistory]);
	});

	it('saves needed state when dispatching ADD_UNDO_ACTION action when there is no a redo action', () => {
		const token = {
			name: 'testColor2',
			value: {
				cssVariableMapping: 'test-color-2',
				label: 'Test Color 2',
				value: '#CCCCCC',
			},
		};

		const {redoHistory, undoHistory} = editorReducer(STATE, {
			type: ADD_UNDO_ACTION,
			...token,
		});

		expect(redoHistory).toEqual([]);
		expect(undoHistory).toEqual([token, ...STATE.undoHistory]);
	});

	it('saves needed state when dispatching ADD_REDO_ACTION action', () => {
		const token = {
			name: 'testColor2',
			value: {
				cssVariableMapping: 'test-color-2',
				label: 'Test Color 2',
				value: '#DDDDDD',
			},
		};

		const {redoHistory} = editorReducer(STATE, {
			type: ADD_REDO_ACTION,
			...token,
		});

		expect(redoHistory).toEqual([token, ...STATE.redoHistory]);
	});

	it('saves needed state when dispatching UPDATE_UNDO_REDO_HISTORY action', () => {
		const nextRedoHistory = [
			{
				name: 'testColor2',
				value: {
					cssVariableMapping: 'test-color-2',
					label: 'Test Color 2',
					value: '#DDDDDD',
				},
			},
		];

		const nextUndoHistory = [
			{
				name: 'testColor3',
				value: {
					cssVariableMapping: 'test-color-3',
					label: 'Test Color 2',
					value: '#333333',
				},
			},
		];

		const {redoHistory, undoHistory} = editorReducer(STATE, {
			redoHistory: nextRedoHistory,
			type: UPDATE_UNDO_REDO_HISTORY,
			undoHistory: nextUndoHistory,
		});

		expect(redoHistory).toEqual(nextRedoHistory);
		expect(undoHistory).toEqual(nextUndoHistory);
	});

	it('saves needed state when dispatching UPDATE_UNDO_REDO_HISTORY action and there are no previous redo/undo history', () => {
		const {redoHistory, undoHistory} = editorReducer(STATE, {
			type: UPDATE_UNDO_REDO_HISTORY,
		});

		expect(redoHistory).toEqual(STATE.redoHistory);
		expect(undoHistory).toEqual(STATE.undoHistory);
	});
});

describe('editorReducer with variations', () => {
	const VARIATIONS_STATE = {
		activeVariation: 'default',
		frontendTokensValues: {
			primary: {cssVariableMapping: 'primary', value: '#000'},
		},
		redoHistory: [],
		undoHistory: [{name: 'primary', value: {value: '#111'}}],
		variations: {
			dark: {
				name: 'Dark',
				tokens: {
					primary: {cssVariableMapping: 'primary', value: '#fff'},
				},
			},
			default: {name: 'default', tokens: {}},
		},
	};

	it('switches to a variation, keeping the edited tokens and clearing the history', () => {
		const state = editorReducer(VARIATIONS_STATE, {
			key: 'dark',
			type: SET_ACTIVE_VARIATION,
		});

		expect(state.activeVariation).toBe('dark');
		expect(state.frontendTokensValues).toEqual(
			VARIATIONS_STATE.variations.dark.tokens
		);
		expect(state.undoHistory).toEqual([]);
		expect(state.variations.default.tokens).toEqual(
			VARIATIONS_STATE.frontendTokensValues
		);
	});

	it('adds a variation without tokens and selects it', () => {
		const state = editorReducer(VARIATIONS_STATE, {
			key: 'calm',
			name: 'Calm',
			type: ADD_VARIATION,
		});

		expect(state.activeVariation).toBe('calm');
		expect(state.frontendTokensValues).toEqual({});
		expect(state.variations.calm).toEqual({name: 'Calm', tokens: {}});
	});

	it('deletes a variation and goes back to the default one', () => {
		const darkState = editorReducer(VARIATIONS_STATE, {
			key: 'dark',
			type: SET_ACTIVE_VARIATION,
		});

		const state = editorReducer(darkState, {
			key: 'dark',
			type: DELETE_VARIATION,
		});

		expect(state.activeVariation).toBe('default');
		expect(state.frontendTokensValues).toEqual(
			VARIATIONS_STATE.frontendTokensValues
		);
		expect(state.variations.dark).toBeUndefined();
	});

	it('adds new custom token values to the default variation while another one is active', () => {
		const darkState = editorReducer(VARIATIONS_STATE, {
			key: 'dark',
			type: SET_ACTIVE_VARIATION,
		});

		const state = editorReducer(darkState, {
			tokens: {custom: {cssVariableMapping: 'custom', value: 'red'}},
			type: SET_DEFAULT_VARIATION_TOKEN_VALUES,
		});

		expect(state.frontendTokensValues).toEqual(
			VARIATIONS_STATE.variations.dark.tokens
		);
		expect(state.variations.default.tokens.custom).toEqual({
			cssVariableMapping: 'custom',
			value: 'red',
		});
	});
});
