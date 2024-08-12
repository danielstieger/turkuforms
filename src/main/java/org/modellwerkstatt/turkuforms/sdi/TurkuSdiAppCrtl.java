package org.modellwerkstatt.turkuforms.sdi;

import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.sdicore.ApplicationSDI;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;

public class TurkuSdiAppCrtl extends ApplicationSDI {

    public TurkuSdiAppCrtl(IToolkit_UiFactory factory, IToolkit_MainWindow appWin, IGenAppUiModule appBehavior, AppJmxRegistration registration, IOFXCoreReporter.MoWarePlatform pltfrm) {
        super(factory, appWin, appBehavior, registration, pltfrm);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "_" + (hashCode() % 1000);
    }
}



