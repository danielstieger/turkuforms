package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.html.Image;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_ImageEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Defs;

public class ImageViewer extends FormChild<Image> implements IToolkit_ImageEditor {
    protected String cachedValue;
    protected String retrieveLocationStore;


    public ImageViewer(String rls) {
        super(new Image());
        retrieveLocationStore = rls;
        inputField.setWidth("100%");

        // another idea would be to retrieve the available
        // space in grid, then scale img
    }

    public void setText(String s) {

        if (Defs.hasText(retrieveLocationStore)) {
            s = retrieveLocationStore + s;
        }

        if (!SaveObjectComperator.equals(s, cachedValue)) {
            cachedValue = s;
            inputField.setSrc(s);
            inputField.setText("-");
        }
    }

    public String getText() {
        throw new RuntimeException("Not implemented");
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

        if (! "0".equals(xVal)) { inputField.setMaxWidth(xVal + "px"); };
        if (! "0".equals(yVal)) { inputField.setMaxHeight(yVal + "px"); inputField.setWidth(""); };
    }

    @Override
    public void newObjectBound() { }

    @Override
    public void setIssuesUpdateConclusion() { }
}
