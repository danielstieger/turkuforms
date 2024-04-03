package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import org.modellwerkstatt.dataux.runtime.core.FocusController;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TabForm;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.util.List;

public class TurkuTabForm<DTO> extends TabSheet implements IToolkit_TabForm<DTO> {
    private FocusController<IToolkit_Form> focusController;


    public TurkuTabForm() {
        super();

        this.setSizeFull();
        addClassName("TurkuTabForm");

        focusController = new FocusController<>();
        addThemeVariants(TabSheetVariant.LUMO_TABS_MINIMAL);

        this.addSelectedChangeListener(event ->{
            Tab current = event.getSelectedTab();
            int index = this.getIndexOf(current);
            focusController.myRequestFocusOnChild(index);
        });
    }


    @Override
    public void addTab(IToolkit_Form form, String label) {
        focusController.addChild(form);
        Component cmpt = (Component) form;
        Tab theTab = this.add(label, cmpt);
    }

    @Override
    public IToolkit_TabForm<DTO> getToolkitImplementation() {
        return this;
    }

    @Override
    public boolean selectionChanged(IOFXSelection<DTO> iofxSelection) {
        return false;
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection<DTO> iofxSelection) {

    }

    @Override
    public void gcClear() {
        focusController.gcClear();
    }

    @Override
    public void setTitleText(String s) {
        throw new RuntimeException("Not implemented for TurkuTabForm");
    }

    @Override
    public void setProblems(List<IOFXProblem> list) {
        throw new RuntimeException("Not implemented for TurkuTabForm");
    }

    @Override
    public Object myRequestFocus() {
        int selectedIndex = this.getSelectedIndex();
        return focusController.myRequestFocusOnChild(selectedIndex);
    }

    @Override
    public void afterFullUiInitialized() {
        focusController.afterFullUiInitialized();
    }
}
