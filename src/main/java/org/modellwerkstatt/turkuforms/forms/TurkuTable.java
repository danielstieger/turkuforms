package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.extensions.ITableCellStringConverter;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenSelControlled;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TableForm;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.util.List;

public class TurkuTable<DTO> extends VerticalLayout implements IToolkit_TableForm<DTO> {
    private FlexLayout topPane;
    private Grid grid;
    private FlexLayout bottomPane;


    public TurkuTable() {

        this.setSizeFull();


    }

    @Override
    public void endOfInitializationForElementClass(Class aClass) {
        this.add(new Div(new Text("Table for " + aClass.getSimpleName())));
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

    }

    @Override
    public void setProblems(List<IOFXProblem> list) {

    }

    @Override
    public Object myRequestFocus() {
        return null;
    }

    @Override
    public void afterFullUiInitialized() {

    }
}
