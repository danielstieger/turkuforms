package org.modellwerkstatt.turkuforms.core;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.dataux.runtime.core.ApplicationMDI;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_MainWindow;
import org.modellwerkstatt.dataux.runtime.toolkit.IToolkit_UiFactory;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.turkuforms.util.Turku;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.List;

import static org.modellwerkstatt.turkuforms.core.SessionUtil.*;

public class TurkuApplicationController extends ApplicationMDI implements HttpSessionBindingListener, ITurkuAppCrtlAccess {
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


    public void beaconClose(VaadinSession session, UI closingUi) {
        Turku.l("TurkuApp.beaconClose() shutdown in progress: " + inShutdownMode() + " . . . or shutdown now.");
        // this will result in a valueUnbound()
        logMowareTracing("","", ITurkuAppFactory.TURKU_PORTJ, "closing app due to a beacon close tab call.","" + VaadinSession.getCurrent().hashCode());

        unregisterFromSessionTryInvalidate(session, false);
    }

    static public List<TurkuApplicationController> getOtherControllersInSession(VaadinSession session, TurkuApplicationController self) {
        return null;
    }



    public void registerOnSessionSetTimeout(VaadinSession vaadinSession, String userName, String remoteAddr) {
        WrappedSession session = vaadinSession.getSession();
        session.setAttribute(SessionUtil.appCrtlSessionName(this), this);
        session.setAttribute(REMOTE_SESSIONATTRIB, remoteAddr);
        session.setAttribute(USERNAME_SESSIONATTRIB, userName);

        session.setMaxInactiveInterval(MPreisAppConfig.SESSION_TIMEOUT_FOR_APP_SEC);
    }

    public boolean unregisterFromSessionTryInvalidate(VaadinSession vaadinSession, boolean immediate) {
        WrappedSession session = vaadinSession.getSession();
        session.removeAttribute(SessionUtil.appCrtlSessionName(this));

        boolean others = false;

        // other appcrtls present?
        for (String name: session.getAttributeNames()){
            if (isTurkuControllerAttribute(name)) {
                others = true;
                break;
            }
        }

        if (!others) {
            Turku.l("TurkuApplicationController.unregisterFromSessionTryInvalidate() setting invalidate timeout (or invalidate immediatelly = "+ immediate+ ")");
            setUserPrincipal(vaadinSession, null);
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
        logMowareTracing("","", ITurkuAppFactory.TURKU_PORTJ, "Unregistring from session, shutdown in progress ","" + this.inShutdownMode());

        if (!this.inShutdownMode()) {
            // failback only ...
            this.internal_immediatelyShutdown();
        }
    }
}
