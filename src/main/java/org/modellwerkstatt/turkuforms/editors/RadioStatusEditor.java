package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_StatusEditor;

import java.util.List;

public class RadioStatusEditor extends EditorBasis<RadioButtonGroup<String>> implements IToolkit_StatusEditor {
    protected List<String> items = null;



    public RadioStatusEditor() {
        super(new RadioButtonGroup<String>());
        inputField.setSizeFull();

        inputField.addValueChangeListener(event -> {
            if (issueUpdateEnabled) {
                boolean updtExecuted = execUpdateConclusion(event.getValue());
            }

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
