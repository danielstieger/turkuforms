package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import com.vaadin.flow.component.textfield.TextField;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_StatusEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import java.util.List;

public class StatusEditor extends EditorBasis<Select<String>> implements IToolkit_StatusEditor {
    protected List<String> items = null;

    public StatusEditor() {
        super(new Select<String>());
        inputField.setSizeFull();
        inputField.setEmptySelectionAllowed(false);
        inputField.addThemeVariants(SelectVariant.LUMO_SMALL);

        inputField.addValueChangeListener(event -> {
            if (issueUpdateEnabled) {
                boolean updtExecuted = execUpdateConclusion(event.getValue());
            }

            turkuDelegatesForm.focusOnNextDlgt(delegate, true);

        });
    }


    public void setText(String s) {
        if (!SaveObjectComperator.equals(cachedValue, s)) {
            cachedValue = s;
            lastIssuedUpdateText = null;
            inputField.setValue(s);
        }
    }

    public String getText() {
        cachedValue = inputField.getValue();
        return cachedValue;
    }

    @Override
    public int getSelectedIndex() {
        return items.indexOf(getText());
    }

    @Override
    public void setSelectedIndex(int i) {
        inputField.setValue(items.get(i));
    }

    @Override
    public void setItems(List<String> given) {
        items = given;
        inputField.setItems(items);
        // setting items will clear input ..
        lastIssuedUpdateText = null;
        cachedValue = null;
    }
}
