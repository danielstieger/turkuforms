package org.modellwerkstatt.addons.desktopgridpro;


import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.data.provider.DataCommunicator;
import elemental.json.JsonObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
@Tag("vaadin-selection-grid")
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-selection-grid")
@CssImport("./styles/gridglobal.css")
@JsModule("./src/vcf-selection-grid.js")
@JsModule("./src/selection-grid.js")
@JsModule("./src/desktop-grid.js")
public class DesktopGridPro<T> extends GridPro<T> {
    private Method dataCommunicatorFetchFromProvider;
    private Method dataCommunicatorGetDataProviderSize;
    private Method columnGetInternalId;

    private ShortcutRegistration gridEscShortCut;


    public DesktopGridPro() {
        super();
        setupGrid();
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        this.hideMultiSelectionColumn();
    }

    @Override
    public void focus() {
        this.getElement().executeJs(
                "modellwerkstatt_desktopgrid.focusGrid($0, true)",
                this.getElement());
    }

    public int getRowToSelectWhileEdit(JsonObject data) {
        JsonObject details = data.getObject("event.detail.item");
        String key = details.getString("key");
        boolean selected = details.hasKey("selected") && details.getBoolean("selected");
        return selected ? -1 : Integer.parseInt(key);
    }

    public void enableGlobalEsc() {
        gridEscShortCut.setEventPropagationAllowed(true);
    }

    public void disableGlobalEsc() {
        gridEscShortCut.setEventPropagationAllowed(false);
    }

    private void setupGrid() {
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

        gridEscShortCut = Shortcuts.addShortcutListener(this, e -> {
            //
        }, Key.ESCAPE).listenOn(this);
        enableGlobalEsc();
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
     * Adds theme variants to the component.
     *
     * @param variants theme variants to add
     */
    public void addThemeVariants(DesktopGridProVariant... variants) {
        getThemeNames().addAll(Stream.of(variants)
                .map(DesktopGridProVariant::getVariantName).collect(Collectors.toList()));
    }

    /**
     * Removes theme variants from the component.
     *
     * @param variants theme variants to remove
     */
    public void removeThemeVariants(DesktopGridProVariant... variants) {
        getThemeNames().removeAll(Stream.of(variants)
                .map(DesktopGridProVariant::getVariantName).collect(Collectors.toList()));
    }
}
