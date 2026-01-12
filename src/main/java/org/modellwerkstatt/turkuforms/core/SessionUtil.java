package org.modellwerkstatt.turkuforms.core;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.joda.time.DateTime;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.util.Turku;

public class SessionUtil {
    public final static String APPCRTL_SESSIONATTRIB_PREFIX = "appCrtl_";
    public final static String USERNAME_SESSIONATTRIB = "userName";
    public final static String REMOTE_SESSIONATTRIB = "remoteAddr";
    public final static String USERPRINCIPAL_SESSIONATTRIB = "userPrincipal";
    public final static String PARAMS_SESSIONATTRIB = "tempLandingParams";
    public final static String USERENV_SESSIONATTRIB = "tempLandingUserEnv";

    public SessionUtil() {

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

        long crtlSPresent = session.getAttributeNames().stream().filter(SessionUtil::isTurkuControllerAttribute).count();

        for (String name: session.getAttributeNames()){
            if (isTurkuControllerAttribute(name)) {
                TurkuApplicationController crtl = (TurkuApplicationController) session.getAttribute(name);
                TurkuApp mainWin = (TurkuApp) crtl.getMainWindowImpl();

                try {
                    if (mainWin.getUI().isPresent() && mainWin.getUI().get().isAttached()) {
                        mainWin.getUI().get().access(() -> {
                            crtl.logMowareTracing("", "", ITurkuAppFactory.TURKU_PORTJ, "shutdown other controllers, shutting down this one.", "" + vaadinSession.hashCode());
                            crtl.onExitRequested(true);
                        });

                    } else {
                        // This leads to mem leaks in V23
                        Turku.l("TurkuApplicationController.shutdownOtherControllersInSession() NO UI FOR " + name + " - doing a shutdown without ui.access({}).");

                        crtl.logMowareTracing("", "", ITurkuAppFactory.TURKU_PORTJ, "shutdown other controllers, shutting down this one WITHOUT UI ACCESS.", "" + vaadinSession.hashCode());
                        crtl.onExitRequested(true);

                    }


                } catch (Throwable t) {
                    System.err.println("TurkuApplicationController " + new DateTime() + " (crtlcnt " + crtlSPresent +") Problem with " + crtl);
                    t.printStackTrace();

                    if (!crtl.inShutdownMode()) {
                        crtl.onExitRequested(true);
                    }

                }
                Turku.l("TurkuApplicationController.shutdownOtherControllersInSession() closed down " + name);
            }
        }
    }



    public static String appCrtlSessionName(ITurkuAppCrtlAccess crtl) {
        return APPCRTL_SESSIONATTRIB_PREFIX + crtl.hashCode();
    }

    public static boolean isTurkuControllerAttribute(String name) {
        return name.startsWith(APPCRTL_SESSIONATTRIB_PREFIX);
    }


    public static UserPrincipal getUserPrincipal(VaadinSession vaadinSession) {
        WrappedSession session = vaadinSession.getSession();
        return (UserPrincipal) session.getAttribute(USERPRINCIPAL_SESSIONATTRIB);
    }

    public static void setUserPrincipal(VaadinSession vaadinSession, UserPrincipal principal) {
        WrappedSession session = vaadinSession.getSession();
        session.setAttribute(USERPRINCIPAL_SESSIONATTRIB, principal);
    }

    public static ParamInfo getAndClearParamInfo(VaadinSession vaadinSession) {
        WrappedSession session = vaadinSession.getSession();
        ParamInfo info = (ParamInfo) session.getAttribute(PARAMS_SESSIONATTRIB);
        setParamInfo(vaadinSession,null);
        return info;
    }

    public static void setParamInfo(VaadinSession vaadinSession, ParamInfo info) {
        WrappedSession session = vaadinSession.getSession();
        session.setAttribute(PARAMS_SESSIONATTRIB, info);
    }


}
