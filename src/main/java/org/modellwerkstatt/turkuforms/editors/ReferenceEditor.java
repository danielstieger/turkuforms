package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.combobox.ComboBoxVariant;
import com.vaadin.flow.dom.Element;
import org.modellwerkstatt.dataux.runtime.delegates.ReferenceDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_ReferenceEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.vaadin.addons.autoselectcombobox.AutoSelectComboBox;

import java.util.ArrayList;
import java.util.List;

public class ReferenceEditor extends EditorBasisFocusable<AutoSelectComboBox<String>> implements IToolkit_ReferenceEditor {
    protected List<String> items = null;
    protected boolean extendedWidthSet = false;

    public ReferenceEditor() {
        super(new AutoSelectComboBox<>());

        inputField.setSizeFull();
        inputField.setAutoOpen(true);
        inputField.setAllowCustomValue(false);
        // inputField.getStyle().set("--vaadin-combo-box-overlay-width", "450px");
        inputField.setRequired(true);
        inputField.setRequiredIndicatorVisible(false);
        inputField.addThemeVariants(ComboBoxVariant.LUMO_SMALL);

        Peculiar.crtlSpaceCrtlAHk(inputField, event -> {
            inputField.setOpened(! inputField.isOpened());
            if (inputField.isOpened()) { selectAll(); }
        });

        Peculiar.focusMoveEnterHk(false, inputField, event -> {
            turkuDelegatesForm.focusOnNextDlgt(delegate, true);});
        Peculiar.focusMoveEnterHk(true, inputField, event -> {
            turkuDelegatesForm.focusOnNextDlgt(delegate, false);});
    }


    @Override
    public void setIssuesUpdateConclusion() {
        super.setIssuesUpdateConclusion();
        inputField.addValueChangeListener(event -> { execUpdateConclusion(event.getValue());});

        if (provideHintOption) {
            inputField.setAllowCustomValue(true);
            inputField.addCustomValueSetListener(event -> {
                if (delegate != null) {
                    String text = event.getDetail();
                    ((ReferenceDelegate) delegate).setHintForScope(text);
                    execUpdateConclusion(text);
                    setText(text);
                    inputField.setOpened(true);
                }

            });
        }
    }

    public void setText(String s) {
        boolean valueNull = (s == null);

        Turku.l("ReferenceEditor.setText() " + this + ": " + cachedValue + " given(" + s + ") with enabled " + cachedEnabledState);
        if (!SaveObjectComperator.equals(cachedValue, s)) {

            if (!valueNull && items == null) {
                // scope not set at all, problems are monitored via ReferenceDelegate

                List<String> scope = new ArrayList<>();
                scope.add(s);
                this.setItems(scope);
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
