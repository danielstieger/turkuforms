package org.modellwerkstatt.turkuforms.core;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.UIDetachedException;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.joda.time.DateTime;
import org.modellwerkstatt.dataux.runtime.core.ApplicationMDI;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class TurkuApplicationController extends ApplicationMDI implements HttpSessionBindingListener, ITurkuAppCrtlAccess {
    public final static String APPCRTL_SESSIONATTRIB_PREFIX = "org.modelwerkstatt.TurkuAppCrtl_";
    public final static String USERNAME_SESSIONATTRIB = "userName";
    public final static String REMOTE_SESSIONATTRIB = "remoteAddr";
    public final static String TURKU_PORTJ = "org.modellwerkstatt.turkuforms";



    private int lastRequestHash = -1;
    private long lastRequestStarted;
    private String lastHkProcessedInThisRequest;

    public TurkuApplicationController(IToolkit_UiFactory factory, IToolkit_MainWindow appWin, IGenAppUiModule appBehavior, AppJmxRegistration registration, IOFXCoreReporter.MoWarePlatform pltfrm) {
        super(factory, appWin, appBehavior, registration, pltfrm);

        Turku.l("TurkuApplicationController() initialization of " + this.hashCode());
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

    public void beaconClose(VaadinSession session, UI closingUi) {
        Turku.l("TurkuApp.beaconClose() shutdown in progress: " + inShutdownMode() + " . . . or shutdown now.");
        // this will result in a valueUnbound()
        logMowareTracing("","", TURKU_PORTJ, "closing app due to a beacon close tab call.","" + VaadinSession.getCurrent().hashCode());

        unregisterFromSessionTryInvalidate(session, false);
    }

    static public boolean hasOtherControllersInSession(VaadinSession vaadinSession) {
        if (vaadinSession == null || vaadinSession.getSession() == null) { return false; }

        WrappedSession session = vaadinSession.getSession();

        for (String name: session.getAttributeNames()){
            if (isTurkuControllerAttribute(name)) {
                return true;
            }
        }
        return false;
    }

    static public void shutdownOtherControllersInSession(VaadinSession vaadinSession) {
        WrappedSession session = vaadinSession.getSession();

        long crtlSPresent = session.getAttributeNames().stream().filter(TurkuApplicationController::isTurkuControllerAttribute).count();

        for (String name: session.getAttributeNames()){
            if (isTurkuControllerAttribute(name)) {

                TurkuApplicationController crtl = (TurkuApplicationController) session.getAttribute(name);
                TurkuApp mainWin = (TurkuApp) crtl.getMainWindowImpl();

                try {
                    if (mainWin.getUI().isPresent() && mainWin.getUI().get().isAttached()) {
                        mainWin.getUI().get().access(() -> {
                            crtl.logMowareTracing("", "", TURKU_PORTJ, "shutdown other controllers, shutting down this one.", "" + vaadinSession.hashCode());
                            crtl.onExitRequested(true);
                        });

                    } else {
                        Turku.l("TurkuApplicationController.shutdownOtherControllersInSession() NO UI FOR " + name + " - doing a shutdown without ui.access({}).");
                        crtl.logMowareTracing("", "", TURKU_PORTJ, "shutdown other controllers, shutting down this one WITHOUT UI ACCESS.", "" + vaadinSession.hashCode());
                        crtl.onExitRequested(true);

                    }

                } catch (Throwable t) {
                    System.err.println("TurkuApplicationController " + new DateTime() + " (crtlcnt " + crtlSPresent +") Problem with " + crtl);
                    t.printStackTrace();

                }
                Turku.l("TurkuApplicationController.shutdownOtherControllersInSession() exited " + name);
            }
        }
    }


    public void registerOnSessionSetTimeout(VaadinSession vaadinSession, String userName, String remoteAddr) {
        WrappedSession session = vaadinSession.getSession();
        session.setAttribute(appCrtlSessionName(), this);
        session.setAttribute(REMOTE_SESSIONATTRIB, remoteAddr);
        session.setAttribute(USERNAME_SESSIONATTRIB, userName);

        session.setMaxInactiveInterval(MPreisAppConfig.SESSION_TIMEOUT_FOR_APP_SEC);
    }

    public boolean unregisterFromSessionTryInvalidate(VaadinSession vaadinSession, boolean immediate) {
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

        if (!others) {
            Turku.l("TurkuApplicationController.unregisterFromSessionTryInvalidate() invalidating session");
            UserPrincipal.setUserPrincipal(vaadinSession, null);
            session.setAttribute(USERNAME_SESSIONATTRIB, session.getAttribute(USERNAME_SESSIONATTRIB) + " unregistered");
            session.setMaxInactiveInterval(MPreisAppConfig.SESSION_TIMEOUT_INVALIDATE_SEC);

            if (immediate) {
                session.invalidate();
            }

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
        logMowareTracing("","", TURKU_PORTJ, "Unregistring from session, shutdown in progress ","" + this.inShutdownMode());

        if (!this.inShutdownMode()) {
            // failback only ...
            this.internal_immediatelyShutdown();
        }
    }
}
