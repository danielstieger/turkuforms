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


    private String lastHkProcessedInThisRequest = "";

    public TurkuApplicationController(IToolkit_UiFactory factory, IToolkit_Application appWin, IGenAppUiModule appBehavior, AppJmxRegistration registration, IOFXCoreReporter.MoWarePlatform pltfrm) {
        super(factory, appWin, appBehavior, registration, pltfrm);
    }

    public void startRequest() {
        // Vaadin Bug/Problems 23.3 with HK Processing
        lastHkProcessedInThisRequest = "";
    }

    public boolean sameHkInThisRequest(String newHk) {
        // TODO: Still necessary?
        boolean result = lastHkProcessedInThisRequest.equals(newHk);
        lastHkProcessedInThisRequest = newHk;
        return result;

    }

    private String sessionName() {
        return APPCRTL_SESSIONATTRIB_PREFIX + this.hashCode();
    }

    public static boolean isTurkuControllerAttribute(String name) {
        return name.startsWith(APPCRTL_SESSIONATTRIB_PREFIX);
    }

    public void registerOnSession(VaadinSession vaadinSession, String userName, String remoteAddr) {
        WrappedSession session = vaadinSession.getSession();
        session.setAttribute(sessionName(), this);
        session.setAttribute(REMOTE_SESSIONATTRIB, remoteAddr);
        session.setAttribute(USERNAME_SESSIONATTRIB, userName);
    }

    public boolean unregisterFromSessionTryInvalidate(VaadinSession vaadinSession, boolean tryInvalidate) {
        WrappedSession session = vaadinSession.getSession();
        session.removeAttribute(sessionName());

        boolean others = false;
        // other appcrtls present?
        for (String name: session.getAttributeNames()){
            if (isTurkuControllerAttribute(name)) {
                others = true;
                break;
            }
        }

        if (!others && tryInvalidate) {
            UserPrincipal.setUserPrincipal(vaadinSession, null);
            VaadinSession.getCurrent().getSession().invalidate();
            return true;
        }

        return false;
    }

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        Turku.l("TurkuApplicationController.valueBound() ");
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        Turku.l("TurkuApplicationController.valueUnbound(): shutdown in progress (" + this.inShutdownMode() + ") or shutdown now.");
        if (!this.inShutdownMode()) {
            // failback only ...
            this.internal_immediatelyShutdown();
        }
    }
}
