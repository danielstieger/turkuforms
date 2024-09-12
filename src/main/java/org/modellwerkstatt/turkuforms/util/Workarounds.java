package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServlet;
import org.modellwerkstatt.objectflow.runtime.OFXConsoleHelper;
import org.modellwerkstatt.turkuforms.core.ITurkuAppCrtlAccess;
import org.modellwerkstatt.turkuforms.core.TurkuApp;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.sdi.BrowserTab;


/*
 * Temporary Workarounds for Vaadin 23+, primarily to access and work with server state
 *
 * Have also a look at the TurkuAppCrtl for "within a request" workarounds.
 *
 *
 *
 */

public class Workarounds {

    public static void addMlToolTipIfNec(String tooltip, Component cmpt){
        // \n is acceptable here for now, since we use
        // css property:   white-space: pre;

        if ("".equals(tooltip)) {

        } /*else if (cmpt instanceof Button) {
            ((Button) cmpt).setTooltipText(tooltip);


        } */ else {
            Tooltip t = Tooltip.forComponent(cmpt);
            t.setPosition(Tooltip.TooltipPosition.TOP);
            t.setText(tooltip);
        }
    }

    public static String niceGridHeaderLabel(String s){
        if ("".equals(s)) {
            return "-";
        }
        return s;
    }

    public static Component createIconWithCollection(String fullName, boolean needClassWorkaround) {
        Span icon = new Span(fullName);
        if (needClassWorkaround) {
            icon.getElement().setProperty("style", "font-family: Material Icons; padding-right: 0.5em; font-size: 18px; vertical-align: middle;");
        } else {
            icon.addClassName("TurkuMaterialIcon");
        }

        return icon;
    }

    public static boolean sameHkInThisRequest(String hk) {
        ITurkuAppCrtlAccess crtl = Workarounds.getControllerFormUi(UI.getCurrent());
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

    public static ITurkuAppCrtlAccess getControllerFormUi(UI ui) {
        Component mainComponent = ui.getChildren().findFirst().orElse(null);

        if (mainComponent instanceof TurkuApp) {
            return ((TurkuApp) mainComponent).getApplicationController();

        } else if (mainComponent instanceof BrowserTab) {
            return ((BrowserTab) mainComponent).getApplicationController();
        }

        return null;
    }

    /* Only used when working and reporting on http response basis without websockets.
     * Probably to remove when finally only working on websockets ...

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

    *
    */
}

