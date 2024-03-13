package org.modellwerkstatt.turkuforms.app;


import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.dataux.runtime.core.ApplicationController;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_Application;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.util.Turku;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class TurkuApplicationController extends ApplicationController implements HttpSessionBindingListener {
    public final static String APPCRTL_SESSIONATTRIB_PREFIX = "org.modelwerkstatt.TurkuAppCrtl_";
    public final static String USERNAME_SESSIONATTRIB = "userName";
    public final static String REMOTE_SESSIONATTRIB = "remoteAddr";



    private int lastRequestHash = -1;
    private long lastRequestStarted;
    private String lastHkProcessedInThisRequest;

    public TurkuApplicationController(IToolkit_UiFactory factory, IToolkit_Application appWin, IGenAppUiModule appBehavior, AppJmxRegistration registration, IOFXCoreReporter.MoWarePlatform pltfrm) {
        super(factory, appWin, appBehavior, registration, pltfrm);

        // upon init, take this as req.
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

    @Deprecated
    public boolean sameHkInThisRequest(String newHk) {
        boolean result = lastHkProcessedInThisRequest.equals(newHk);
        lastHkProcessedInThisRequest = newHk;
        return result;

    }

    private String appCrtlSessionName() {
        return APPCRTL_SESSIONATTRIB_PREFIX + this.hashCode();
    }

    public static boolean isTurkuControllerAttribute(String name) {
        return name.startsWith(APPCRTL_SESSIONATTRIB_PREFIX);
    }

    public void closeAppCrtlMissingHearbeatOrBeacon(VaadinSession session) {
        Turku.l("TurkuApp.closeAppCrtlMissingHearbeatOrBeacon() shutdown in progress: " + inShutdownMode() + " . . . or shutdown now.");
        // this will result in a valueUnbound()
        unregisterFromSessionTryInvalidate(session, true);
    }

    public void shutdownOtherExistingControllers(VaadinSession vaadinSession){
        WrappedSession session = vaadinSession.getSession();
        String own = appCrtlSessionName();

        for (String name: session.getAttributeNames()){
            if (isTurkuControllerAttribute(name) && !name.equals(own)) {

                TurkuApplicationController crtl = (TurkuApplicationController) session.getAttribute(name);
                TurkuApp mainWin = (TurkuApp) crtl.getTecMainWindowHandle();

                mainWin.getUI().get().access(() -> crtl.onExitRequestedEvent(true));
                Turku.l("TurkuApplicationController.shutdownOtherExistingControllers() exited " + name);
            }
        }
    }

    public void registerOnSession(VaadinSession vaadinSession, String userName, String remoteAddr) {
        WrappedSession session = vaadinSession.getSession();
        session.setAttribute(appCrtlSessionName(), this);
        session.setAttribute(REMOTE_SESSIONATTRIB, remoteAddr);
        session.setAttribute(USERNAME_SESSIONATTRIB, userName);
    }

    public boolean unregisterFromSessionTryInvalidate(VaadinSession vaadinSession, boolean tryInvalidate) {
        WrappedSession session = vaadinSession.getSession();
        session.removeAttribute(appCrtlSessionName());

        boolean others = false;

        // other appcrtls present?
        for (String name: session.getAttributeNames()){
            if (isTurkuControllerAttribute(name)) {
                others = true;
                break;
            }
        }

        if (!others && tryInvalidate) {
            Turku.l("TurkuApplicationController.unregisterFromSessionTryInvalidate() invalidating session");
            UserPrincipal.setUserPrincipal(vaadinSession, null);
            VaadinSession.getCurrent().getSession().invalidate();
            return true;
        }

        return false;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {

    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        Turku.l("TurkuApplicationController.valueUnbound(): shutdown in progress (" + this.inShutdownMode() + ") or shutdown now.");

        // Just this controller, not others of the httpSession
        if (!this.inShutdownMode()) {
            // failback only ...
            this.internal_immediatelyShutdown();
        }
    }
}
