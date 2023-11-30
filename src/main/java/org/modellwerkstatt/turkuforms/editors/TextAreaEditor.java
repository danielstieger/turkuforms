package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Element;
import org.modellwerkstatt.dataux.runtime.delegates.LocalDateDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.app.MPreisAppConfig;
import org.modellwerkstatt.turkuforms.app.TurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;

public class TextAreaEditor extends EditorBasis<TextArea> implements IToolkit_TextEditor {

    public TextAreaEditor(int numLines) {
        super(new TextArea());

        inputField.setWidthFull();
        String height = String.format("%.0frem", MPreisAppConfig.DELEGATES_LINE_HIGHT_IN_REM * numLines + 0.5).replace(",",".");
        inputField.setHeight(height);

        inputField.setAutoselect(true);
        inputField.setValueChangeMode(ValueChangeMode.LAZY);
        inputField.addThemeVariants(TextAreaVariant.LUMO_SMALL);

        Peculiar.focusMoveEnterHk(true, inputField, event -> { turkuDelegatesForm.focusOnNextDlgt(delegate, false);});
        Peculiar.focusMoveEnterHk(false, inputField, event -> {
            if (issueUpdateEnabled && execUpdateConclusion(inputField.getValue())) {
                // okay, we did an update conclusion
            } else {
                turkuDelegatesForm.focusOnNextDlgt(delegate, true);
            }
        });
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
        return cachedValue;
    }
}
