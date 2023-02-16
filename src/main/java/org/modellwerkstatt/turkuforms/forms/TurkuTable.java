package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import org.modellwerkstatt.dataux.runtime.extensions.ITableCellStringConverter;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenSelControlled;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TableForm;
import org.modellwerkstatt.dataux.runtime.utils.MoJSON;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.LeftRight;
import org.modellwerkstatt.turkuforms.util.OverflowMenu;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

public class TurkuTable<DTO> extends VerticalLayout implements IToolkit_TableForm<DTO> {
    private ITurkuFactory factory;

    private LeftRight topPane;
    private FormHeading heading;
    private TextField searchField;
    private Button infoCsvButton;
    private OverflowMenu overflowMenu;

    private Grid<DTO> grid;
    private Label leftLabel;
    private Label rightLabel;
    private boolean hasSummaryLine = false;



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

        infoCsvButton = new Button("*/*");
        infoCsvButton.setTooltipText(factory.getSystemLabel(-1, MoWareTranslations.Key.COPY_CSV_FROM_TABLE));
        infoCsvButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);

        topPane.add(heading);
        topPane.spacer();
        topPane.add(searchField);
        topPane.add(infoCsvButton);

        grid = new Grid<DTO>();

        this.add(topPane, grid);
    }

    @Override
    public void endOfInitializationForElementClass(Class aClass) {

    }

    @Override
    public void setFormController(IGenSelControlled iGenSelControlled) {

    }

    @Override
    public void addTableItemColor(String property, ITableCellStringConverter converter) {

    }

    @Override
    public void addColumn(String property, String label, ITableCellStringConverter converter, int width, boolean editable, boolean folded, boolean important) {

        String template = "<span>Hello</span>";
        Grid.Column<DTO> col = grid.addColumn(LitRenderer.<DTO>of(template).
                withProperty(property, item -> {
                    Turku.l("LitRenderer " + item + "."+ property + " = " + MoJSON.get(item, property));
                    return "" + MoJSON.get(item, property); }));

        col.setHeader(label);
        col.setWidth("" + width + "%");
        col.setResizable(true);
    }

    @Override
    public boolean selectionChanged(IOFXSelection iofxSelection) {
        return false;
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection iofxSelection) {
        grid.setItems(list);
    }



    @Override
    public void forceNotEditable() {

    }

    @Override
    public void setEditPreview() {

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
