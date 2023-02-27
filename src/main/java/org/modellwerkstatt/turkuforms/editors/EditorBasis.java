package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import org.modellwerkstatt.dataux.runtime.extensions.IDataUxDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;
import org.modellwerkstatt.turkuforms.util.Workarounds;

abstract public class EditorBasis {
    protected Label label;
    @Deprecated
    protected TextField field;

    public EditorBasis() {
        label = new Label();
        field = new TextField();
        field.setSizeFull();
        field.setEnabled(false);
    }

    public void setDelegate(IDataUxDelegate iDataUxDelegate) {

    }

    public void enableKeyReleaseEvents() {

    }

    public void setLabelTooltip(String s) {
        Tooltip tt = Tooltip.forComponent(label);
        tt.setText(Workarounds.mlToolTipText(s));
    }

    public void setValidationErrorText(String s) {

    }

    public void setLabel(String s) {
        label.setText(s);
    }

    public void setEnabled(boolean b) {

    }

    public void setEditorPrompt(String s) {

    }

    public void newObjectBound() {

    }

    public void setText(String s) {
        field.setValue(s);
    }

    public String getText() {
        return field.getValue();
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
        return field;
    }

    public void gcClear() {

    }
}
