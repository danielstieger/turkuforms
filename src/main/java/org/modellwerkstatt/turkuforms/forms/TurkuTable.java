package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.gridpro.EditColumnConfigurator;
import com.vaadin.flow.component.gridpro.GridProVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.modellwerkstatt.dataux.runtime.extensions.ITableCellStringConverter;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenSelControlled;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TableForm;
import org.modellwerkstatt.dataux.runtime.utils.MoJSON;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.dataux.runtime.utils.ValueObjectReplacementFacility;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.objectflow.runtime.Selection;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.components.SelectionGrid;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.*;

@SuppressWarnings("unchecked")
public class TurkuTable<DTO> extends VerticalLayout implements IToolkit_TableForm<DTO> {
    private ITurkuAppFactory factory;

    private LeftRight topPane;
    private FormHeading heading;
    private TextField searchField;
    private Button infoCsvButton;
    private TurkuMenu overflowMenu;
    private MenuContext contextMenu;

    private Class dtoClass;
    private SelectionGrid<DTO> grid;
    private GridMultiSelectionModel<DTO> selectionModel;
    private TurkuTableDataView<DTO> dataView;

    private IGenSelControlled genFormController;
    private List<TurkuTableCol> colInfo = new ArrayList<>();

    private Label leftLabel;
    private Label rightLabel;
    private boolean hasSummaryLine = false;
    private boolean selectionHandlerEnabled = true;



    public TurkuTable(ITurkuAppFactory fact) {
        factory = fact;
        Peculiar.shrinkSpace(this);
        this.setSizeFull();
        this.getStyle().set("gap", "0");

        topPane = new LeftRight();
        heading = new FormHeading();

        searchField = new TextField();
        searchField.setAutoselect(true);
        searchField.setClearButtonVisible(true);
        searchField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);

        infoCsvButton = new Button("*/*");
        infoCsvButton.setTooltipText(factory.getSystemLabel(-1, MoWareTranslations.Key.COPY_CSV_FROM_TABLE));
        infoCsvButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        infoCsvButton.addClickListener(event -> {
            UI.getCurrent().getPage().executeJs("turku.copyToClipboard($0, $1)", this, dataView.generateCsv());
        });

        topPane.add(heading);
        topPane.spacer();
        topPane.add(searchField);
        topPane.add(infoCsvButton);

        grid = new SelectionGrid<>();
        grid.setEditOnClick(true);
        grid.setEnterNextRow(true);
        grid.addThemeVariants(GridProVariant.LUMO_HIGHLIGHT_EDITABLE_CELLS);

        /* grid.getElement().addEventListener("selected-items-changed", e -> {
            Turku.l("Turkutable.grid.addEventListener() SELECTED-ITEMS-CHANGED " + e.getType() + " / " + e.getEventData());
        }); */

        grid.getElement().addEventListener("cell-edit-started", e -> {
            int idx = Workarounds.getRowToSelectWhileEdit(e.getEventData());
            Turku.l("Turkutable.grid.addEventListener() cell-edit-started. We have to select " + idx);
            if (idx > 0) {
                selectionHandlerEnabled = false;
                grid.deselectAll();
                selectionHandlerEnabled = true;

                grid.select(dataView.getItem(idx - 1));
            }
        });

        grid.getElement().addEventListener("item-property-changed", e -> {
           Turku.l("Turkutable.grid.addEventListener(): item-property-changed - was this an esc? Fires only on value change?  ");
        });




        // grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setThemeName("dense");

        selectionModel = (GridMultiSelectionModel<DTO>) grid.getSelectionModel();
        selectionModel.setSelectionColumnFrozen(true);

        selectionModel.addMultiSelectionListener(event -> {
            if (selectionHandlerEnabled) {
                Set<DTO> allSelected = event.getAllSelectedItems();
                Turku.l("TukruTable.selectionModel.addMultiSelectionListener() Pushing " + allSelected.size() + " selected to SelCrtl.");

                Selection sel = new Selection(dtoClass);
                sel.setIssuer(TurkuTable.this.hashCode());

                if (allSelected.size() > 0) {
                    sel.setObjects(new ArrayList(allSelected));
                }
                genFormController.pushSelection(sel);
                adjustTableInformation("", true);
            }
        });
        Peculiar.useGridShortcutHk(grid, "A", event -> { selectionModel.selectAll(); });

