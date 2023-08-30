package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePickerVariant;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_DateOrTimeEditor;
import org.modellwerkstatt.objectflow.runtime.MoWareFormattersFactory;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

public class DateTimeEditor extends EditorBasis<DateTimePicker> implements IToolkit_DateOrTimeEditor {

    static protected HashMap<String, DateTimeFormatter> cachedFormatter = new HashMap<>();
    protected String cachedFormatKey;
    protected boolean isInvalid;

    public DateTimeEditor() {
        super(new DateTimePicker());
        inputField.addThemeVariants(DateTimePickerVariant.LUMO_SMALL);
        inputField.setSizeFull();
        inputField.setAutoOpen(true);
        inputField.setMax(LocalDateTime.of(2049, 1, 1, 1, 0));
        inputField.setMin(LocalDateTime.of(1951, 1, 1, 1, 0));
        inputField.setStep(Duration.ofMinutes(1));

        Peculiar.focusMoveEnterHk(false, inputField, event -> {
            turkuDelegatesForm.focusOnNextDlgt(delegate, true);
        });
        Peculiar.focusMoveEnterHk(true, inputField, event -> {
            turkuDelegatesForm.focusOnNextDlgt(delegate, false);
        });

        // check DatePickerEditor ...
        inputField.addValidationStatusChangeListener(event -> {
            isInvalid = !event.getNewStatus();
            // Turku.l("DatePickerEditor.addValidationStatusChangeListener() isInvalid: " + isInvalid);
        });
    }

    public void setText(String s) {
        boolean valueNull = (s == null || s.equals(""));

        // Turku.l("" + this  + " setText(): '" + s + "' " + valueNull);
        if (!SaveObjectComperator.equals(cachedValue, s)) {
            if (valueNull) {
                cachedValue = null;
                lastIssuedUpdateText = null;
                inputField.clear();

            } else {
                cachedValue = s;
                lastIssuedUpdateText = s;

                LocalDateTime notAdjusted = LocalDateTime.parse(cachedValue, cachedFormatter.get(cachedFormatKey));
                LocalDateTime adjusted = notAdjusted.withYear(MoWareFormattersFactory.twoToFourDigitYear(notAdjusted.getYear()));
                inputField.setValue(adjusted);

            }
        }
    }

    public String getText() {
        LocalDateTime ld = inputField.getValue();

        // Turku.l("" + this  + " getvalue(): " + ld + " / " +  isInvalid);

        if (ld != null) {
            LocalDateTime adjusted = ld.withYear(MoWareFormattersFactory.twoToFourDigitYear(ld.getYear()));
            cachedValue = cachedFormatter.get(cachedFormatKey).format(ld);

        } else if (isInvalid) {
            cachedValue = "_";

        } else {
            cachedValue = "";
        }

        // Turku.l("" + this  + " getText(): " + cachedValue);
        return cachedValue;
    }


    @Override
    public void setFormatter(String format, String locale, int langIdx) {
        // e.g. "dd.MM.yy HH:mm" splitted by space :)

        String key = format + "_" + locale + "_" + langIdx;

        if (!SaveObjectComperator.equals(cachedFormatKey, key)) {
            cachedFormatKey = format;

            // cut of time from format ..
            String[] splitted = format.split("\\s+");
            String dateFormat = splitted[1];
            if (splitted[0].contains("d") || splitted[0].contains("y") || splitted[0].contains("M")) {
                dateFormat = splitted[0];
            }

            Locale loc = Locale.forLanguageTag(locale);
            inputField.setLocale(loc);

            DatePicker.DatePickerI18n i18n = new DatePicker.DatePickerI18n();
            i18n.setReferenceDate(LocalDate.of(MoWareFormattersFactory.PIVOT_YEAR, 1, 1));
            i18n.setDateFormat(dateFormat);
            inputField.setDatePickerI18n(i18n);

            if (!cachedFormatter.containsKey(cachedFormatKey)) {
                cachedFormatter.put(cachedFormatKey, DateTimeFormatter.ofPattern(format, loc));
            }
        }
    }
}
