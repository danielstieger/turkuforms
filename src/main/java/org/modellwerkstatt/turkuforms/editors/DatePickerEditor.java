package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;

import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_DateOrTimeEditor;
import org.modellwerkstatt.objectflow.runtime.MoWareFormattersFactory;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;


public class DatePickerEditor extends EditorBasis<DatePicker> implements IToolkit_DateOrTimeEditor {
    static protected HashMap<String, DateTimeFormatter> cachedFormatter = new HashMap<>();
    protected String cachedFormatKey;

    public DatePickerEditor() {
        super(new DatePicker());
        inputField.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        inputField.setSizeFull();
        inputField.setAutoOpen(false);
        inputField.setRequired(true);

        DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
        i18n.setReferenceDate(LocalDate.of(MoWareFormattersFactory.PIVOT_YEAR, 1, 1));
        inputField.setI18n(i18n);

        Peculiar.focusMoveEnterHk(false, inputField, event -> { turkuDelegatesForm.focusOnNextDlgt(delegate, true);});
        Peculiar.focusMoveEnterHk(true, inputField, event -> { turkuDelegatesForm.focusOnNextDlgt(delegate, false);});

    }

    public void setText(String s) {
        boolean valueNull = (s == null || s.equals(""));


        Turku.l("" + this  + " setText(): '" + s+ "' " + valueNull);
        if (!SaveObjectComperator.equals(cachedValue, s)) {
            if (valueNull) {
                cachedValue = null;
                lastIssuedUpdateText = null;
                inputField.clear();

            } else {
                cachedValue = s;
                lastIssuedUpdateText = s;

                inputField.setValue(LocalDate.parse(cachedValue, cachedFormatter.get(cachedFormatKey)));
            }
        }
    }

    public String getText() {
        LocalDate ld = inputField.getValue();

        Turku.l("" + this  + " getvalue(): " + ld + " / " + inputField.isInvalid() + " / " + inputField.getElement().hasProperty("invalid"));
        if (inputField.isInvalid()) {
            cachedValue = "_";

        } else if (ld == null) {
            cachedValue = "";

        } else {
            cachedValue = cachedFormatter.get(cachedFormatKey).format(ld);

        }

        Turku.l("" + this  + " getText(): " + cachedValue);
        return cachedValue;
    }


    @Override
    public void setFormatter(String format, String locale, int langIdx) {
        String key = format + "_" + locale + "_" + langIdx;

        if (!SaveObjectComperator.equals(cachedFormatKey, key)) {
            cachedFormatKey = format;

            Locale loc = Locale.forLanguageTag(locale);
            inputField.setLocale(loc);

            if (! cachedFormatter.containsKey(cachedFormatKey)) {
                cachedFormatter.put(cachedFormatKey, DateTimeFormatter.ofPattern(format, loc));
            }
        }
    }
}
