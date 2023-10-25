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
/* eslint-disable no-invalid-this */

export function _selectionGridSelectRow(e) {

    const vaadinTreeToggle = e.composedPath().find((p) => p.nodeName === "VAADIN-GRID-TREE-TOGGLE");
    if (vaadinTreeToggle) {
        // don't select, it will expand/collapse the node
        // reset the last item
        this.rangeSelectRowFrom = -1;
    } else {
        const tr = e.composedPath().find((p) => p.nodeName === "TR");
        if (tr && typeof tr.index != 'undefined') {
            const item = tr._item;
            const index = tr.index;

            this._selectionGridSelectRowWithItem(e, item, index);
        }
    }
}
export function _selectionGridSelectRowWithItem(e, item, index) {
    const ctrlKey = (e.metaKey)?e.metaKey:e.ctrlKey; //(this._ios)?e.metaKey:e.ctrlKey;

    // if click select only this row
    if (!ctrlKey && !e.shiftKey) {
        console.log('(1) selectRangeOnly(' + index + ', ' + index + ')');
        if (this.$server) {
            this.$server.selectRangeOnly(index, index);
        } else {
            this.selectedItems = [];
            this.selectItem(item);
        }
    }

    // if ctrl click
    // TODO: Why double update this? Dan added an else if
    console.log('(2) shiftKey ' + e.shiftKey + ', rangeSelectRowFrom ' + this.rangeSelectRowFrom);
    if (e.shiftKey && this.rangeSelectRowFrom >= 0) {
        console.log('(3) index is ' + index + " diff " + (this.rangeSelectRowFrom - index));
        if((this.rangeSelectRowFrom - index) !== 0) { // clear text selection, if multiple rows are selected using shift
            const sel = window.getSelection ? window.getSelection() : document.selection;
            if (sel) {
                if (sel.removeAllRanges) {
                    sel.removeAllRanges();
                } else if (sel.empty) {
                    sel.empty();
                }
            }
        }

        console.log('(4) selectRangeOnly(' + this.rangeSelectRowFrom + ', ' + index + ')');
        if (!ctrlKey) {
            if (this.$server) {
                this.$server.selectRangeOnly(this.rangeSelectRowFrom, index);
            }
        } else {
            if (this.$server) {
                this.$server.selectRange(this.rangeSelectRowFrom, index);
            }
        }

    } else {
        if (!ctrlKey) {
            console.log('(5) selectRangeOnly(' + index + ', ' + index + ')');

            if (this.$server) {
                // TODO: Why double update this ?
                this.$server.selectRangeOnly(index, index);
            }

        } else {
            console.log('(6) selectedItems ' + this.selectedItems + ', item.key ' + item.key);

            if (this.selectedItems && this.selectedItems.some((i) => i.key === item.key)) {
                console.log('(7) connectorStuff()');
                if (this.$connector) {
                    this.$connector.doDeselection([item], true);
                } else {
                    this.deselectItem(item);
                }
            } else {
                console.log('(8) selectRange(' + index + ', ' + index + ')');
                if (this.$server) {
                    this.$server.selectRange(index, index);
                }
            }
        }
        console.log('(9) setting rangeSelectRowFrom to ' + index );
        this.rangeSelectRowFrom = index;
    }
}


export function _getItemOverriden(index, el) {
    if (index >= this._effectiveSize) {
        return;
    }
    el.index = index;
    const { cache, scaledIndex } = this._cache.getCacheAndIndex(index);
    const item = cache.items[scaledIndex];
    if (item) {
        this.toggleAttribute("loading", false, el);
        this._updateItem(el, item);
        if (this._isExpanded(item)) {
            cache.ensureSubCacheForScaledIndex(scaledIndex);
        }
    } else {
        this.toggleAttribute("loading", true, el);
        this._loadPage(this._getPageForIndex(scaledIndex), cache);
    }
    /** focus when get item if there is an item to focus **/
    if (this._rowNumberToFocus > -1) {
        if (index === this._rowNumberToFocus) {
            const row = Array.from(this.$.items.children).filter(
                (child) => child.index === this._rowNumberToFocus
            )[0];
            if (row) {
                this._focus();
            }
        }
    }
};
