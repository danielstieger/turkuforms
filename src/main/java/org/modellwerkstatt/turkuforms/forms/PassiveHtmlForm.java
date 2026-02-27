package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.extensions.IDlgt;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Form;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.editors.FormChild;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.util.List;

public class PassiveHtmlForm<DTO> extends VerticalLayout implements IToolkit_Form<DTO> {
    private ITurkuAppFactory factory;
    private FormHeading heading;
    private Div contentDiv;

    public PassiveHtmlForm(ITurkuAppFactory fact) {
        factory = fact;
        Peculiar.shrinkSpace(this);
        addClassName("PassiveHtmlForm");
        setSizeFull();
        contentDiv = new Div();
        contentDiv.setSizeFull();
        add(contentDiv);
    }

    @Override
    public boolean selectionChanged(IOFXSelection<DTO> iofxSelection) {
        return false;
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection<DTO> iofxSelection) {

    }

    @Override
    public Object myRequestFocus() {
        return null;
    }

    @Override
    public void afterFullUiInitialized() {

    }

    @Override
    public void gcClear() {
        factory = null;
    }

    @Override
    public void setTitleText(String s) {
        contentDiv.getElement().setProperty("innerHTML", s);
    }

    @Override
    public void setProblems(List<IOFXProblem> list) {
        throw new RuntimeException("Not implemented for PassiveHtmlForm");
    }
}
