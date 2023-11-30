package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.html.Image;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_ImageEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;

public class ImageViewer extends FormChild<Image> implements IToolkit_ImageEditor {
    protected String cachedValue;

    public ImageViewer() {
        super(new Image());
        inputField.setWidth("100%");

    }

    public void setText(String s) {
        if (!SaveObjectComperator.equals(s, cachedValue)) {
            cachedValue = s;
            inputField.setSrc(s);
            inputField.setText("-");
        }
    }

    public String getText() {
        return inputField.getSrc();
    }


    @Override
    public void enableKeyReleaseEvents() { }

    @Override
    public void setValidationErrorText(String s) { }

    @Override
    public void setEnabled(boolean b) { }

    @Override
    public void setEditorPrompt(String promptText) {
        String[] xySplit = promptText.split(",");
        String xVal = xySplit[0].trim();
        String yVal = xySplit[1].trim();

        inputField.setMaxWidth(xVal + "px");
        inputField.setMaxHeight(yVal + "px");
    }

    @Override
    public void newObjectBound() { }

    @Override
    public void setIssuesUpdateConclusion() { }

    @Override
    public void setOption(Option... options) {}
}
