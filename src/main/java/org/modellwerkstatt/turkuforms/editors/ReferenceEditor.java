package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.dom.Element;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_ReferenceEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Peculiar;

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
        inputField.setRequiredIndicatorVisible(false);
        inputField.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

        Peculiar.crtlSpaceCrtlAHk(inputField, event -> {
            inputField.setOpened(! inputField.isOpened());
            if (inputField.isOpened()) { selectAll(); }
        });

        Peculiar.focusMoveEnterHk(false, inputField, event -> { turkuDelegatesForm.focusOnNextDlgt(delegate, true);});
        Peculiar.focusMoveEnterHk(true, inputField, event -> { turkuDelegatesForm.focusOnNextDlgt(delegate, false);});
    }


    @Override
    public void setIssuesUpdateConclusion() {
        super.setIssuesUpdateConclusion();
        inputField.addValueChangeListener(event -> { execUpdateConclusion(event.getValue());});
    }

    public void setText(String s) {
        boolean valueNull = (s == null);

        // Turku.l("ReferenceEditor.setText() " + this + ": " + cachedValue + " given("+s+")");
        if (!SaveObjectComperator.equals(cachedValue, s)) {

            // scope not set at all, okay in case of read only
            if (!valueNull && items == null && !cachedEnabledState) {
                List<String> scope = new ArrayList<>();
                scope.add(s);
                this.setItems(scope);

            } else if (!valueNull && items == null) {
                throw new RuntimeException("Editor for " + delegate.getPropertyName() + " has no scope set and is not in read-only.");

            } else if (!valueNull && !items.contains(s)) {
                throw new RuntimeException("Editor for " + delegate.getPropertyName() + " scope does not contain " + s + ". Scope set is " + String.join("; ", items));
            }

            if (valueNull) {
                cachedValue = null;
                lastIssuedUpdateText = null;
                inputField.clear();

            } else {
                cachedValue = s;
                lastIssuedUpdateText = s;
                inputField.setValue(s);
            }
        }

    }

    public String getText() {
        // vaadin getValue() returns null for "nothing selected"
        cachedValue = inputField.getValue();
        // Turku.l("ReferenceEditor.getText() cachedValue " + cachedValue);
        return cachedValue;
    }

    @Override
    public void setItems(List<String> given) {
        // Turku.l("ReferenceEditor.setItems() "+ this + " => " + given);
        items = given;
        inputField.setItems(items);
        // calling vaadin.setItems() will set value to null
        lastIssuedUpdateText = null;
        cachedValue = null;
    }

    @Override
    public void setOptionalAfterLoad(boolean val) {
        inputField.setRequired(! val);
        inputField.setRequiredIndicatorVisible(false);
    }

    @Override
    public void turkuFocus() {
        super.turkuFocus();
        selectAll();
    }

    public void selectAll() {
        Element component = inputField.getElement();
        component.executeJs(
                "turku.selectAllOnChildInput($0)",
                component);
    }

}
