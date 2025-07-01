package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_ImageEditor;
import org.modellwerkstatt.objectflow.runtime.SaveObjectComperator;
import org.modellwerkstatt.turkuforms.util.Defs;

public class ImageViewer extends FormChild<Div> implements IToolkit_ImageEditor {
    protected String cachedValue;
    protected Image image;
    protected String retrieveLocationStore;


    public ImageViewer(String rls) {
        super(new Div());
        image = new Image();
        image.addClassName("ImageViewerImage");
        image.setWidth("100%");

        super.inputField.add(image);
        super.inputField.setWidth("100%");

        retrieveLocationStore = rls;

        // another idea would be to retrieve the available
        // space in grid, then scale img
    }

    public void setText(String s) {

        if (Defs.hasText(retrieveLocationStore)) {
            s = retrieveLocationStore + s;
        }

        if (!SaveObjectComperator.equals(s, cachedValue)) {
            cachedValue = s;
            image.setSrc(s);
            image.setText("-");
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
        int ADD_SPACE = 10;

        if (! "0".equals(xVal)) {
            image.setMaxWidth(xVal + "px");
            int parentX = Integer.valueOf(xVal) + ADD_SPACE;
            super.inputField.setWidth(parentX + "px");
        }

        if (! "0".equals(yVal)) {
            image.setMaxHeight(yVal + "px");
            image.setWidth("");
            int parentY = Integer.valueOf(yVal) + ADD_SPACE;
            super.inputField.setHeight(parentY + "px"); };

    }

    @Override
    public void newObjectBound() { }

    @Override
    public void setIssuesUpdateConclusion() { }
}
