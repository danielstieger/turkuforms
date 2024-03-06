package org.modellwerkstatt.turkuforms.editors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UploadEditor;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.util.Defs;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.io.File;



public class UploadEditor extends FormChild<Upload> implements IToolkit_UploadEditor {
    protected String uploadFsLocationStore;

    protected String currentFilename;

    public UploadEditor(String uls, ITurkuAppFactory factory) {
        super(new Upload());
        uploadFsLocationStore = uls;

        inputField.setMaxFiles(1);
        inputField.setMaxFileSize(factory.getUploadMaxSize());
        inputField.setDropAllowed(true);
        inputField.setAutoUpload(true);

        FileBuffer singleFileBuffer = new FileBuffer();
        inputField.setReceiver(singleFileBuffer);
        inputField.addSucceededListener(event -> {

            try {
                File newFile;
                if (Defs.hasText(uploadFsLocationStore)) {
                    newFile = new File(uploadFsLocationStore + event.getFileName());
                } else {
                    newFile = new File(singleFileBuffer.getFileData().getFile().getParentFile().getAbsolutePath() + File.separator + event.getFileName());
                }

                boolean ok = singleFileBuffer.getFileData().getFile().renameTo(newFile);
                if (!ok) {
                    throw new RuntimeException("Not able to move file to " + newFile.getAbsolutePath() + " after upload.");
                }

                if (Defs.hasText(uploadFsLocationStore)) {
                    setText(event.getFileName());

                } else {
                    setText(newFile.getAbsolutePath());

                }

                Notification n = Notification.show(String.format(factory.getSystemLabel(-1, MoWareTranslations.Key.UPLOAD_SUCCESS), event.getFileName()), 4000, Notification.Position.TOP_END);
                n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                Workarounds.getControllerFormUi(UI.getCurrent()).logAppTrace("", "", UploadEditor.class.getName(),newFile.getAbsolutePath() + " uploaded successfully.", "", null);


            } catch (Throwable e) {
                Notification n = Notification.show(e.getMessage(), 4000, Notification.Position.TOP_END);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);

                Workarounds.getControllerFormUi(UI.getCurrent()).logFrmwrkProblem("", "", UploadEditor.class.getName(), e, "Problem in success listener of upload editor.");
            }
        });

        inputField.addFailedListener(event -> {
            Notification n = Notification.show(event.getReason().getMessage(), 4000, Notification.Position.TOP_END);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);

            Workarounds.getControllerFormUi(UI.getCurrent()).logFrmwrkProblem("", "", UploadEditor.class.getName(), event.getReason(), "Problem in failed listener of upload editor.");

        });

    }

    @Override
    public void setValidationErrorText(String s) {
        if (Defs.hasText(s)) {
            inputField.addClassName("uploadEditorError");
            inputField.setDropLabel(new Label(s));

        } else {
            inputField.removeClassName("uploadEditorError");
            inputField.setDropLabel(new Label(""));
        }

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
            currentFilename = s;
            // do not show full path.
            inputField.setDropLabel(new Label(removePath(s)));
        }
    }

    @Override
    public String getText() {
        // return filename only, without dirs
        return currentFilename;
    }

    @Override
    public void setIssuesUpdateConclusion() {
        throw new RuntimeException("Not implemented");
    }

    public static String removePath(String filePath) {
        int li = filePath.lastIndexOf(File.separator);
        Turku.l("UploadEditor() searching for " + File.separator + " in " + filePath + " ==> " + li);
        if (li < 0) { return filePath; }
        return filePath.substring(li + 1);
    }
}
