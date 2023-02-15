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
import org.modellwerkstatt.dataux.runtime.extensions.ITableCellStringConverter;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenSelControlled;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TableForm;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.util.LeftRight;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

public class TurkuTable<DTO> extends VerticalLayout implements IToolkit_TableForm<DTO> {
    private LeftRight topPane;
    private FormHeading heading;
    private TextField searchField;
    private Button infoCsvButton;

    private Grid grid;
    private FlexLayout bottomPane;
    private ITurkuFactory factory;


    public TurkuTable(ITurkuFactory fact) {
        factory = fact;

        this.setSizeFull();

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

        grid = new Grid<>();

        this.add(topPane, grid);
    }

    @Override
    public void endOfInitializationForElementClass(Class aClass) {

    }

    @Override
    public void setFormController(IGenSelControlled iGenSelControlled) {

    }

    @Override
    public void addTableItemColor(String s, ITableCellStringConverter iTableCellStringConverter) {

    }

    @Override
    public void addColumn(String s, String s1, ITableCellStringConverter iTableCellStringConverter, int i, boolean b, boolean b1, boolean b2) {

    }

    @Override
    public void setSelectionSummaryLineText(String s) {

    }

    @Override
    public boolean selectionChanged(IOFXSelection iofxSelection) {
        return false;
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection iofxSelection) {

    }

    @Override
    public void setTableSummaryLineText(String s) {

    }

    @Override
    public void forceNotEditable() {

    }

    @Override
    public void setEditPreview() {

    }

    @Override
    public void addMenuAndSetButtons(MenuSub menuSub) {

    }

    @Override
    public void gcClear() {

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
    public Object myRequestFocus() {
        return null;
    }

    @Override
    public void afterFullUiInitialized() {

    }
}
