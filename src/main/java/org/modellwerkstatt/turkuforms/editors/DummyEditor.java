package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import org.modellwerkstatt.dataux.runtime.extensions.IDataUxDelegate;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_TextEditor;

public class DummyEditor extends EditorBasis<TextField> implements IToolkit_TextEditor {

    public DummyEditor() {
        super(new TextField());
        inputField.setSizeFull();
        inputField.setEnabled(false);
        inputField.setVisible(false);
    }

    public void setText(String s) {
        throw new RuntimeException("Not implemented for this editor.") ;
    }

    public String getText() {
        throw new RuntimeException("Not implemented for this editor.") ;
    }

}
