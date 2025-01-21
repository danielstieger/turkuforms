package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datepicker.DatePickerVariant;
import com.vaadin.flow.component.notification.Notification;
import net.bytebuddy.asm.Advice;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_DateOrTimeEditor;
import org.modellwerkstatt.objectflow.runtime.MoWareFormattersFactory;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.springframework.scripting.bsh.BshScriptUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;


public class DatePickerEditor extends EditorBasisFocusable<DatePicker> implements IToolkit_DateOrTimeEditor {
    static protected HashMap<String, DateTimeFormatter> cachedFormatter = new HashMap<>();
    protected String cachedFormatKey;
    protected boolean isInvalid;

    public DatePickerEditor(DatePicker.DatePickerI18n extI18n) {
        super(new DatePicker());
        inputField.addThemeVariants(DatePickerVariant.LUMO_SMALL);
        inputField.setSizeFull();
        inputField.setAutoOpen(true);
        inputField.setRequired(true);
        inputField.setRequiredIndicatorVisible(false);
        inputField.setMax(LocalDate.of(2049, 1, 1));
        inputField.setMin(LocalDate.of(1951, 1, 1));

        inputField.setI18n(extI18n);

        // esc closes cmdtab
        ShortcutRegistration reg = Shortcuts.addShortcutListener(inputField, shortcutEvent -> {
            if (inputField.isOpened()) {
                inputField.setOpened(false);
            } else {
                Workarounds.tryToSendToUnderlyingCmdUi(inputField, shortcutEvent);
            }

        }, Key.ESCAPE);
        reg.listenOn(inputField);
        reg.setEventPropagationAllowed(false);
        reg.setBrowserDefaultAllowed(false);


        Peculiar.focusMoveEnterHk(false, inputField, event -> {
            turkuDelegatesForm.focusOnNextDlgt(delegate, true);});
        Peculiar.focusMoveEnterHk(true, inputField, event -> {
            turkuDelegatesForm.focusOnNextDlgt(delegate, false);});

        // other invalid attribute listeners are not working.
        // https://github.com/vaadin/platform/issues/3066
        inputField.addValidationStatusChangeListener(event -> {
            // also on no value, this event triggers with a true
            isInvalid = !event.getNewStatus();
        });


        inputField.addValueChangeListener(event -> {
            if (issueUpdateEnabled) {
                LocalDate ld = event.getValue();
                // also forward null, this will result in red error messages.
                if (execUpdateConclusion(ld == null ? null : ldToString(event.getValue()))) {
                    // ..
                } else {
                    turkuDelegatesForm.focusOnNextDlgt(delegate, true);
                }
            }
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

                LocalDate notAdjusted = LocalDate.parse(cachedValue, cachedFormatter.get(cachedFormatKey));
                LocalDate adjusted = notAdjusted.withYear(MoWareFormattersFactory.twoToFourDigitYear(notAdjusted.getYear()));
                inputField.setValue(adjusted);

            }
        }
    }

    private String ldToString(LocalDate ld) {
        LocalDate adjusted = ld.withYear(MoWareFormattersFactory.twoToFourDigitYear(ld.getYear()));
        return cachedFormatter.get(cachedFormatKey).format(adjusted);
    }

    public String getText() {
        LocalDate ld = inputField.getValue();

        // Turku.l("" + this  + " getvalue(): " + ld + " / " +  isInvalid);

        if (ld != null) {
            cachedValue = ldToString(ld) ;

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
        String key = format + "_" + locale + "_" + langIdx;

        if (!SaveObjectComperator.equals(cachedFormatKey, key)) {
            cachedFormatKey = format;

            Locale loc = Locale.forLanguageTag(locale);
            inputField.setLocale(loc);

            DatePicker.DatePickerI18n i18n = inputField.getI18n();
            i18n.setReferenceDate(LocalDate.of(MoWareFormattersFactory.PIVOT_YEAR, 1, 1));
            i18n.setDateFormat(format);
            inputField.setI18n(i18n);

            if (! cachedFormatter.containsKey(cachedFormatKey)) {
                cachedFormatter.put(cachedFormatKey, DateTimeFormatter.ofPattern(format, loc));
            }
        }
    }
}
