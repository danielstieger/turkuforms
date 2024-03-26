package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.objectflow.runtime.OFXConsoleHelper;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.core.TurkuApp;
import org.modellwerkstatt.turkuforms.core.TurkuApplicationController;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/*
 * Temporary Workarounds for Vaadin 23+, primarily to access and work with server state
 *
 * Have also a look at the TurkuAppCrtl for "within a request" workarounds.
 *
 *
 *
 */

public class Workarounds {
    public final static String INTERNAL_VAADIN_SESSION_NAME = "com.vaadin.flow.server.VaadinSession.loaderservlet";
    public final static String INTERNAL_VAADIN_UID_NAME = "v-uiId";

    public static String mlToolTipText(String tooltip){
        // \n is acceptable here for now, since we use
        // css property:   white-space: pre;
        return tooltip;
    }

    public static String niceGridHeaderLabel(String s){
        if ("".equals(s)) {
            return "-";
        }
        return s;
    }



    public static Component createIconWithCollection(String fullName) {
        /* try {
            Icon icon;

            if (fullName.contains(":")) {
                String[] parts = fullName.split(":");
                icon = new Icon(parts[0], parts[1]);
            } else {
                icon = new Icon("vaadin", fullName);
            }

            icon.addClassName("TurkulayoutMenuIcon");
            return icon;

        } catch (Throwable t) {
            throw new RuntimeException("While looking for icon '" + fullName + "'", t);
        } */

        Span icon = new Span(fullName);
        icon.getElement().setProperty("style", "font-family: Material Icons; padding-right: 0.5em; font-size: 18px; vertical-align: middle;");
        // icon.addClassName("material-icons");
        return icon;
    }

    @Deprecated
    public static boolean sameHkInThisRequest(String hk) {
        TurkuApplicationController crtl = Workarounds.getControllerFormUi(UI.getCurrent());
        return crtl.sameHkInThisRequest(hk);
    }

    public static String litPropertyName(String origNameWithDot) {
        return origNameWithDot.replace(".","_");
    }



    public static boolean isHeartBeatRequest(VaadinRequest request) {
        String rParam = request.getParameter("v-r");
        return "heartbeat".equals(rParam);
    }

    /* - - - - - - - - - - - - access to local environment and session - - - - - - - - - - - - */
    public static boolean closedByMissingHearbeat() {
        // @PreserveOnRefresh also issues attach and detach events when swapping UI instances.
        // Thus we can not simply call internal_shutdown on detach. Maybe the TurkuApp is reattached
        // in the same request.
        String stacktrace = OFXConsoleHelper._____organizeCurrentStacktrace_____();
        return stacktrace.contains("VaadinService.cleanupSession");
    }

    public static TurkuServlet getCurrentTurkuServlet() {
        return (TurkuServlet) VaadinServlet.getCurrent();
    }

    public static TurkuApplicationController getControllerFormUi(UI ui) {
        Component mainComponent = ui.getChildren().findFirst().orElse(null);

        if (mainComponent instanceof TurkuApp) {
            return ((TurkuApp) mainComponent).getApplicationController();

        } else {
            // LoginComponents etc. ?
            return null;
        }
    }

    /* Only used when working and reporting on http response basis without websockets.
     * Probably to remove when finally only working on websockets ...
     */
    public static TurkuApplicationController getControllerFromRequest(HttpServletRequest request, HttpSession httpSession) {
        VaadinSession vaadinSession = (VaadinSession) httpSession.getAttribute(INTERNAL_VAADIN_SESSION_NAME);
        String[] vuiId = request.getParameterMap().get(INTERNAL_VAADIN_UID_NAME);

        if (vaadinSession != null && vuiId != null && vuiId.length > 0) {
            int vuiIdAsInteger = Integer.parseInt(vuiId[0]);
            UI currentUi = vaadinSession.getUIById(vuiIdAsInteger);
            if (currentUi == null) { throw new RuntimeException("This can not happen, currentUi is null for request"); }
            return getControllerFormUi(currentUi);
        }
        return null;
    }
    public static TurkuApplicationController getControllerFromRequest(VaadinRequest request, WrappedSession wrappedHttpSession) {
        VaadinSession vaadinSession = (VaadinSession) wrappedHttpSession.getAttribute(INTERNAL_VAADIN_SESSION_NAME);
        String[] vuiId = request.getParameterMap().get(INTERNAL_VAADIN_UID_NAME);

        if (vaadinSession != null && vuiId != null && vuiId.length > 0) {
            int vuiIdAsInteger = Integer.parseInt(vuiId[0]);
            UI currentUi = vaadinSession.getUIById(vuiIdAsInteger);
            if (currentUi == null) { throw new RuntimeException("This can not happen, currentUi is null for request"); }
            return getControllerFormUi(currentUi);
        }
        return null;
    }

    public static UserEnvironmentInformation getAndClearUserEnvFromUi() {
        UI current = UI.getCurrent();
        UserEnvironmentInformation env = (UserEnvironmentInformation) ComponentUtil.getData(current,"uiCurrentUserEnv");
        setUserEnvForUi(null);
        return env;
    }

    public static void setUserEnvForUi(UserEnvironmentInformation env) {
        UI current = UI.getCurrent();
        ComponentUtil.setData(current,"uiCurrentUserEnv", env);
    }

}

