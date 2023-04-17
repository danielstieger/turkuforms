package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.shared.Tooltip;
import org.modellwerkstatt.dataux.runtime.extensions.IDataUxDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.Workarounds;


/*
 * To Check
 * (1) Wo wird die Validierungs-Meldung angezeigt? Und gelöscht?
 * (2) Placeholder handling correct ?
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

abstract public class EditorBasis<T extends Component & HasValidation & HasEnabled> {
    protected Label label;
    protected T inputField;

    protected boolean issueUpdateEnabled = false;
    protected IDataUxDelegate delegate;

    public EditorBasis(T theField) {
        inputField = theField;
        label = new Label();
        // label.setFor(inputField);
    }

    public void setDelegate(IDataUxDelegate iDataUxDelegate) {
        delegate = iDataUxDelegate;
    }

    public void enableKeyReleaseEvents() {

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
        inputField.setEnabled(b);
    }

    public void setEditorPrompt(String s) {
        inputField.getElement().setProperty("placeholder", s);
    }

    public void newObjectBound() {

    }

    public void setIssuesUpdateConclusion() {

    }

    public void setOption(IToolkit_TextEditor.Option... options) {

    }

    public Object getEditor() {
        return null;
    }

    public Object getLabel() {
        return label;
    }

    public Object getRightPartComponent() {
        return inputField;
    }

    public void gcClear() {

    }
}
