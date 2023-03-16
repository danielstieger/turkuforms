package org.modellwerkstatt.turkuforms.app;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import org.modellwerkstatt.dataux.runtime.core.ApplicationController;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Application;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.turkuforms.util.Turku;


public class TurkuApplicationController extends ApplicationController implements HttpSessionBindingListener {

    // TODO: Alternativeley, should we make the TurkuApp implement the HttpSessionBindingListener?
    public TurkuApplicationController(IToolkit_UiFactory factory, IToolkit_Application appWin, IGenAppUiModule appBehavior, AppJmxRegistration registration, IOFXCoreReporter.MoWarePlatform pltfrm) {
        super(factory, appWin, appBehavior, registration, pltfrm);
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        Turku.l("TurkuApplicationController.valueBound() ");
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        Turku.l("TrukuApplicationController.valueUnbound()");
        if (!this.inShutdownMode()) {
            Turku.l("TurkuApplicationController, app not in shutdown mode - shutting it down now!");
            this.internal_immediatelyShutdown();
        }
    }
}
