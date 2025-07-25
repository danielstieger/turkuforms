package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.gridpro.EditColumnConfigurator;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.modellwerkstatt.addons.desktopgridpro.DesktopGridPro;
import org.modellwerkstatt.addons.desktopgridpro.DesktopGridProDataView;
import org.modellwerkstatt.dataux.runtime.delegates.Delegate;
import org.modellwerkstatt.dataux.runtime.delegates.TableCellBigDecimalConverter;
import org.modellwerkstatt.dataux.runtime.extensions.ITableCellStringConverter;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenSelControlled;
import org.modellwerkstatt.dataux.runtime.genspecifications.Menu;
import org.modellwerkstatt.dataux.runtime.genspecifications.Table;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TableForm;
import org.modellwerkstatt.dataux.runtime.utils.MoJSON;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.dataux.runtime.utils.ValueObjectReplacementFacility;
import org.modellwerkstatt.objectflow.runtime.IOFXMetaRangeScale;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.objectflow.runtime.Selection;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private DesktopGridPro<DTO> grid;
    private GridMultiSelectionModel<DTO> selectionModel;
    private DesktopGridProDataView<DTO> dataView;

    private IGenSelControlled genFormController;
    private List<TableColumn> colInfo = new ArrayList<>();
    private List<Grid.Column<DTO>> gridColumns;
    private int firstEditableCol = -1;
    private boolean editPreview = false;
    private boolean generallyEditable = true;

    private Label leftLabel;
    private Label rightLabel;
    private boolean hasSummaryLine = false;
    private boolean selectionHandlerEnabled = true;
    private String cssRulesToAdd = "";



    public TurkuTable(ITurkuAppFactory fact) {
        factory = fact;
        Peculiar.shrinkSpace(this);
        this.setSizeFull();
        this.getStyle().set("gap", "0");
        addClassName("TurkuTable");

        topPane = new LeftRight("TurkuHeadingTopPane");
        heading = new FormHeading();

        searchField = new TextField();
        searchField.setAutoselect(true);
        searchField.setClearButtonVisible(true);
        searchField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        Peculiar.useDummyEnterHk(searchField);

        infoCsvButton = new Button("*/*");
        infoCsvButton.setTooltipText(factory.getSystemLabel(-1, MoWareTranslations.Key.COPY_CSV_FROM_TABLE));
        infoCsvButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        infoCsvButton.addClickListener(event -> {
            UI.getCurrent().getPage().executeJs("turku.copyToClipboard($0, $1)", this, this.generateCsv());
        });

        topPane.add(heading);
        topPane.spacer();
        topPane.add(searchField);
        topPane.add(infoCsvButton);

        grid = new DesktopGridPro<>();
        grid.setEditOnClick(true);
        grid.setEnterNextRow(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setThemeName("dense");
        // grid.addThemeName("no-border");
        grid.addThemeName("row-stripes");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND );


        Peculiar.useGridShortcutHk(grid, "C", event -> {
            UI.getCurrent().getPage().executeJs("turku.copyToClipboard($0, $1)", this, this.generateCsv());
        });

        selectionModel = (GridMultiSelectionModel<DTO>) grid.getSelectionModel();

        selectionModel.addMultiSelectionListener(event -> {
            if (selectionHandlerEnabled) {
                selectionHandlerEnabled = false;

                Set<DTO> allSelected = event.getAllSelectedItems();
                Turku.l("TukruTable.selectionModel.addMultiSelectionListener() Pushing " + allSelected.size() + " selected to SelCrtl.");

                Selection sel;
                // sel.setIssuer(TurkuTable.this.hashCode());

                if (allSelected.size() > 0) {
                    sel = new Selection(dtoClass, new ArrayList(allSelected));

                } else {
                    sel = new Selection(dtoClass);

                }
                genFormController.pushSelection(sel);
                adjustTableInformation("", true);

                selectionHandlerEnabled = true;
            }
        });

        Peculiar.useGridShortcutHk(grid, "A", event -> { selectionModel.selectAll(); });
        Peculiar.useGridShortcutHk(grid, "END", event -> { grid.scrollToIndex(dataView.getFilteredTotalCount()-1); });
        Peculiar.useGridShortcutHk(grid, "HOME", event -> { grid.scrollToIndex(0); });

        this.add(topPane, grid);


        dataView = new DesktopGridProDataView<>();
        searchField.addValueChangeListener(e -> {
                    Turku.l("SearchField.valueChange(LAZY) for '"+ e.getValue() + "'");
                    dataView.setSearchText(e.getValue());

                    Set<DTO> curSel = selectionModel.getSelectedItems();
                    if (dataView.updateFilterList(grid, curSel)) {
                        // new GridListDataView cleared selections
                        selectionModel.updateSelection(curSel, Collections.emptySet());
                        adjustTableInformation("",true);

                    }else{
                        adjustTableInformation("",false);

                    }
                });

        dataView.setFilterMethod((item, text) -> {

            for (int i=0; i < gridColumns.size(); i++) {
                if (gridColumns.get(i).isVisible()) {
                    TableColumn col = colInfo.get(i);

                    String viewed = col.mowareConverter.convert(MoJSON.get(item, col.propertyName));
                    if (viewed.toLowerCase().replace(".", "").contains(text)) {
                        return true;
                    }
                }
            }
            return false;
        });


        topPane.addClickListener(event -> {
            if (event.getClickCount() == 2) {
                for(int i=0; i < colInfo.size(); i++) {
                    TableColumn col = colInfo.get(i);
                    if (col.widthInPercent == 0) {

                        Grid.Column<DTO> column = gridColumns.get(i);
                        if (column.isVisible()) {
                            column.setVisible(false);
                            column.setWidth("0%");
                        } else {
                            column.setVisible(true);
                            column.setWidth("5%");
                        }
                    }

                }
            }
        });
    }

    @Override
    public void endOfInitializationForElementClass(Class theDto) {
        dtoClass = theDto;

        int remainingWidth = 99;
        for (int i = 0; i < colInfo.size(); i++) {
            boolean lastCol = (i == colInfo.size() - 1);
            TableColumn col = colInfo.get(i);

            int width = col.widthInPercent;

            if (lastCol) {
                width = remainingWidth;
            } else {
                remainingWidth -= width;
            }

            grid.getColumns().get(col.position).setWidth(width + "%");
            if (lastCol && width > 40) {
                grid.getColumns().get(col.position).setTextAlign(ColumnTextAlign.START);
            }
        }


        if (firstEditableCol >= 0) {
            searchField.setEnabled(false);
            infoCsvButton.setEnabled(false);
            searchField.setVisible(false);
            grid.getColumns().forEach(it -> { it.setSortable(false); });

            grid.getElement().addEventListener("cell-edit-started", e -> {
                String eventData = "-";
                int idx = -1;
                try {
                    grid.disableGlobalEsc();

                    eventData = e.getEventData().toString();
                    idx = grid.getRowToSelectWhileEdit(e.getEventData());

                    Turku.l("TurkuTable.eventListener(cell-edit-started) idx " +  idx + " from " + eventData);

                    if (idx > 0) {
                        selectionHandlerEnabled = false;
                        grid.deselectAll();
                        selectionHandlerEnabled = true;
                        grid.select(dataView.getItem(idx - 1));
                    }

                } catch (IndexOutOfBoundsException ex) {
                    System.err.println("TurkuTable: We have an ioobe at " + dtoClass.getSimpleName() + " " + generallyEditable + "/" + firstEditableCol + "\n" + dataView.debugInfo() + " with idx " + idx + " from " + eventData);
                    ex.printStackTrace();
                }

            });


            // Open: grid.getEditor().addCancelListener() is not working.
            grid.getElement().addEventListener("cell-edit-stopped", e -> {
                grid.enableGlobalEsc();
            });
        }

        gridColumns = grid.getColumns();

        if (!"".equals(cssRulesToAdd)) {
            grid.ensureColorStylesPresent(cssRulesToAdd);
        }
    }


    @Override
    public void setFormController(IGenSelControlled iGenSelControlled) {
        genFormController = iGenSelControlled;
    }

    @Override
    public void addTableItemColor(String property, ITableCellStringConverter converter) {

    }

    private BigDecimal validate(Object item, String newValue, ITableCellStringConverter<?> genConverter, String property) throws ValidationException {
        int langIndex = -1;
        BigDecimal bdValue = null;
        TableCellBigDecimalConverter converter = (TableCellBigDecimalConverter) genConverter;

        if (! generallyEditable) {
            String msg = factory.getSystemLabel(langIndex, MoWareTranslations.Key.COMMAND_IN_READONLY);
            throw new ValidationException(msg);
        }

        try {
            bdValue = converter.convertBack(newValue);

        } catch(Exception e) {
            String msg = String.format(factory.getSystemLabel(langIndex, MoWareTranslations.Key.DECIMAL_VALIDATION_ERR), converter.formatterToLocalizedPattern(), newValue);
            msg += " " + factory.getSystemLabel(langIndex, MoWareTranslations.Key.NOT_TAKEN_OVER_ADDON);
            throw new ValidationException(msg);
        }



        IOFXMetaRangeScale<BigDecimal> meta = MoJSON.get(item, Delegate.getMetaDataAccessorToPath(property));
        String errText = null;

        if (meta.getMin() != null && meta.getMax() != null) {
            if (bdValue.compareTo(meta.getMin()) < 0 || bdValue.compareTo(meta.getMax()) > 0) {
                errText = String.format(factory.getSystemLabel(langIndex, MoWareTranslations.Key.DECIMAL_BETWEEN_ERR), meta.getMin().toString(), meta.getMax().toString());
            }
        } else if (meta.getMin() != null) {
            if (bdValue.compareTo(meta.getMin()) < 0) {
                errText = String.format(factory.getSystemLabel(langIndex, MoWareTranslations.Key.DECIMAL_MINIMUM_ERR), meta.getMin().toString());
            }
        } else if (meta.getMax() != null) {
            if (bdValue.compareTo(meta.getMax()) > 0) {
                errText = String.format(factory.getSystemLabel(langIndex, MoWareTranslations.Key.DECIMAL_MAXIMUM_ERR), meta.getMax().toString());
            }
        }

        if (errText == null && meta.getScale() != null) {
            // check num of decimal points
            if (bdValue.setScale(meta.getScale(), RoundingMode.HALF_DOWN).compareTo(bdValue) != 0) {
                errText = String.format(factory.getSystemLabel(langIndex, MoWareTranslations.Key.DECIMAL_SCALE_ERR), meta.getScale().toString());
            }
        }

        if (errText != null) {
            errText += " " + factory.getSystemLabel(langIndex, MoWareTranslations.Key.NOT_TAKEN_OVER_ADDON);
            throw new ValidationException(errText);
        }

        return bdValue;
    }

    @Override
    public void addColumn(String property, String label, ITableCellStringConverter<?> converter, int width, boolean editable, boolean folded, boolean important) {

        if (folded) { width = 0; }
        colInfo.add(new TableColumn(colInfo.size(), property, label, converter, width, !folded));

        Grid.Column<DTO> col;
        if (editable) {
            if (firstEditableCol < 0) { firstEditableCol = colInfo.size() - 1; }

            EditColumnConfigurator<DTO> editableCol = grid.addEditColumn(item -> converter.convert(MoJSON.get(item, property)) );
            editableCol.text((item, newValue) ->
                    {

                        try {
                            BigDecimal val = validate(item, newValue, converter, property);
                            ValueObjectReplacementFacility.put(item, property, val);

                        } catch (ValidationException e) {
                            Notification n = Notification.show(e.getMessage() + "\n", 4000, Notification.Position.TOP_END);
                            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        }
                    });

            col = editableCol.getColumn();

        } else {
            String litPropName = Workarounds.litPropertyName(property);
            String[] colorMap = converter.getColorMapOrNull();
            boolean dynamicColor = converter.hasDynamicColor();
            String template = "${item." + litPropName + "}";


            if (important && dynamicColor) {
                template = "<span class=\"TrkCelImp\" style=\"color:${item." + litPropName + "__Color}\">${item." + litPropName + "}</span>";

            } else if (important && colorMap == null) {
                template = "<span class=\"TrkCelImp\">${item." + litPropName + "}</span>";

            } else if (important) {
                template = "<span class=\"TrkCelColImp\">${item." + litPropName + "}</span>";

            } else if (dynamicColor) {
                template = "<span style=\"color:${item." + litPropName + "__Color}\">${item." + litPropName + "}</span>";

            } else if (colorMap != null) {
                // not important, but we need a span
                template = "<span class=\"TrkCelCol\">${item." + litPropName + "}</span>";
            }


            if (dynamicColor) {

                col = grid.addColumn(LitRenderer.<DTO>of(template).
                        withProperty(litPropName, item -> {
                            return converter.convert(MoJSON.get(item, property));

                        }).withProperty(litPropName + "__Color", item -> {
                            return converter.getBgColor(MoJSON.get(item, property));

                        }));

            } else if (colorMap != null) {
                col = grid.addColumn(LitRenderer.<DTO>of(template).
                        withProperty(litPropName, item -> {
                            return converter.convert(MoJSON.get(item, property));
                        }));

                col.setClassNameGenerator(item -> {
                    String color = converter.getBgColor(MoJSON.get(item, property));

                    if (color != null) {
                        return "TkuCol" + color.substring(1);
                    }
                    return "";
                });

                for (String color: colorMap) {
                    if (color == null) { break; }
                    if (cssRulesToAdd.contains(color)) { continue; }
                    cssRulesToAdd += ".TkuCol" + color.substring(1) + " {--turku-CelCol:" + color + ";--turku-CelColBg:" + color + "20;}";
                }


            } else {
                col = grid.addColumn(LitRenderer.<DTO>of(template).
                        withProperty(litPropName, item -> {
                            return converter.convert(MoJSON.get(item, property));
                        }));
            }

        }



        if (folded) { col.setVisible(false); }
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

    @Override
    public boolean selectionChanged(IOFXSelection<DTO> iofxSelection) {

        // TODO: WME initialization BUG in spot ..
        try {
            boolean issuedFromSelectionHandler = iofxSelection.getIssuer() == this.hashCode();

            // Turku.l("TurkuTable.selectionChanged() " + iofxSelection + "/ " + iofxSelection.getObjectOrNull() + " ignore: " + issuedFromSelectionHandler);
            // No longer? if (issuedFromSelectionHandler) { return true; }

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

        } catch (NullPointerException ex) {
            throw new RuntimeException("Class " + dtoClass.getSimpleName() + " @ " + genFormController.getClass().getSimpleName() + " 2select " + iofxSelection.getObjectOrNull() + " dv " + dataView.getFilteredList(), ex);
        }
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection<DTO> iofxSelection) {

        selectionHandlerEnabled = false;

        boolean allSelFound = dataView.setNewList(grid, list, iofxSelection.getObjects());
        selectionHandlerEnabled = true;

        // Turku.l("TurkuTable.loadList() "  + list.size() + ", all sel found " + allSelFound + ", " + iofxSelection + " / " + iofxSelection.getObjectOrNull());
        // Turku.l("                      " + iofxSelection.getIssuer() + " == " + this.hashCode());

        // Und was ist mit sort order?
        if (allSelFound) {
            // Besser direkt setzen, statt ueber selectionChanged?
            selectionChanged(iofxSelection);

        } else {
            // Shouldn't we report on selection crtl, that selection was not found?
            adjustTableInformation("", false);

        }
    }


    void adjustTableInformation(String debugSt, boolean selOfSelCrtlSameAsLocal) {

        Set<DTO> selection = selectionModel.getSelectedItems();
        int total = dataView.getFilteredTotalCount();
        int numSelection = selection.size();

        if (!selOfSelCrtlSameAsLocal) { debugSt += " ? "; }


        if (numSelection == 0) {

        } else if (numSelection == 1) {
            // debugSt += " " + (dataView.getIndex(selection.iterator().next()) + 1);

        } else {
            debugSt += numSelection + " / ";
        }

        infoCsvButton.setText(debugSt + total);
    }


    @Override
    public void forceNotEditable() {
        generallyEditable = false;
    }

    @Override
    public void setEditPreview() {
        editPreview = true;
        searchField.setEnabled(false);
        infoCsvButton.setEnabled(false);
        searchField.setVisible(false);
        grid.getColumns().forEach(it -> { it.setSortable(false); });

        grid.setEditPreviewMode();
    }


    @Override
    public Object myRequestFocus() {

        Optional<DTO> firstSelected = selectionModel.getFirstSelectedItem();
        // Turku.l("TurkuTable.myRequestFocus(): firstSelected is " + firstSelected);

        // scrolling needed?
        if ((factory.isScrollAdjust() || firstEditableCol >= 0 || editPreview) && firstSelected.isPresent()) {
            int idx = dataView.getIndex(firstSelected.get());
            if (idx > 0) {
                if (idx < (dataView.getFilteredTotalCount() -7)) {
                    idx -= 7;
                }
                grid.scrollToIndex(idx);
            }
        }


        if (editPreview) {
            // should not take over fokus
            return null;

        } else if (firstSelected.isPresent()) {
            grid.focus();

        }
        // take over focus anyway
        return grid;
    }

    @Override
    public void afterFullUiInitialized() {
        Turku.l("TurkuTable.afterFullUiInitialized() called");
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
        LeftRight lr = new LeftRight("TurkuTableSummaryLine");
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
        grid.addItemDoubleClickListener(e -> {
            contextMenu.execDoubleClick();
        });
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
    public void rootForm() {
        heading.inRootPos();
    }

    @Override
    public void gcClear() {
        selectionHandlerEnabled = false;
        dataView.gcClear();
        grid.setItems(new ArrayList<>());
        grid.removeAllColumns();
        grid.gcClean();

        overflowMenu = null;
        contextMenu = null;

        factory = null;
    }



    public String generateCsv() {

        List<DTO> objectsToExport = dataView.getSelectionInSync(selectionModel.getSelectedItems());

        for (int i=0; i < gridColumns.size(); i++) {
            colInfo.get(i).visible = gridColumns.get(i).isVisible();
        }

        return genFormController.convertAsCsv(dataView.getOriginalList(), objectsToExport, colInfo);
    }
}
