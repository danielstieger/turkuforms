package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.genspecifications.MenuSub;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_FormContainer;
import org.modellwerkstatt.objectflow.runtime.IOFXEntity;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;

import java.util.List;

public class TurkuGridLayout<DTO> extends VerticalLayout implements IToolkit_FormContainer<DTO> {

    @Override
    public void setLayoutConstraints(List<String> list, List<String> list1) {

    }

    @Override
    public void addChildren(IToolkit_Form iToolkit_form) {
        this.add((Component) iToolkit_form);
    }

    @Override
    public void addMenuAndSetButtons(MenuSub menuSub) {

    }

    @Override
    public boolean selectionChanged(IOFXSelection iofxSelection) {
        return false;
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection iofxSelection) {

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
