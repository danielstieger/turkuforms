package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.textfield.TextField;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;

public class TextEditor extends EditorBasis<TextField> implements IToolkit_TextEditor {

    public TextEditor() {
        super(new TextField());
        inputField.setSizeFull();
        inputField.setEnabled(false);
        inputField.setAutoselect(true);
    }

    public void setText(String s) {
        if (s == null) {
            inputField.clear();
        } else {
            inputField.setValue(s);
        }

    }

    public String getText() {
        return inputField.getValue();
    }

}
