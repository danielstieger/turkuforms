/*-
 * #%L
 * Selection Grid
 * %%
 * Copyright (C) 2020 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
customElements.whenDefined("vaadin-selection-grid").then(() => {
    const Grid = customElements.get("vaadin-selection-grid");
    if (Grid) {

        // TODO: codesmell - use bind or attach it to the Grid component? but not both...

        const oldOnContextMenuHandler = Grid.prototype._onContextMenu;
        Grid.prototype._onContextMenu = function _onContextMenu(e) {

            const tr = e.composedPath().find((p) => p.nodeName === "TR");
            if (tr && typeof tr.index != 'undefined') {
                const item = tr._item;
                const index = tr.index;
                if (! (this.selectedItems && this.selectedItems.some((i) => i.key === item.key))) {
                    this._selectionGridSelectRow(e);
                }
            }

            const boundOnConextMenuHandler = oldOnContextMenuHandler.bind(this);
            boundOnConextMenuHandler(e);
        }

        const oldStopEdit = Grid.prototype._stopEdit;
        Grid.prototype._stopEdit = function _onStopEdit(shouldCancel, shouldRestoreFocus) {

            const boundOldStopEdit = oldStopEdit.bind(this);
            boundOldStopEdit(shouldCancel, shouldRestoreFocus);

            this.dispatchEvent(
              new CustomEvent('cell-edit-stopped', {
                detail: {
                  cancel: shouldCancel,
                  restoreFocus: shouldRestoreFocus,
                },
                bubbles: true,
                cancelable: true,
                composed: true,
              }),
            );
        }


        const oldClickHandler = Grid.prototype._onClick;
        Grid.prototype._onClick = function _click(e) {

            const boundOldClickHandler = oldClickHandler.bind(this);
            boundOldClickHandler(e);

            /* if (! (e.originalTarget != 'undefined' && e.originalTarget.type != 'undefined' && e.originalTarget.type == "checkbox")) {
                // handled by original grid

            } */
            this._selectionGridSelectRow(e);
            // console.log('SELECTION GRID: Click received and executed.');

        };
        Grid.prototype.old_onNavigationKeyDown = Grid.prototype._onNavigationKeyDown;
        Grid.prototype._onNavigationKeyDown = function _onNavigationKeyDownOverridden(e, key) {

            this.old_onNavigationKeyDown(e, key);

            const ctrlKey = (e.metaKey)?e.metaKey:e.ctrlKey;
            if (e.shiftKey || !ctrlKey) {
                // select on shift down on shift up
                if (key === 'ArrowDown' || key === 'ArrowUp') {

                    const row = Array.from(this.$.items.children).filter(
                        (child) => child.index === this._focusedItemIndex
                    )[0];

                    if (row && typeof row.index != 'undefined') {
                        this._selectionGridSelectRowWithItem(e, row._item, row.index);
                    }

                }
            } // else do nothing
        }


        Grid.prototype.old_onCellNavigation = Grid.prototype._onCellNavigation;
        Grid.prototype._onCellNavigation = function _onCellNavigationOverridden(cell, dx, dy) {
            console.log('_onCellNavigation ' + cell + "  (" +dx+ ", " + dy + ")");
            this.old_onCellNavigation(cell, dx, dy);
        }

        Grid.prototype.old_onRowNavigation = Grid.prototype._onRowNavigation;
        Grid.prototype._onRowNavigation = function _onRowNavigationOverridden(activeRow, dy) {
            console.log('_onRowNavigation ' + activeRow + "  (" + dy + ")");
            this.old_onRowNavigation(activeRow, dy);
        }

        Grid.prototype.old__focusBodyCell = Grid.prototype.__focusBodyCell;
        Grid.prototype.__focusBodyCell = function __focusBodyCellOverridden(param) {
            console.log('__focusBodyCell ' + param );
            console.log(param);
            this.old__focusBodyCell(param);
        }


        Grid.prototype.old_onSpaceKeyDown = Grid.prototype._onSpaceKeyDown;
        Grid.prototype._onSpaceKeyDown = function _onSpaceKeyDownOverriden(e) {
            this.old_onSpaceKeyDown(e);

            const tr = e.composedPath().find((p) => p.nodeName === "TR");
            if (tr && typeof tr.index != 'undefined') {
                const item = tr._item;
                const index = tr.index;

                if (this.selectedItems && this.selectedItems.some((i) => i.key === item.key)) {
                    if (this.$connector) {
                        this.$connector.doDeselection([item], true);
                    } else {
                        this.deselectItem(item);
                    }
                } else {
                    if (this.$server) {
                        this.$server.selectRangeOnly(index, index);
                    } else {
                        this.selectedItems = [];
                        this.selectItem(item);
                    }
                }
            }

        }
    }
});
