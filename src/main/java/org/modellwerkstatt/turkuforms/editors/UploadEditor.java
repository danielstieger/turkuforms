package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UploadEditor;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.Turku;


// (1) Kein Problem, wenn wir Button tauschen?
//
public class UploadEditor extends FormChild<Upload> implements IToolkit_UploadEditor {
    protected String uploadLocationStore;
    protected String uploadLocationRetrieve;

    protected String uploadedFileName;

    public UploadEditor(String uploadLocationStore, String uploadLocationRetrieve) {
        super(new Upload());
        this.uploadLocationStore = uploadLocationStore;
        this.uploadLocationRetrieve = uploadLocationRetrieve;

        inputField.setMaxFiles(1);
        inputField.setMaxFileSize(2000000);
        inputField.setDropAllowed(true);
        inputField.setAutoUpload(true);

        FileBuffer singleFileBuffer = new FileBuffer();
        inputField.setReceiver(singleFileBuffer);
        inputField.addSucceededListener(event -> {
            Notification.show("SUCCESS: " + event.getFileName() + " / " + singleFileBuffer.getFileData().getFile().getAbsolutePath());
            Turku.l("SUCCESS: " + event.getFileName() + " / " + singleFileBuffer.getFileData().getFile().getAbsolutePath());
        });

        inputField.addFailedListener(event -> {
            Notification n = Notification.show(event.getReason().getMessage(), 4000, Notification.Position.TOP_END);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

    }

    @Override
    public void setValidationErrorText(String s) {
        inputField.setDropLabel(new Label(s));
    }

    @Override
    public void setEnabled(boolean b) {
        inputField.setVisible(b);
    }

    @Override
    public void setEditorPrompt(String s) {

    }

    @Override
    public void newObjectBound() {

    }

    @Override
    public void setText(String s) {
        if (Defs.hasText(s)) {
            uploadedFileName = s;
            inputField.setDropLabel(new Label(s));
        }
    }

    @Override
    public String getText() {
        // return filename only, without dirs
        return uploadedFileName;
    }

    @Override
    public void setIssuesUpdateConclusion() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void setOption(Option... options) {

    }
}
