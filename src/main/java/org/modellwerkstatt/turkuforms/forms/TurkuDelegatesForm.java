package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.extensions.IDataUxDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_DelegateForm;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.editors.DummyEditor;
import org.modellwerkstatt.turkuforms.util.FormHeading;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.ArrayList;
import java.util.List;

public class TurkuDelegatesForm<DTO> extends VerticalLayout implements IToolkit_DelegateForm<DTO> {
    private ITurkuFactory factory;
    private FormHeading heading;
    private FormLayout formLayout;
    private List<Integer> colWeights;
    private int numDelegate = 0;

    public TurkuDelegatesForm(ITurkuFactory fact) {
        factory = fact;
        Workarounds.shrinkSpace(this);

        formLayout = new FormLayout();
        this.add(formLayout);
    }

    @Override
    public void setColLayoutConstraints(List<String> weights) {
        colWeights = new ArrayList<>();
        int totalWidth = 0;
        for (String w: weights) {
            int val = TurkuGridLayout.getWeight(w);
            if (val <= 0) { throw new RuntimeException("This can not happen. Pos weight needed but is "+ val); }
            colWeights.add(val);
            totalWidth += val;
        }

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("1px", 1),
                new FormLayout.ResponsiveStep("790px", totalWidth));

        // Turku.l("TurkuDelegatesForm.setColLayoutConstraints() with weights: " + colWeights + " total " + totalWidth);
    }

    @Override
    public void addDelegate(IDataUxDelegate iDataUxDelegate) {

        IToolkit_TextEditor editor = iDataUxDelegate.getDelegateUiImpl();
        Component rightPart = (Component) editor.getRightPartComponent();
        Component label = (Component) editor.getLabel();

        FormLayout.FormItem newItem = formLayout.addFormItem(rightPart, label);
        formLayout.setColspan(newItem, colWeights.get(numDelegate % colWeights.size()));
        if (editor instanceof DummyEditor) { newItem.addClassName("InvisibleWhenBelow"); }

        // Turku.l("TurkuDelegatesForm.addDelegate() added "+ iDataUxDelegate.getPropertyName() + " as "  + numDelegate + " with span  " + colWeights.get(numDelegate % colWeights.size()));

        numDelegate++;
    }

    @Override
    public boolean selectionChanged(IOFXSelection<DTO> iofxSelection) {
        return false;
    }

    @Override
    public void loadList(List<DTO> list, IOFXSelection<DTO> iofxSelection) {

    }

    @Override
    public void afterFullUiInitialized() {

    }

    @Override
    public Object myRequestFocus() {
        return null;
    }

    @Override
    public String checkDelegatesValidAndFocus() {
        return null;
    }

    @Override
    public void gcClear() {

    }

    @Override
    public void setTitleText(String s) {
        if (heading == null) { installHeading(); }
        heading.setHeading(s);
    }

    @Override
    public void setProblems(List<IOFXProblem> list) {
        if (heading == null) { installHeading(); }
        heading.flag(list);
    }

    private void installHeading() {
        heading = new FormHeading();
        addComponentAtIndex(0, heading);
    }
}
