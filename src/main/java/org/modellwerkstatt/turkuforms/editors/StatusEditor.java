package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.textfield.TextField;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_StatusEditor;

import java.util.List;

public class StatusEditor extends EditorBasis<TextField> implements IToolkit_StatusEditor {


    public StatusEditor() {
        super(new TextField());
        inputField.setSizeFull();
        inputField.setEnabled(false);
    }

    public void setText(String s) {
        if (s == null) { s = "(null)"; }
        inputField.setValue(s);
    }

    public String getText() {
        return inputField.getValue();
    }

    @Override
    public int getSelectedIndex() {
        return 0;
    }

    @Override
    public void setSelectedIndex(int i) {

    }

    @Override
    public void setItems(List<String> list) {

    }
}
