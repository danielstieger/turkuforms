package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.timepicker.TimePicker;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_DateOrTimeEditor;
import org.modellwerkstatt.objectflow.runtime.MoWareFormattersFactory;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

public class TimePickerEditor extends EditorBasisFocusable<TimePicker> implements IToolkit_DateOrTimeEditor {
    static protected HashMap<String, DateTimeFormatter> cachedFormatter = new HashMap<>();
    protected String cachedFormatKey;
    protected boolean isInvalid;

    public TimePickerEditor() {
        super(new TimePicker());
        // inputField.setSizeFull();
        inputField.setAutoOpen(true);
        // prevents dropdown combo for time
        inputField.setStep(Duration.ofMinutes(30));

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

                // must be parsable to LocaDateTime (containing year etc.) not just java.time.Parsed
                LocalDateTime notAdjusted = LocalDateTime.parse(cachedValue, cachedFormatter.get(cachedFormatKey));
                LocalDateTime adjusted = notAdjusted.withYear(MoWareFormattersFactory.twoToFourDigitYear(notAdjusted.getYear()));
                inputField.setValue(adjusted.toLocalTime());

            }
        }
    }

    public String getText() {
        LocalTime ld = inputField.getValue();

        // Turku.l("" + this  + " getvalue(): " + ld + " / " +  isInvalid);

        if (ld != null) {
            LocalDateTime notAdjusted = LocalDateTime.now();
            LocalDateTime adjusted = notAdjusted.withYear(MoWareFormattersFactory.twoToFourDigitYear(notAdjusted.getYear())).withHour(ld.getHour()).withMinute(ld.getMinute()).withSecond(ld.getSecond());
            cachedValue = cachedFormatter.get(cachedFormatKey).format(adjusted);

        } else if (isInvalid) {
            cachedValue = "_";

        } else {
            cachedValue = "";
        }

        // Turku.l("" + this  + " getText(): " + cachedValue);
        return cachedValue;
    }

    @Override
    public void setEditorPrompt(String s) {
        super.setEditorPrompt("__:__");
    }

    @Override
    public void setValidationErrorText(String text) {
        String formatToReplace = cachedFormatKey;
        text = text.replace(formatToReplace, "HH:mm");
        super.setValidationErrorText(text);
    }

    @Override
    public void setFormatter(String format, String locale, int langIdx) {
        // e.g. "dd.MM.yy HH:mm" splitted by space :)
        // can not format EEEE dd.MM etc.

        String key = format + "_" + locale + "_" + langIdx;

        if (!SaveObjectComperator.equals(cachedFormatKey, key)) {
            cachedFormatKey = format;

            Locale loc = Locale.forLanguageTag(locale);
            inputField.setLocale(loc);

            if (!cachedFormatter.containsKey(cachedFormatKey)) {
                cachedFormatter.put(cachedFormatKey, DateTimeFormatter.ofPattern(format, loc));
            }
        }
    }
}
