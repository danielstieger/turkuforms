package org.modellwerkstatt.turkuforms.print;

import org.modellwerkstatt.objectflow.runtime.IOFXPrintFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.objectflow.runtime.IPrintingServiceImpl;

public class TurkuPrintFactory implements IOFXPrintFactory {
    protected String templateClassLoaderFqName;
    protected String fallBackFileSystemTemplateLoadingPath;
    protected String printerOutputPath;
    protected String httpServedPath;
    protected boolean useFoplandModfier;
    protected String fontPath;

    public TurkuPrintFactory(String templateClassLoaderFqName, String fallBackFileSystemTemplateLoadingPath, String printerOutputPath, String httpServedPath, boolean useFoplandModifier, String fontPath) {
        this.templateClassLoaderFqName = templateClassLoaderFqName.trim();
        this.fallBackFileSystemTemplateLoadingPath = fallBackFileSystemTemplateLoadingPath.trim();
        this.printerOutputPath = printerOutputPath.trim();
        this.httpServedPath = httpServedPath.trim();
        this.useFoplandModfier = useFoplandModifier;

        // unix
        this.fontPath = fontPath;
        // http path as well as output path can not be empty
        if ("".equals(this.printerOutputPath) || "".equals(this.httpServedPath)) {
            throw new RuntimeException("Output Path as well as http-served Path must be given.");
        }
    }

    public IPrintingServiceImpl createConfiguredUserPrintService(Object tecHandle, IOFXUserEnvironment userEnvironment) {
        TurkuUserPrintService printService = new TurkuUserPrintService(
                this.httpServedPath,
                userEnvironment,
                this.templateClassLoaderFqName,
                this.fallBackFileSystemTemplateLoadingPath,
                this.printerOutputPath,
                this.useFoplandModfier);

        printService.selfconfigureFopFactory(this.fontPath);
        return printService;
    }

    public void gcClean() {
    }

}