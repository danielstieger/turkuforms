package org.modellwerkstatt.turkuforms.print;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.objectflow.runtime.OFXFatClientFopUserPrintService;

import java.io.File;

public class TurkuUserPrintService extends OFXFatClientFopUserPrintService {
    protected Component theApp;
    protected String httpServedPath;


    public TurkuUserPrintService(Component app, String httpPath, IOFXUserEnvironment userEnv, String classLoadForXslTemplates, String fallBackFsDirForXsltTemplate, String filesSystemOutputPath, boolean useFopland) {
        super(userEnv, classLoadForXslTemplates, fallBackFsDirForXsltTemplate, filesSystemOutputPath, useFopland, false, true);

        this.theApp = app;
        this.httpServedPath = httpPath;
    }

    @Override
    public void view(File pdfFile) {
        // output path on filesystem was
        // http base directory is
        // remove output path from file
        if (pdfFile == null) {
            throw new RuntimeException("Given pdfFile for view() is null!");
        }
        String filePath = pdfFile.getAbsolutePath();
        String remainder = filePath.substring(this.outputFileDir.length());
        if (remainder.startsWith("/")) {
            remainder = remainder.substring(1);
        }

        // UI instance might change on refreshes
        // TODO: this is not correct in SDI apps, user UI.getCurrent(), otherwise the tab will be to the left :)
        UI ui = this.theApp.getUI().get();
        ui.getPage().open(this.httpServedPath + "/" + remainder, "_blank");
    }

    @Override
    public void print(File pdfFile) {
        this.view(pdfFile);
    }


    @Override
    public void openUrl(String url) {
        // TODO: this is not correct in SDI apps, user UI.getCurrent(), otherwise the tab will be to the left :)

        UI ui = this.theApp.getUI().get();
        ui.getPage().open(url, "_blank");
    }
}

