package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.textfield.TextField;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_ImageEditor;

public class ImageViewer extends EditorBasis<TextField> implements IToolkit_ImageEditor {


    public ImageViewer() {
        super(new TextField());
        inputField.setSizeFull();
        inputField.setEnabled(false);
    }

    public void setText(String s) {
        if (s == null) { s = "(null)"; }
        inputField.setValue(s);
    }

    public String getText() {
        return inputField.getValue();
    }

}
