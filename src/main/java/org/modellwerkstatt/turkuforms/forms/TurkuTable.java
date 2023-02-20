package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.*;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import net.bytebuddy.implementation.bytecode.Throw;
import org.modellwerkstatt.dataux.runtime.extensions.ITableCellStringConverter;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenSelControlled;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TableForm;
import org.modellwerkstatt.dataux.runtime.utils.MoJSON;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.objectflow.runtime.OFXConsoleHelper;
import org.modellwerkstatt.objectflow.runtime.Selection;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.LeftRight;
import org.modellwerkstatt.turkuforms.util.OverflowMenu;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.*;

@SuppressWarnings("unchecked")
public class TurkuTable<DTO> extends VerticalLayout implements IToolkit_TableForm<DTO> {
    private ITurkuFactory factory;

    private LeftRight topPane;
    private FormHeading heading;
    private TextField searchField;
    private Button infoCsvButton;
    private OverflowMenu overflowMenu;

    private Class dtoClass;
    private Grid<DTO> grid;
    private GridMultiSelectionModel<DTO> selectionModel;
    private GridListDataView<DTO> gridListDataView;

    private IGenSelControlled genFormController;
    private List<TurkuTableCol> colInfo = new ArrayList<>();

    private Label leftLabel;
    private Label rightLabel;
    private boolean hasSummaryLine = false;
    private boolean selectionHandlerEnabled = true;
    private String filterSearchForWhat = "";



    public TurkuTable(ITurkuFactory fact) {
        factory = fact;

        this.setPadding(false);
        this.setSizeFull();
        this.getStyle().set("gap", "0");

        topPane = new LeftRight();
        heading = new FormHeading();

        searchField = new TextField();
        searchField.setAutoselect(true);
        searchField.setClearButtonVisible(true);
        searchField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        infoCsvButton = new Button("*/*");
        infoCsvButton.setTooltipText(factory.getSystemLabel(-1, MoWareTranslations.Key.COPY_CSV_FROM_TABLE));
        infoCsvButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        topPane.add(heading);
        topPane.spacer();
        topPane.add(searchField);
        topPane.add(infoCsvButton);

        grid = new Grid<DTO>();
        grid.addThemeVariants(GridVariant.LUMO_COMPACT);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        selectionModel = (GridMultiSelectionModel<DTO>) grid.getSelectionModel();

        grid.addItemClickListener(new ComponentEventListener<ItemClickEvent<DTO>>() {
            @Override
            public void onComponentEvent(ItemClickEvent<DTO> event) {
                DTO item = event.getItem();
                if (selectionModel.isSelected(item)) {
                    selectionModel.deselect(item);
                }else{
                    selectionModel.select(item);
                }
            }
        });

        selectionModel.addMultiSelectionListener(event -> {
            if (selectionHandlerEnabled) {
                Set<DTO> allSelected = event.getAllSelectedItems();
                Selection sel = new Selection(dtoClass);
                sel.setIssuer(this.hashCode());

                if (allSelected.size() > 0) {
                    sel.setObjects(new ArrayList(allSelected));
                }
                genFormController.pushSelection(sel);
            }
        });

        this.add(topPane, grid);

        searchField.addValueChangeListener(e -> {
                    filterSearchForWhat = e.getValue();
                    if (gridListDataView != null) {
                        gridListDataView.refreshAll();
                    }
                });
    }

    @Override
    public void endOfInitializationForElementClass(Class theDto) {
        dtoClass = theDto;

        Workarounds.adjustColWidthToCheckbox(colInfo);
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
            colInfo.add(new TurkuTableCol(colInfo.size(), property, converter, width));

            String template = "<span style=\"${item." + property + "Style}\">${item." + property + "}</span>";
            String fontWeight = important ? "font-weight:800;" : "";

            Grid.Column<DTO> col = grid.addColumn(LitRenderer.<DTO>of(template).
                    withProperty(property, item -> { return converter.convert(MoJSON.get(item, property)); }).
                    withProperty(property + "Style", item -> {
                        String color = converter.getBgColor(MoJSON.get(item, property));
                        return color == null ? fontWeight: fontWeight + "color:" + color + ";";
                    }));


            col.setHeader(label);
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
                }else {
                    return v1.compareTo(v2);
                }
            });
        }
    }

    @Override
    public boolean selectionChanged(IOFXSelection<DTO> iofxSelection) {
        Turku.l("TurkuTable.selectionChanged() " + iofxSelection);
        selectionHandlerEnabled = false;
        selectionModel.deselectAll();

        for (DTO obj : iofxSelection.getObjects()) {
            selectionModel.select(obj);
        }

        selectionHandlerEnabled = true;
        return true;
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection<DTO> iofxSelection) {
        Turku.l("TurkuTable.loadList() "  + list.size() + " / " + iofxSelection);

        gridListDataView = grid.setItems(list);
        gridListDataView.addFilter(dto -> {
            int i = 0;
            String viewed = "?";
            try {
                for (; i < colInfo.size(); i++) {
                    TurkuTableCol col = colInfo.get(i);
                    viewed = col.mowareConverter.convert(MoJSON.get(dto, col.propertyName));
                    if (viewed.toLowerCase().replace(".", "").contains(filterSearchForWhat)) {
                        return true;
                    }
                }
            }catch (Throwable t) {
                Turku.l("DTO " + dto + " @ prop " + colInfo.get(i).propertyName + " - " + viewed);
                Turku.l("TABLE LOADLIST: " + OFXConsoleHelper.stackTrace2String(t));
            }

            return false;
        });
        selectionChanged(iofxSelection);
    }


    void adjustTableInformation(boolean allSelectedItemsFound) {

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
        return null;
    }

    @Override
    public void afterFullUiInitialized() {

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
    public void addMenuAndSetButtons(MenuSub menuSub) {
        overflowMenu = new OverflowMenu();
        overflowMenu.initialize(factory, menuSub);
        topPane.add(overflowMenu);
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
