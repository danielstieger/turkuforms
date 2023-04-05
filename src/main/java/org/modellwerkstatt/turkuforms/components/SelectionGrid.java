package org.modellwerkstatt.turkuforms.components;

/*
 * #%L
 * selection-grid-flow
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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.data.provider.DataCommunicator;
import com.vaadin.flow.data.selection.SelectionModel;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
@Tag("vaadin-selection-grid")
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-selection-grid")
@JsModule("./src/vcf-selection-grid.js")
@JsModule("./src/selection-grid.js")
public class SelectionGrid<T> extends Grid<T> {
    private Method dataCommunicatorFetchFromProvider;
    private Method dataCommunicatorGetDataProviderSize;
    private Method columnGetInternalId;


    /**
     * @see Grid#Grid()
     */
    public SelectionGrid() {
        super();
        reflectMethods();
    }

    /**
     * @param pageSize - the page size. Must be greater than zero.
     * @see Grid#Grid(int)
     */
    public SelectionGrid(int pageSize) {
        super(pageSize);
        reflectMethods();
    }

    /**
     * @param beanType          - the bean type to use, not null
     * @param autoCreateColumns – when true, columns are created automatically for the properties of the beanType
     * @see Grid#Grid(Class, boolean)
     */
    public SelectionGrid(Class<T> beanType, boolean autoCreateColumns) {
        super(beanType, autoCreateColumns);
        reflectMethods();
    }

    /**
     * @param beanType - the bean type to use, not null
     * @see Grid#Grid(Class)
     */
    public SelectionGrid(Class<T> beanType) {
        super(beanType);
        reflectMethods();
    }

    private void reflectMethods() {
        try {
            dataCommunicatorFetchFromProvider = DataCommunicator.class.getDeclaredMethod("fetchFromProvider", int.class, int.class);
            dataCommunicatorFetchFromProvider.setAccessible(true);

            dataCommunicatorGetDataProviderSize = DataCommunicator.class.getDeclaredMethod("getDataProviderSize");
            dataCommunicatorGetDataProviderSize.setAccessible(true);

            columnGetInternalId = Column.class.getDeclaredMethod("getInternalId");
            columnGetInternalId.setAccessible(true);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public void scrollToIndex(int rowIndex) {
        super.scrollToIndex(rowIndex);
    }

    /**
     * Focus on the first cell on the row
     *
     * @param item item to scroll and focus
     */
    public void focusOnCell(T item) {
        focusOnCell(item, null);
    }

    /**
     * Focus on the specific column on the row
     *
     * @param item   item to scroll and focus
     * @param column column to focus
     */
    public void focusOnCell(T item, Column<T> column) {
        int index = getIndexForItem(item);
        if (index >= 0) {
            int colIndex = (column != null) ? getColumns().indexOf(column) : 1;
            // delay the call of focus on cell if it's used on the same round trip (grid creation + focusCell)
            this.getElement().executeJs("setTimeout(function() { $0.focusOnCell($1, $2) });", getElement(), index, colIndex);
        }
    }

    private List<T> getItemsInOrder() {
        DataCommunicator<T> dataCommunicator = super.getDataCommunicator();

        try {
            int size = (Integer) dataCommunicatorGetDataProviderSize.invoke(dataCommunicator);
            return ((Stream<T>) dataCommunicatorFetchFromProvider.invoke(dataCommunicator, 0, size)).collect(Collectors.toList());

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private int getIndexForItem(T item) {
        return getItemsInOrder().indexOf(item);
    }

    private String getColumnInternalId(Column<T> column) {
        try {
            return (String) columnGetInternalId.invoke(column);

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Select the range and keep the other items selected
     *
     * @param fromIndex
     * @param toIndex
     */
    @ClientCallable
    private void selectRange(int fromIndex, int toIndex) {
        Turku.l("selectRange() " + fromIndex + " - " + toIndex);

        GridSelectionModel<T> model = getSelectionModel();
        if (model instanceof GridMultiSelectionModel) {
            DataCommunicator<T> dataCommunicator = super.getDataCommunicator();

            try {
                asMultiSelect().select(((Stream<T>) dataCommunicatorFetchFromProvider.invoke(dataCommunicator, Math.min(fromIndex, toIndex), Math.max(fromIndex,
                        toIndex) - Math.min(fromIndex, toIndex) + 1)).collect(Collectors.toList()));

            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Select the range and deselect the other items
     *
     * @param fromIndex
     * @param toIndex
     */
    @ClientCallable
    private void selectRangeOnly(int fromIndex, int toIndex) {
        Turku.l("selectRangeOnly() " + fromIndex + " - " + toIndex);

        GridSelectionModel<T> model = getSelectionModel();
        if (model instanceof GridMultiSelectionModel) {
            int from = Math.min(fromIndex, toIndex);
            int to = Math.max(fromIndex, toIndex);
            DataCommunicator<T> dataCommunicator = super.getDataCommunicator();

            try {
                Set<T> newSelectedItems = ((Stream<T>) dataCommunicatorFetchFromProvider.invoke(dataCommunicator, from, to - from + 1)).collect(Collectors.toSet());
                HashSet<T> oldSelectedItems = new HashSet<>(getSelectedItems());
                oldSelectedItems.removeAll(newSelectedItems);
                asMultiSelect().updateSelection(newSelectedItems, oldSelectedItems);

            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Runs a JavaScript snippet to hide the multi selection / checkbox column on the client side. The column
     * is not removed, but set to "hidden" explicitly.
     */
    public void hideMultiSelectionColumn() {
        getElement().getNode().runWhenAttached(ui ->
                ui.beforeClientResponse(this, context ->
                        getElement().executeJs(
                                "if (this.querySelector('vaadin-grid-flow-selection-column')) {" +
                                        " this.querySelector('vaadin-grid-flow-selection-column').hidden = true }")));
    }

    /**
     * Adds theme variants to the component.
     *
     * @param variants theme variants to add
     */
    public void addThemeVariants(SelectionGridVariant... variants) {
        getThemeNames().addAll(Stream.of(variants)
                .map(SelectionGridVariant::getVariantName).collect(Collectors.toList()));
    }

    /**
     * Removes theme variants from the component.
     *
     * @param variants theme variants to remove
     */
    public void removeThemeVariants(SelectionGridVariant... variants) {
        getThemeNames().removeAll(Stream.of(variants)
                .map(SelectionGridVariant::getVariantName).collect(Collectors.toList()));
    }
}
