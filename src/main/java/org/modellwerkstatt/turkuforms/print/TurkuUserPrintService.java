package org.modellwerkstatt.turkuforms.print;

import com.vaadin.flow.component.UI;
import org.modellwerkstatt.dataux.runtime.telemetrics.Dux;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.objectflow.runtime.OFXFatClientFopUserPrintService;

import java.io.File;

public class TurkuUserPrintService extends OFXFatClientFopUserPrintService {
    protected String httpServedPath;


    public TurkuUserPrintService(String httpPath, IOFXUserEnvironment userEnv, String classLoadForXslTemplates, String fallBackFsDirForXsltTemplate, String filesSystemOutputPath, boolean useFopland) {
        super(userEnv, classLoadForXslTemplates, fallBackFsDirForXsltTemplate, filesSystemOutputPath, useFopland, false, true);

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

        UI ui = UI.getCurrent();
        Dux.hl("Opening " + this.httpServedPath + "/" + remainder + " in UI " + ui);

        ui.getPage().open(this.httpServedPath + "/" + remainder, "_blank");
        Dux.hl("Open request sent.");
    }

    @Override
    public void print(File pdfFile) {
        this.view(pdfFile);
    }


    @Override
    public void openUrl(String url) {
        UI ui = UI.getCurrent();
        ui.getPage().open(url, "_blank");
    }
}
