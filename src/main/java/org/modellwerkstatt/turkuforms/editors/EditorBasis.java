package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import org.modellwerkstatt.dataux.runtime.extensions.IDataUxDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;
import org.modellwerkstatt.objectflow.runtime.OFXConclusionInformation;
import org.modellwerkstatt.objectflow.runtime.OFXConsoleHelper;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

abstract public class EditorBasis<T extends Component & HasValidation & HasEnabled & Focusable<?>> implements IToolkit_TextEditor {
    protected Label label;
    protected T inputField;
    protected Component rightPart;
    protected IDataUxDelegate<?> delegate;

    protected boolean issueUpdateEnabled = false;
    protected Button updateConclusionButton;
    protected String lastIssuedUpdateText = null;

    protected boolean cachedEnabledState = true;
    protected String cachedValue = null;


    public EditorBasis(T theField) {
        inputField = theField;
        rightPart = inputField;
        label = new Label();
        label.setMinWidth("200px");
        // label.setFor(inputField);
    }

    public void setDelegate(IDataUxDelegate iDataUxDelegate) { delegate = iDataUxDelegate; }

    public void enableKeyReleaseEvents() {
        // for textfield only, in case hooks are used (calc tax of value etc.)
    }

    public void setLabelTooltip(String s) {
        Tooltip tt = Tooltip.forComponent(label);
        tt.setText(Workarounds.mlToolTipText(s));
    }

    public void setValidationErrorText(String text) {
        if (Defs.hasValidationErrorText(text)) {
            inputField.setErrorMessage(text);
            inputField.setInvalid(true);
        } else{
            inputField.setErrorMessage("");
            inputField.setInvalid(false);
        }
    }

    public void setLabel(String s) {
        label.setText(s);
    }

    public void setEnabled(boolean b) {
        // since setEnabled is called oftentimes, unclear if that does impair vaadin performance
        if (cachedEnabledState != b) {
            cachedEnabledState = b;
            if (updateConclusionButton!= null) { updateConclusionButton.setEnabled(b); }
            inputField.setEnabled(b);
        }
    }

    public void setEditorPrompt(String s) { inputField.getElement().setProperty("placeholder", s);}

    public void newObjectBound() {
        lastIssuedUpdateText = null;
    }

    public void execUpdateConclusion(String newValue) {
        // default implementation
        Turku.l("EditorBasis.execUpdateConclusion() [" + issueUpdateEnabled + "] " + lastIssuedUpdateText + " -> " + newValue + " for " + this);
        /* if (newValue == null) {
            Turku.l(OFXConsoleHelper._____organizeCurrentStacktrace_____());
        } */

        if (cachedEnabledState && issueUpdateEnabled) {
            if (!SaveObjectComperator.equals(lastIssuedUpdateText, newValue)) {
                Turku.l("EditorBasis.execUpdateConclusion() STARTING update conclusion.");

                lastIssuedUpdateText = newValue;
                issueUpdateEnabled = false;
                delegate.issueUpdateConclusionAfterContentChange();
                issueUpdateEnabled = true;
                Turku.l("EditorBasis.execUpdateConclusion() update conclusion PROCESSED 2.");
            }
        }

    }

    public void updateConclusionButtonClicked() {
        // it might be necessary to overwrite the button click
        execUpdateConclusion(this.getText());
    }

    public void setIssuesUpdateConclusion() {

        HorizontalLayout hl = new HorizontalLayout();
        Peculiar.shrinkSpace(hl);

        updateConclusionButton = new Button(
                Workarounds.createIconWithCollection("refresh"),
                event -> { updateConclusionButtonClicked(); });
        updateConclusionButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        updateConclusionButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        hl.add(inputField, updateConclusionButton);
        hl.setFlexGrow(1.0, inputField);
        hl.setFlexGrow(0.0, updateConclusionButton);

        rightPart = hl;
        issueUpdateEnabled = true;
    }

    public void setOption(IToolkit_TextEditor.Option... options) {
    }

    public void turkuFocus() { inputField.focus(); }

    public Object getEditor() { return inputField; }

    public Object getLabel() { return label; }

    public Object getRightPartComponent() { return rightPart; }

    public void gcClear() {
        delegate = null;

    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " for " + (delegate == null ? "null" : delegate.getPropertyName());
    }
}
