package org.modellwerkstatt.turkuforms.components;

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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag("sel-table")
@CssImport(value = "./styles/seltable.css", themeFor = "sel-table")
@JsModule("./src/vcf-sel-table.js")
@JsModule("./src/sel-table.js")
public class SelTable<T> extends Grid<T> {

    /**
     * @see Grid#Grid()
     */
    public SelTable() {
        super();
    }

    /**
     * @param pageSize - the page size. Must be greater than zero.
     * @see Grid#Grid(int)
     */
    public SelTable(int pageSize) {
        super(pageSize);
    }

    /**
     * @param beanType          - the bean type to use, not null
     * @param autoCreateColumns – when true, columns are created automatically for the properties of the beanType
     * @see Grid#Grid(Class, boolean)
     */
    public SelTable(Class<T> beanType, boolean autoCreateColumns) {
        super(beanType, autoCreateColumns);
    }

    /**
     * @param beanType - the bean type to use, not null
     * @see Grid#Grid(Class)
     */
    public SelTable(Class<T> beanType) {
        super(beanType);
    }

    /**
     * Runs the super.onAttach and hides the multi selection column afterwards (if necessary).
     *
     * @param attachEvent event
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (this.getSelectionModel() instanceof SelectionModel.Multi) {
            hideMultiSelectionColumn();
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
        if (index > 0) {
            int colIndex = (column != null) ? getColumns().indexOf(column) : 0;
            // delay the call of focus on cell if it's used on the same round trip (grid creation + focusCell)
            this.getElement().executeJs("setTimeout(function() { $0.focusOnCell($1, $2) });", getElement(), index, colIndex);
        }
    }


    private int getIndexForItem(T item) {
        return getItemsInOrder().indexOf(item);
    }

    private List<T> getItemsInOrder() {
        DataCommunicator<T> dataCommunicator = super.getDataCommunicator();
        Method fetchFromProvider;
        Method getDataProviderSize;
        try {
            fetchFromProvider = DataCommunicator.class.getDeclaredMethod("fetchFromProvider", int.class, int.class);
            getDataProviderSize = DataCommunicator.class.getDeclaredMethod("getDataProviderSize");
            fetchFromProvider.setAccessible(true);
            getDataProviderSize.setAccessible(true);
            int size = (Integer) getDataProviderSize.invoke(dataCommunicator);
            return ((Stream<T>) fetchFromProvider.invoke(dataCommunicator, 0, size)).collect(Collectors.toList());
        } catch (Exception ignored) {
        }
        return new ArrayList<>();
    }

    private String getColumnInternalId(Column<T> column) {
        Method getInternalId;
        try {
            getInternalId = Column.class.getDeclaredMethod("getInternalId");
            getInternalId.setAccessible(true);
            return (String) getInternalId.invoke(column);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        throw new IllegalArgumentException("getInternalId");
    }

    /**
     * Select the range and keep the other items selected
     *
     * @param fromIndex
     * @param toIndex
     */
    @ClientCallable
    private void selectRange(int fromIndex, int toIndex) {
        GridSelectionModel<T> model = getSelectionModel();
        if (model instanceof GridMultiSelectionModel) {
            DataCommunicator<T> dataCommunicator = super.getDataCommunicator();
            Method fetchFromProvider;
            try {
                fetchFromProvider = DataCommunicator.class.getDeclaredMethod("fetchFromProvider", int.class, int.class);
                fetchFromProvider.setAccessible(true);
                asMultiSelect().select(((Stream<T>) fetchFromProvider.invoke(dataCommunicator, Math.min(fromIndex, toIndex), Math.max(fromIndex,
                        toIndex) - Math.min(fromIndex, toIndex) + 1)).collect(Collectors.toList()));
            } catch (Exception ignored) {
                ignored.printStackTrace();
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
        GridSelectionModel<T> model = getSelectionModel();
        if (model instanceof GridMultiSelectionModel) {
            int from = Math.min(fromIndex, toIndex);
            int to = Math.max(fromIndex, toIndex);
            DataCommunicator<T> dataCommunicator = super.getDataCommunicator();
            Method fetchFromProvider;
            try {
                fetchFromProvider = DataCommunicator.class.getDeclaredMethod("fetchFromProvider", int.class, int.class);
                fetchFromProvider.setAccessible(true);
                Set<T> newSelectedItems = ((Stream<T>) fetchFromProvider.invoke(dataCommunicator, from, to - from + 1)).collect(Collectors.toSet());
                HashSet<T> oldSelectedItems = new HashSet<>(getSelectedItems());
                oldSelectedItems.removeAll(newSelectedItems);
                asMultiSelect().updateSelection(newSelectedItems, oldSelectedItems);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    @Override
    protected void setSelectionModel(GridSelectionModel<T> model, SelectionMode selectionMode) {
        if (selectionMode == SelectionMode.MULTI) {
            hideMultiSelectionColumn();
        }
        super.setSelectionModel(model, selectionMode);
    }

    /**
     * Runs a JavaScript snippet to hide the multi selection / checkbox column on the client side. The column
     * is not removed, but set to "hidden" explicitly.
     */
    protected void hideMultiSelectionColumn() {
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
    public void addThemeVariants(SelTableVariant... variants) {
        getThemeNames().addAll(Stream.of(variants)
                .map(SelTableVariant::getVariantName).collect(Collectors.toList()));
    }

    /**
     * Removes theme variants from the component.
     *
     * @param variants theme variants to remove
     */
    public void removeThemeVariants(SelTableVariant... variants) {
        getThemeNames().removeAll(Stream.of(variants)
                .map(SelTableVariant::getVariantName).collect(Collectors.toList()));
    }
}
