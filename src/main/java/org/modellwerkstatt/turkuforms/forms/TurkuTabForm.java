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
import org.modellwerkstatt.turkuforms.util.Turku;

import java.util.List;

public class TurkuTabForm<DTO> extends TabSheet implements IToolkit_TabForm<DTO> {
    private FocusController<IToolkit_Form> focusController;
    private boolean uiInitialized = false;
    private int hLevel;

    public TurkuTabForm() {
        this.setSizeFull();
        focusController = new FocusController<>();
        addThemeVariants(TabSheetVariant.LUMO_TABS_MINIMAL);

        this.addSelectedChangeListener(event ->{
            Tab current = event.getSelectedTab();
            int index = this.getIndexOf(current);
            focusController.myRequestFocusOnChild(index);
        });
    }

    @Override
    public void setHLevel(int numComponent, int level) {
        Turku.l("TurkuTabForm.setHLevel( " + numComponent + ", " + level);

        hLevel = level;
        for (IToolkit_Form form: focusController.getChildren()) {
            form.setHLevel(++numComponent, level + 1);
        }
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
        uiInitialized = true;
    }
}
