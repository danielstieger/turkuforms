package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_ReferenceEditor;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.util.ArrayList;
import java.util.List;

public class ReferenceEditor extends EditorBasis<ComboBox<String>> implements IToolkit_ReferenceEditor {
    protected List<String> items = null;

    public ReferenceEditor() {
        super(new ComboBox<>());
        inputField.setSizeFull();
        inputField.setAutoOpen(true);
        inputField.setAllowCustomValue(false);
        inputField.getStyle().set("--vaadin-combo-box-overlay-width", "350px");
        inputField.setRequired(true);

        Peculiar.crtlSpaceHk(inputField, event -> {
            inputField.setOpened(! inputField.isOpened());
            if (inputField.isOpened()) { selectAll(); }
        });
    }

    public void setText(String s) {
        boolean valueNull = (s == null);

        // scope not set at all, okay in case of read only
        if (!valueNull && items == null && !inputField.isEnabled()) {
            List<String> scope = new ArrayList<>();
            scope.add(s);
            this.setItems(scope);

        } else if (!valueNull && items == null) {
            throw new RuntimeException("Editor for " + delegate.getPropertyName() + " has no scope set and is not in read-only.");

        } else if (!valueNull && !items.contains(s)) {
            throw new RuntimeException("Editor for " + delegate.getPropertyName() + " scope does not contain " + s + ". Scope set is " + String.join("; ", items));
        }

        if (valueNull) {
            inputField.clear();
        } else {
            inputField.setValue(s);
        }
    }

    public String getText() {
        // vaadin getValue() returns null for "nothing selected"
        return inputField.getValue();
    }

    @Override
    public void setOptionalAfterLoad(boolean val) {
        if (val) {
            inputField.setRequired(false);
        }
    }

    @Override
    public void setItems(List<String> given) {
        items = given;
        inputField.setItems(items);
    }


    public void selectAll() {
        Element component = inputField.getElement();
        component.executeJs(
                "turku.selectAllOnChildInput($0)",
                component);
    }

}
