package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_DateOrTimeEditor;

public class DateEditor extends EditorBasis<TextField> implements IToolkit_DateOrTimeEditor {


    public DateEditor(boolean withPicker) {
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
    public void setFormatter(String format, String locale, int langIdx) {

    }
}
