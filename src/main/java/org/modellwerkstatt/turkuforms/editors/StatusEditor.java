package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_StatusEditor;

import java.util.List;

public class StatusEditor extends EditorBasisFocusable<Select<String>> implements IToolkit_StatusEditor {
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

            // will also issue a focus after setSelectedIndex on init...
            // :(
            turkuDelegatesForm.focusOnNextDlgt(delegate, true);
        });
    }


    public void setText(String s) {
        throw new RuntimeException("Not implemented for StatusEditor.");
    }

    public String getText() {
        throw new RuntimeException("Not implemented for StatusEditor.");
    }

    @Override
    public int getSelectedIndex() {
        cachedValue = inputField.getValue();
        return items.indexOf(cachedValue);
    }

    @Override
    public void setSelectedIndex(int i) {
        cachedValue = items.get(i);
        lastIssuedUpdateText = cachedValue;
        inputField.setValue(cachedValue);
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
