package org.modellwerkstatt.turkuforms.sdi;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.sdicore.ApplicationSDI;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.turkuforms.core.IAppCrtlAccess;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Workarounds;

public class SdiAppCrtl extends ApplicationSDI implements IAppCrtlAccess {
    final static public String TURKUSDIAPPCRTL = "AppCrtl";

    private int lastRequestHash = -1;
    private long lastRequestStarted;
    private String lastHkProcessedInThisRequest;



    public SdiAppCrtl(IToolkit_UiFactory factory, IGenAppUiModule appBehavior, AppJmxRegistration registration, IOFXCoreReporter.MoWarePlatform pltfrm) {
        super(factory, appBehavior, registration, pltfrm);
        startRequest(4711);
    }

    public void startRequest(int requestHash) {
        if (lastRequestHash == requestHash) {
            // startRequest called multiple times for some request?

        } else {

            lastRequestStarted = System.currentTimeMillis();
            lastRequestHash = requestHash;
            lastHkProcessedInThisRequest = "";
        }
    }

    public long requestDone() {
        lastHkProcessedInThisRequest = "";
        return lastRequestStarted;
    }

    public boolean sameHkInThisRequest(String newHk) {
        boolean result = lastHkProcessedInThisRequest.equals(newHk);
        lastHkProcessedInThisRequest = newHk;
        return result;

    }

    @Override
    public void internal_immediatelyShutdown() {
        // TODO - not implemented yet ?
    }

    @Override
    public void closeAppCrtlMissingHearbeatOrBeacon(VaadinSession session) {
        // TODO - also not impelmented yet?
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "_" + (hashCode() % 1000);
    }

    public static SdiAppCrtl getAppCrtl() {
        WrappedSession session = VaadinSession.getCurrent().getSession();
        SdiAppCrtl appCrtl = (SdiAppCrtl) session.getAttribute(TURKUSDIAPPCRTL);
        return appCrtl;
    }

    public static SdiAppCrtl createAppCrtl(IOFXUserEnvironment env) {
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        WrappedSession session = VaadinSession.getCurrent().getSession();

        SdiAppCrtl appCrtl = new SdiAppCrtl(servlet.getUiFactory(), servlet.getAppBehaviour(), servlet.getJmxRegistration(), IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU);
        appCrtl.initializeApplication(servlet.getAllCmdUrlDefaults(), servlet.getGuessedServerName(), env, servlet.getUiFactory().getRemoteAddr(), "");

        session.setAttribute(TURKUSDIAPPCRTL, appCrtl);
        return appCrtl;
    }

}



