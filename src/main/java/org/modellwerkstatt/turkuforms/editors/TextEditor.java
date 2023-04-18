package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;
import org.modellwerkstatt.turkuforms.util.Turku;

public class TextEditor extends EditorBasis<TextField> implements IToolkit_TextEditor {

    public TextEditor() {
        super(new TextField());
        inputField.setSizeFull();
        inputField.setEnabled(false);
        inputField.setAutoselect(true);
        inputField.setValueChangeMode(ValueChangeMode.LAZY);
    }

    public void setText(String s) {
        Turku.l("TextEditor.getText() " + s);

        if (s == null) {
            inputField.clear();
        } else {
            inputField.setValue(s);
        }

    }

    public String getText() {
        String val = inputField.getValue();
        Turku.l("TextEditor.getText() " + val);
        return val;
    }

    @Override
    public void turkuFocus() {
        super.turkuFocus();
    }
}
