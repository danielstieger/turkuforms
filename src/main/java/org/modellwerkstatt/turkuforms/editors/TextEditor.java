package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import org.modellwerkstatt.dataux.runtime.delegates.LocalDateDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_DateOrTimeEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.app.TurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Peculiar;

public class TextEditor extends EditorBasisFocusable<TextField> implements IToolkit_DateOrTimeEditor {
    protected boolean adjustLocalDateDotsNotConsideringFormat;

    public TextEditor(boolean forLocalDate) {
        super(new TextField());
        inputField.setSizeFull();
        inputField.setAutoselect(true);
        inputField.setValueChangeMode(ValueChangeMode.LAZY);
        inputField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        Peculiar.focusMoveEnterHk(true, inputField, event -> { turkuDelegatesForm.focusOnNextDlgt(delegate, false);});
        Peculiar.focusMoveEnterHk(false, inputField, event -> {
            if (issueUpdateEnabled && execUpdateConclusion(inputField.getValue())) {
                // okay, we did an update conclusion
            } else {
                turkuDelegatesForm.focusOnNextDlgt(delegate, true);
            }
        });

        if (!TurkuAppFactory.onTheFly_allowEuroSignInDelegates) {
            Element component = inputField.getElement();
            component.executeJs(
                    "turku.replaceEuroSign($0)",
                    component);
        }

        adjustLocalDateDotsNotConsideringFormat = forLocalDate;
    }


    public void setText(String s) {
        // Turku.l("TextEditor.getText() " + s);

        if (!SaveObjectComperator.equals(cachedValue, s)) {
            cachedValue = s;
            if (s == null) {
                inputField.clear();
            } else {
                inputField.setValue(s);
            }
        }
    }

    public String getText() {
        cachedValue = inputField.getValue();
        if (!TurkuAppFactory.onTheFly_allowEuroSignInDelegates) {
            //fallback
            cachedValue = cachedValue.replace("€", "EUR");
        }


        if (adjustLocalDateDotsNotConsideringFormat) {
            // do not adjust cachedvalue to force an update of the ui on reload etc.
            return LocalDateDelegate.adjusDateDotInputText(cachedValue);
        } else {
            return cachedValue;
        }
    }

    /* turkuFocus() autoselect(true) handles selection
     *
     */

    @Override
    public void turkuFocus() {
        super.turkuFocus();

        Element component = inputField.getElement();
        component.executeJs(
                "turku.selectAllOnChildInput($0)",
                component);
    }

    @Override
    public void setFormatter(String format, String locale, int langIdx) {
        // just in case the text editor is used as a local date editor.
        // ignored since format, locale or langIdx are all managed by the delegate
    }
}
