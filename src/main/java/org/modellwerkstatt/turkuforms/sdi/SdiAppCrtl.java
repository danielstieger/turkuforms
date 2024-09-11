package org.modellwerkstatt.turkuforms.sdi;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.sdicore.ApplicationSDI;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.IOFXUserEnvironment;
import org.modellwerkstatt.turkuforms.core.ITurkuAppCrtlAccess;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.MPreisAppConfig;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import static org.modellwerkstatt.turkuforms.auth.UserPrincipal.USERPRINCIPAL_SESSIONATTRIB;
import static org.modellwerkstatt.turkuforms.core.TurkuApplicationController.REMOTE_SESSIONATTRIB;
import static org.modellwerkstatt.turkuforms.core.TurkuApplicationController.USERNAME_SESSIONATTRIB;


public class SdiAppCrtl extends ApplicationSDI implements ITurkuAppCrtlAccess, HttpSessionBindingListener {
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
    public void closeAppCrtlMissingHearbeatOrBeacon(VaadinSession session) {
        // TODO - also not impelmented yet?
    }

    @Override
    public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {

    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
        Turku.l("SdiAppCrtl.valueUnbound(): shutdown in progress (" + inShutdownMode() + ") or shutdown now.");

        // Just this controller, not others of the httpSession
        if (!inShutdownMode()) {
            // fallback only ...
            internal_shutdownSDIAppImmediatelly();
        }
    }

    public static SdiAppCrtl getAppCrtl() {
        WrappedSession session = VaadinSession.getCurrent().getSession();
        SdiAppCrtl appCrtl = (SdiAppCrtl) session.getAttribute(TURKUSDIAPPCRTL);
        return appCrtl;
    }

    public static SdiAppCrtl createAppCrtlOnSession(IOFXUserEnvironment env) {
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        ITurkuAppFactory factory = servlet.getUiFactory();
        WrappedSession session = VaadinSession.getCurrent().getSession();

        SdiAppCrtl appCrtl = new SdiAppCrtl(factory, servlet.getAppBehaviour(), servlet.getJmxRegistration(), IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU);
        appCrtl.initializeApplication(factory.getAllCmdUrlDefaults(), servlet.getGuessedServerName(), env, servlet.getUiFactory().getRemoteAddr(), "");

        session.setAttribute(TURKUSDIAPPCRTL, appCrtl);
        session.setAttribute(REMOTE_SESSIONATTRIB, servlet.getUiFactory().getRemoteAddr());
        session.setAttribute(USERNAME_SESSIONATTRIB, env.getUserName());

        // get rid of user principal - not used in sdi
        session.removeAttribute(USERPRINCIPAL_SESSIONATTRIB);

        session.setMaxInactiveInterval(MPreisAppConfig.SESSION_TIMEOUT_FOR_APP_SEC);

        return appCrtl;
    }


}



