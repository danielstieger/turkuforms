package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.select.SelectVariant;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_StatusEditor;
import org.modellwerkstatt.turkuforms.util.Defs;

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

    protected void adjustEnabled() {
        if (! cachedEnabledState) {
            inputField.setItems(cachedValue);
            inputField.setValue(cachedValue);
        }
    }

    @Override
    public void setSelectedIndex(int i) {
        cachedValue = items.get(i);
        lastIssuedUpdateText = cachedValue;
        inputField.setValue(cachedValue);
        adjustEnabled();
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        adjustEnabled();
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