        this.add(topPane, grid);


        dataView = new TurkuTableDataView<>(colInfo);
        searchField.addValueChangeListener(e -> {
                    Turku.l("SearchField.valueChange(LAZY) for '"+ e.getValue() + "'");
                    dataView.setSearchText(e.getValue());

                    Set<DTO> curSel = selectionModel.getSelectedItems();
                    if (dataView.updateFilterList(grid, curSel)) {
                        // new GridListDataView cleared selections
                        selectionModel.updateSelection(curSel, Collections.emptySet());
                        // TODO: Not exactly correct. We are loosing the ! in case of empty selections
                        adjustTableInformation("",true);

                    }else{
                        adjustTableInformation("",false);

                    }
                });
    }


    @Override
    protected void onAttach(AttachEvent attachEvent) {
        grid.hideMultiSelectionColumn();
    }

    @Override
    public void endOfInitializationForElementClass(Class theDto) {
        dtoClass = theDto;

        // Workarounds.adjustColWidthToCheckbox(colInfo);
        colInfo.forEach(colInfo -> {
            grid.getColumns().get(colInfo.position).setWidth("" + colInfo.widthInPercent + "%");
        });

    }


    @Override
    public void setFormController(IGenSelControlled iGenSelControlled) {
        genFormController = iGenSelControlled;
    }

    @Override
    public void addTableItemColor(String property, ITableCellStringConverter converter) {

    }

    @Override
    public void addColumn(String property, String label, ITableCellStringConverter<?> converter, int width, boolean editable, boolean folded, boolean important) {

        //TODO: Folded not implemented
        if (folded) {

        } else {
            colInfo.add(new TurkuTableCol(colInfo.size(), property, label, converter, width));

            Grid.Column<DTO> col;
            if (editable) {
                EditColumnConfigurator<DTO> editableCol = grid.addEditColumn(item -> converter.convert(MoJSON.get(item, property)) );
                editableCol.text((item, newValue) ->
                        {
                            try {
                                Object val = converter.convertBack(newValue);
                                ValueObjectReplacementFacility.put(item, property, val);
                            } catch (Exception e) {
                                Notification.show("Text not accepted! " + e.getMessage(), 4000, Notification.Position.TOP_END);
                            }
                        });

                col = editableCol.getColumn();

            } else {
                String litPropName = Workarounds.litPropertyName(property);
                String template = "<span style=\"${item." + litPropName + "Style}\">${item." + litPropName + "}</span>";
                String fontWeight = important ? "font-weight:800;" : "";

                col = grid.addColumn(LitRenderer.<DTO>of(template).
                        withProperty(litPropName, item -> {
                            return converter.convert(MoJSON.get(item, property));
                        }).
                        withProperty(litPropName + "Style", item -> {
                            String color = converter.getBgColor(MoJSON.get(item, property));
                            return color == null ? fontWeight : fontWeight + "color:" + color + ";";
                        }));
            }

            col.setHeader(Workarounds.niceGridHeaderLabel(label));
            col.setResizable(true);
            col.setTextAlign(converter.isRightAligned() ? ColumnTextAlign.END : ColumnTextAlign.START);
            col.setSortable(true);
            col.setComparator((dto1, dto2) -> {
                Comparable v1 = MoJSON.get(dto1, property);
                Comparable v2 = MoJSON.get(dto2, property);
                if (v1 == null && v2 == null) {
                    return 0;
                } else if (v1 == null) {
                    return -1;
                } else if (v2 == null) {
                    return +1;
                } else {
                    return v1.compareTo(v2);
                }
            });
        }
    }

    @Override
    public boolean selectionChanged(IOFXSelection<DTO> iofxSelection) {
        boolean issuedFromSelectionHandler = iofxSelection.getIssuer() == this.hashCode();
        Turku.l("TurkuTable.selectionChanged() " + iofxSelection + " ignore: " + issuedFromSelectionHandler);
        if (issuedFromSelectionHandler) { return true; }

        selectionHandlerEnabled = false;

        selectionModel.deselectAll();

        // There is actually a filteredList
        LinkedHashSet<DTO> selection = new LinkedHashSet<>(iofxSelection.getObjects());
        boolean allSelectionsAvailable = dataView.allSelectionsCurrentlyInFilter(selection);
        if (allSelectionsAvailable) {
            selectionModel.updateSelection(selection, Collections.emptySet());
        }
        adjustTableInformation("", allSelectionsAvailable);

        selectionHandlerEnabled = true;
        return allSelectionsAvailable;
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection<DTO> iofxSelection) {
        Turku.l("TurkuTable.loadList() "  + list.size() + " / " + iofxSelection);

        selectionHandlerEnabled = false;
        // (0) SelCrtl clears selection if sel not in newList
        boolean selCleared = dataView.setNewList(grid, list, iofxSelection);
        selectionHandlerEnabled = true;

        if (selCleared) {
            selectionChanged(iofxSelection);

        } else {
            adjustTableInformation("", false);

        }
    }


    void adjustTableInformation(String debugSt, boolean selOfSelCrtlSameAsLocal) {
        int selCnt = selectionModel.getSelectedItems().size();
        int total = dataView.getFilteredTotalCount();
        if (!selOfSelCrtlSameAsLocal) { debugSt += " !"; }
        infoCsvButton.setText(debugSt + " " + selCnt + " | " + total);
    }


    @Override
    public void forceNotEditable() {
        // TODO: Editable Grid not implemented yet
    }

    @Override
    public void setEditPreview() {
        // TODO: Edit Preview not implemented yet.
    }


    @Override
    public Object myRequestFocus() {
        grid.getElement().executeJs(
                "turku.focusGrid($0)",
                grid.getElement());

        Optional<DTO> first = selectionModel.getFirstSelectedItem();
        Turku.l("TurkuTable.myRequestFocus() called: " + first);
        // first.ifPresent(dto -> grid.focusOnCell(dto));
        grid.focus();
        return grid;
    }

    @Override
    public void afterFullUiInitialized() {
        Turku.l("TurkuTable.afterFullUiInitialized() called");
    }

    private void clearLocalSelectionWithoutPush() {
        Turku.l("TurkuTable.clearLocalSelectionWithoutPush() clearing local selection!");
        if (selectionHandlerEnabled) {
            selectionHandlerEnabled = false;
            selectionModel.deselectAll();
            selectionHandlerEnabled = true;
        } else {
            selectionModel.deselectAll();
        }
    }

    @Override
    public void setSelectionSummaryLineText(String s) {
        if (!hasSummaryLine) { initSummaryLine(); }
        rightLabel.setText(s);
    }

    @Override
    public void setTableSummaryLineText(String s) {
        if (!hasSummaryLine) { initSummaryLine(); }
        leftLabel.setText(s);
    }

    private void initSummaryLine(){
        leftLabel = new Label();
        rightLabel = new Label();
        LeftRight lr = new LeftRight();
        lr.add(leftLabel);
        lr.spacer();
        lr.add(rightLabel);
        this.add(lr);
        hasSummaryLine = true;
    }

    @Override
    public void addMenuAndSetButtons(Menu menuSub) {
        overflowMenu = new TurkuMenu();
        overflowMenu.initialize(factory, menuSub);
        topPane.add(overflowMenu);

        contextMenu = new MenuContext<DTO>(factory, grid, menuSub);
        grid.addItemDoubleClickListener(e -> { contextMenu.execDoubleClick(); });
    }

    @Override
    public void setTitleText(String s) {
        heading.setHeading(s);
    }

    @Override
    public void setProblems(List<IOFXProblem> list) {
        heading.flag(list);
    }

    @Override
    public void gcClear() {
        factory = null;
    }
}
