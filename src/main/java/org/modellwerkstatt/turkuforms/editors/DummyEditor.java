package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.html.Label;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;

public class DummyEditor extends FormChild<Label> implements IToolkit_TextEditor {

    public DummyEditor() {
        super(new Label());
        inputField.setSizeFull();
    }

    @Override
    public void setValidationErrorText(String s) {

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
    public void setIssuesUpdateConclusion() {
        throw new RuntimeException("Not implemented for dummy editor.") ;
    }

    @Override
    public void setOption(Option... options) {

    }

    public void setText(String s) {
        throw new RuntimeException("Not implemented for dummy editor.");
    }

    public String getText() {
        throw new RuntimeException("Not implemented for dummy editor.");
    }

}
