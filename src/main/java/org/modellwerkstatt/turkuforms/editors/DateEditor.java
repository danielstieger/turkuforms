package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_DateOrTimeEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateEditor extends EditorBasis<DatePicker> implements IToolkit_DateOrTimeEditor {


    public DateEditor(boolean withPicker) {
        super(new DatePicker());
        inputField.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        inputField.setSizeFull();

        Peculiar.focusMoveEnterHk(false, inputField, event -> { turkuDelegatesForm.focusOnNextDlgt(delegate, true);});
        Peculiar.focusMoveEnterHk(true, inputField, event -> { turkuDelegatesForm.focusOnNextDlgt(delegate, false);});

    }

    public void setText(String s) {
        boolean valueNull = (s == null);

        if (!SaveObjectComperator.equals(cachedValue, s)) {
            if (valueNull) {
                cachedValue = null;
                lastIssuedUpdateText = null;
                inputField.clear();

            } else {
                cachedValue = s;
                lastIssuedUpdateText = s;
                inputField.setValue(LocalDate.now());
            }
        }
    }

    public String getText() {
        LocalDate ld = inputField.getValue();
        return ld.toString();
    }


    @Override
    public void setFormatter(String format, String locale, int langIdx) {

    }
}
