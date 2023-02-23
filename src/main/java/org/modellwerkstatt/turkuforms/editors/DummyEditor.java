package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.html.Label;
import org.modellwerkstatt.dataux.runtime.extensions.IDataUxDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;

public class DummyEditor implements IToolkit_TextEditor {
    private Label label;
    private Label editor;


    public DummyEditor() {
        label = new Label();
        editor = new Label();
    }

    @Override
    public void setDelegate(IDataUxDelegate iDataUxDelegate) {

    }

    @Override
    public void enableKeyReleaseEvents() {

    }

    @Override
    public void setLabelTooltip(String s) {

    }

    @Override
    public void setValidationErrorText(String s) {

    }

    @Override
    public void setLabel(String s) {

    }

    @Override
    public void setEnabled(boolean b) {

    }

    @Override
    public void setEditorPrompt(String s) {

    }

    @Override
    public void newObjectBound() {

    }

    @Override
    public void setText(String s) {

    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setIssuesUpdateConclusion() {

    }

    @Override
    public void setOption(Option... options) {

    }

    @Override
    public Object getEditor() {
        return editor;
    }

    @Override
    public Object getLabel() {
        return label;
    }

    @Override
    public Object getRightPartComponent() {
        return editor;
    }

    @Override
    public void gcClear() {

    }
}
