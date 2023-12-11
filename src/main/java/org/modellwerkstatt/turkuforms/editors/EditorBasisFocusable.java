package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

abstract public class EditorBasisFocusable<T extends Component & HasValidation & HasEnabled & Focusable<?>> extends EditorBasis<T> {


    public EditorBasisFocusable(T theField) {
        super(theField);
    }

    @Override
    public void turkuFocus() { inputField.focus(); }
}
