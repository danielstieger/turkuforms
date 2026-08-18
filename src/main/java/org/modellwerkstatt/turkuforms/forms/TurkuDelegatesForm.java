package org.modellwerkstatt.turkuforms.forms;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.modellwerkstatt.dataux.runtime.extensions.IDlgt;
import org.modellwerkstatt.dataux.runtime.telemetrics.Dux;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_DelegateForm;
import org.modellwerkstatt.objectflow.runtime.IOFXProblem;
import org.modellwerkstatt.objectflow.runtime.IOFXSelection;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.editors.DummyEditor;
import org.modellwerkstatt.turkuforms.editors.FormChild;
import org.modellwerkstatt.turkuforms.editors.ImageViewer;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.util.ArrayList;
import java.util.List;

public class TurkuDelegatesForm<DTO> extends VerticalLayout implements IToolkit_DelegateForm<DTO> {
    private ITurkuAppFactory factory;
    private FormHeading heading;
    private FormLayout formLayout;
    private List<Integer> colWeights;
    private List<IDlgt<?>> delegates;
    private boolean minLabels;


    public TurkuDelegatesForm(ITurkuAppFactory fact) {
        factory = fact;
        Peculiar.shrinkSpace(this);
        addClassName("TurkuDelegatesForm");

        formLayout = new FormLayout();
        this.add(formLayout);

        delegates = new ArrayList<>();

        minLabels = fact.isUseMinimalDelegateFormLabelWidth();
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
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("760px", totalWidth));

        if (!minLabels) {
            setFormLabelWith("10.5em");
        }

        Dux.hl("TurkuDelegatesForm.setColLayoutConstraints() with weights: " + colWeights + " total " + totalWidth);
    }

    @Override
    public void addDelegate(IDlgt iDataUxDelegate) {

        FormChild<?> child = (FormChild<?>) iDataUxDelegate.getDelegateUiImpl();
        Component rightPart = (Component) child.getRightPartComponent();
        Component label = (Component) child.getLabel();

        FormLayout.FormItem newItem = formLayout.addFormItem(rightPart, label);
        formLayout.setColspan(newItem, colWeights.get(delegates.size() % colWeights.size()));

        if (child.isWideOption()) {
            newItem.addClassName("FormItemWideOption");
        }

        if (child instanceof DummyEditor) {
            newItem.addClassName("InvisibleWhenBelow");

        } else if (child instanceof ImageViewer) {
            newItem.addClassName("TurkuImageFormItem");

        }


        // Dux.hl("TurkuDelegatesForm.addDelegate() added "+ iDataUxDelegate.getPropertyName() + " as "  + numDelegate + " with span  " + colWeights.get(numDelegate % colWeights.size()));

        delegates.add(iDataUxDelegate);
        child.attachedToForm(this);
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
        formLayout.getElement().setAttribute("children", "" + delegates.size());

        if (minLabels) {

            int max = delegates.stream()
                    .mapToInt(iDlgt -> {
                        Label l = (Label) iDlgt.getDelegateUiImpl().getLabel();
                        int len = l.getText().length();
                        return len;
                    }).max().orElse(20);

            String labelWidth = (max + 2) + "ch";
            setFormLabelWith(labelWidth);

        }
    }

    @Override
    public Object myRequestFocus() {

        FormChild<?> turkuEditor = null;
        boolean focussed = false;

        for (IDlgt<?> dlgt: delegates) {
            if (dlgt.isRequestFocus()) {
                focussed = true;
                turkuEditor = (FormChild<?>) dlgt.getDelegateUiImpl();
                turkuEditor.turkuFocus();
                break;
            }
        }

        if (!focussed) {
            for (IDlgt<?> dlgt: delegates) {
                if (dlgt.isEnabled()) {
                    focussed = true;
                    turkuEditor = (FormChild<?>) dlgt.getDelegateUiImpl();
                    turkuEditor.turkuFocus();
                    break;
                }
            }
        }

        Dux.hl("Requesting focus for " + turkuEditor);

        if (!focussed) { return null; }
        else { return turkuEditor.getEditor(); }
    }

    @Override
    public String checkDelegatesValidAndFocus() {
        int firstFocus = -1;
        String errText;
        String firstErr = null;

        for (int i = 0; i < delegates.size(); i++) {
            errText = delegates.get(i).isInputValid();
            if (errText != null && firstFocus < 0) {
                firstFocus = i;
                firstErr = errText;
            }
        }

        if (firstFocus >= 0) {
            FormChild<?> fc = (FormChild<?>) this.delegates.get(firstFocus).getDelegateUiImpl();
            fc.turkuFocus();
            // Dux.hl("TurkuDelegatesForm.checkDelegatesValidAndFocus() focussed " + fc);

        }

        // Dux.hl("TurkuDelegatesForm.checkDelegatesValidAndFocus() " + firstErr);
        return firstErr;
    }

    @Override
    public void gcClear() {
        delegates.clear();
        factory = null;
    }

    @Override
    public void setTitleText(String s) {
        if (heading == null) { installHeading(); }
        heading.setHeading(s);
    }

    @Override
    public void setProblems(List<IOFXProblem> list) {
        if (heading == null) { installHeading(); }
        Dux.hl("Updating form problems: " + list);
        heading.flag(list);
    }

    @Override
    public void rootForm() {
        if (heading == null) { installHeading(); }
        heading.inRootPos();
    }

    private void installHeading() {
        heading = new FormHeading();
        addComponentAtIndex(0, heading);
    }

    public void focusOnNextDlgt(IDlgt<?> current, boolean next) {
        int index = delegates.indexOf(current);

        if (next) { index ++; }
        else { index --; }

        if (next && index >= delegates.size()) {
            // we are done - keep focus on last one

        } else if (!next && index < 0) {
            // also done - keep focus on first

        } else if (delegates.get(index).isEnabled()) {
            FormChild<?> formChild = (FormChild<?>) delegates.get(index).getDelegateUiImpl();
            formChild.turkuFocus();

        } else {
            focusOnNextDlgt(delegates.get(index), next);

        }
    }

    private void setFormLabelWith(String width) {
        formLayout.getElement().getStyle()
                .set("--vaadin-form-item-label-width", width);
        formLayout.getElement().getStyle()
                .set("--vaadin-form-layout-label-width", width);
    }

}
