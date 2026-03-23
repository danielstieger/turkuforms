package org.modellwerkstatt.turkuforms.core2;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.dataux.runtime.core.ApplicationMDI;
import org.modellwerkstatt.dataux.runtime.core.GlobalCmdTermEvent;
import org.modellwerkstatt.dataux.runtime.genspecification.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.telemetrics.Dux;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.turkuforms.core.ITurkuAppCrtlAccess;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.MPreisAppConfig;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.ArrayList;
import java.util.List;

import static org.modellwerkstatt.turkuforms.core.SessionUtil.*;

public class TurkuAppCrtl2 extends ApplicationMDI implements HttpSessionBindingListener, ITurkuAppCrtlAccess {
    private int lastRequestHash = -1;
    private long lastRequestStarted;
    private String lastHkProcessedInThisRequest;

    public TurkuAppCrtl2(IToolkit_UiFactory factory, IToolkit_MainWindow appWin, IGenAppUiModule appBehavior, AppJmxRegistration registration, IOFXCoreReporter.MoWarePlatform pltfrm) {
        super(factory, appWin, appBehavior, registration, pltfrm);

        Dux.hl("Initializing application controller " + this.hashCode());
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

    public List<TurkuAppCrtl2> allAppCrtlsInSession(VaadinSession vaadinSession, boolean includingThis) {
        List<TurkuAppCrtl2> crtls = new ArrayList<>();

        WrappedSession session = vaadinSession.getSession();

        for (String name: session.getAttributeNames()){
            if (isTurkuControllerAttribute(name)) {
                Object crtlObj = session.getAttribute(name);

                if (crtlObj != this || includingThis) {
                    crtls.add((TurkuAppCrtl2) crtlObj);
                }
            }

        }
        return crtls;
    }

    public void beaconClose(VaadinSession session, UI closingUi) {
        Dux.hl("Beacon close requested. Shutdown in progress: " + inShutdownMode());

        logMowareTracing("","", ITurkuAppFactory.TURKU_PORTJ, "closing app due to a beacon close tab call.","" + VaadinSession.getCurrent().hashCode());
        unregisterFromSessionTryInvalidate(session, false);
    }


    public void registerOnSessionSetTimeout(VaadinSession vaadinSession, String userName, String remoteAddr) {
        WrappedSession session = vaadinSession.getSession();
        session.setAttribute(appCrtlSessionName(this), this);
        session.setAttribute(REMOTE_SESSIONATTRIB, remoteAddr);
        session.setAttribute(USERNAME_SESSIONATTRIB, userName);

        session.setMaxInactiveInterval(MPreisAppConfig.SESSION_TIMEOUT_FOR_APP_SEC);
    }

    public boolean unregisterFromSessionTryInvalidate(VaadinSession vaadinSession, boolean immediatelyParDeploy) {
        WrappedSession session = vaadinSession.getSession();
        session.removeAttribute(appCrtlSessionName(this));


        List<TurkuAppCrtl2> others = allAppCrtlsInSession(vaadinSession, false);

        if (others.size() == 0) {
            Dux.hl("Setting session invalidation timeout. Immediate parallel deploy: " + immediatelyParDeploy);
            session.setAttribute(USERNAME_SESSIONATTRIB, session.getAttribute(USERNAME_SESSIONATTRIB) + " unregistered");
            // TODO: why not generally invalidate immediatelly?
            session.setMaxInactiveInterval(MPreisAppConfig.NEW_SESSION_TIMEOUT_INVALIDATE_SEC_SHORT);

            if (immediatelyParDeploy) {
                session.invalidate();
            }

            return true;
        }

        return false;
    }

    public void distributeTermEventOnOtherCrtls(GlobalCmdTermEvent evnt) {
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        // case: servlet shutdown
        if (vaadinSession != null) {
            allAppCrtlsInSession(vaadinSession, false).forEach(crtl -> {
                try {
                    ((TurkuMainWin2) crtl.getMainWindowImpl()).getUI().get().access(() -> {
                        // in case windows are next to each other - live update
                        crtl.reciveForeignTermEvent(evnt);
                    });

                } catch (Exception ex) {
                    logFrmwrkProblem("","", ITurkuAppFactory.TURKU_PORTJ, ex, "Got some issues while trying to update " + crtl);
                }

            });
        }
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {

    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        Dux.hl("Session unbound. Shutdown in progress: " + this.inShutdownMode());

        // Just this controller, not others of the httpSession
        logMowareTracing("","", ITurkuAppFactory.TURKU_PORTJ, "Unregistring from session, shutdown in progress ","" + this.inShutdownMode());

        if (!this.inShutdownMode()) {
            // failback only ...
            this.internal_immediatelyShutdown();
        }
    }

    @Override
    public ApplicationMDI getAppMDI() {
        return this;
    }
}
