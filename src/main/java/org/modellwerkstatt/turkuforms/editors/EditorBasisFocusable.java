package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Focusable;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValidation;

abstract public class EditorBasisFocusable<T extends Component & HasValidation & HasEnabled & Focusable<?>> extends EditorBasis<T> {


    public EditorBasisFocusable(T theField) {
        super(theField);
    }

    @Override
    public void turkuFocus() {
        inputField.focus(); }
}
