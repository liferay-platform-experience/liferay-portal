/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

AUI.add(
	'liferay-calendar-simple-color-picker',
	(A) => {
		const AArray = A.Array;
		const KeyMap = A.Event.KeyMap;
		const Lang = A.Lang;

		const STR_BLANK = '';

		const STR_DOT = '.';

		const getClassName = A.getClassName;

		const CSS_SIMPLE_COLOR_PICKER_ITEM = getClassName(
			'simple-color-picker',
			'item'
		);

		const CSS_SIMPLE_COLOR_PICKER_ITEM_SELECTED = getClassName(
			'simple-color-picker',
			'item',
			'selected'
		);

		const TPL_COLOR_ALERT =
			'<span aria-live="assertive" class="sr-only" role="alert"></span>';

		const TPL_SIMPLE_COLOR_PICKER_ITEM = new A.Template(
			'<tpl for="pallete">',
			'<button aria-label="{.}" class="',
			CSS_SIMPLE_COLOR_PICKER_ITEM,
			'" style="background-color: {.}',
			'; border-color:',
			'{.};',
			'" role="radio"></button>',
			'</tpl>'
		);

		const SimpleColorPicker = A.Component.create({
			ATTRS: {
				color: {
					setter(val) {
						return val.toUpperCase();
					},
					validator: Lang.isString,
					value: STR_BLANK,
				},

				host: {
					value: null,
				},

				pallete: {
					setter(val) {
						return AArray.invoke(val, 'toUpperCase');
					},
					validator: Lang.isArray,
					value: [
						'#f7e6e7',
						'#f9f1f5',
						'#efe2ec',
						'#ebe7f4',
						'#d9e2ea',
						'#e6ebf5',
						'#d6ece9',
						'#d7e6de',
						'#c8e6cd',
						'#dce8cc',
						'#eaead3',
						'#f2ebd3',
						'#f9e9db',
						'#f8e4dd',
						'#f3f0f0',
						'#eceaec',
						'#e5e7e9',
						'#f0f1f4',
						'#e4eae9',
						'#e8e8df',
						'#f4efea',
						'#f2c9b8',
						'#ebd0d4',
						'#efcee6',
					],
				},

				trigger: {
					value: null,
				},
			},

			NAME: 'simple-color-picker',

			UI_ATTRS: ['color', 'pallete'],

			prototype: {
				_focusItem(index) {
					const instance = this;

					const items = instance.items;

					const size = items.size();

					if (index !== undefined) {
						index = (index + size) % size;

						const item = items.item(index);

						item.getDOMNode().focus();
					}
				},

				_onClickColor(event) {
					const instance = this;

					const pallete = instance.get('pallete');

					const color =
						pallete[instance.items.indexOf(event.currentTarget)];

					instance.set('color', color);

					instance.colorAlert.setContent(
						Lang.sub(Liferay.Language.get('color-x-selected'), [
							color,
						])
					);
				},

				_onKeyDownColor(event) {
					const instance = this;

					const items = instance.items;

					const currentIndex = items.indexOf(event.currentTarget);

					const {keyCode} = event;

					if (keyCode === KeyMap.ESC) {
						event.preventDefault();
						event.stopPropagation();

						const trigger = instance.trigger;

						if (trigger) {
							trigger.focus();
						}
					}
					else if (
						keyCode === KeyMap.DOWN ||
						keyCode === KeyMap.RIGHT
					) {
						event.preventDefault();

						instance._focusItem(currentIndex + 1);
					}
					else if (
						keyCode === KeyMap.UP ||
						keyCode === KeyMap.LEFT
					) {
						event.preventDefault();

						instance._focusItem(currentIndex - 1);
					}
					else if (
						keyCode === KeyMap.SPACE ||
						keyCode === KeyMap.ENTER
					) {
						event.preventDefault();
						event.stopPropagation();

						instance._onClickColor(event);
					}
				},

				_renderColorAlert() {
					const instance = this;

					instance.colorAlert = A.Node.create(TPL_COLOR_ALERT);

					const contentBox = instance.get('contentBox');

					contentBox.prepend(instance.colorAlert);
				},

				_renderPallete() {
					const instance = this;

					instance.items = A.NodeList.create(
						TPL_SIMPLE_COLOR_PICKER_ITEM.parse({
							pallete: instance.get('pallete'),
						})
					);

					const contentBox = instance.get('contentBox');

					contentBox.setAttribute('role', 'radiogroup');

					contentBox.setContent(instance.items);
				},

				_uiSetColor(val) {
					const instance = this;

					const pallete = instance.get('pallete');

					instance.items.removeClass(
						CSS_SIMPLE_COLOR_PICKER_ITEM_SELECTED
					);

					instance.items.setAttribute('aria-checked', 'false');

					const newNode = instance.items.item(pallete.indexOf(val));

					if (newNode) {
						newNode.addClass(CSS_SIMPLE_COLOR_PICKER_ITEM_SELECTED);
						newNode.setAttribute('aria-checked', 'true');
					}

					const contentBox = instance.get('contentBox');

					contentBox.setAttribute(
						'aria-label',
						Lang.sub(
							Liferay.Language.get(
								'color-picker.-color-selected-x.-use-arrow-keys-to-move-to-different-colors.-press-enter-or-space-to-select-a-color.-press-escape-to-leave-the-color-picker'
							),
							[val]
						)
					);
				},

				_uiSetPallete() {
					const instance = this;

					if (instance.get('rendered')) {
						instance._renderPallete();
					}
				},

				bindUI() {
					const instance = this;

					const contentBox = instance.get('contentBox');

					contentBox.delegate(
						'click',
						instance._onClickColor,
						STR_DOT + CSS_SIMPLE_COLOR_PICKER_ITEM,
						instance
					);
					contentBox.delegate(
						'keydown',
						instance._onKeyDownColor,
						STR_DOT + CSS_SIMPLE_COLOR_PICKER_ITEM,
						instance
					);
				},

				focus(trigger) {
					const instance = this;

					instance.trigger = trigger;

					instance.items.first().focus();
				},

				renderUI() {
					const instance = this;

					instance._renderPallete();

					instance._renderColorAlert();
				},
			},
		});

		Liferay.SimpleColorPicker = SimpleColorPicker;
	},
	'',
	{
		requires: ['aui-base', 'aui-template-deprecated'],
	}
);
