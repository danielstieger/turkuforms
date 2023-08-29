package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.turkuforms.app.TurkuApp;
import org.modellwerkstatt.turkuforms.app.TurkuApplicationController;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.forms.TurkuTableCol;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

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
        icon.getElement().setProperty("style", "font-family: Material Icons; padding-right: 0.5em;");
        // icon.addClassName("material-icons");
        return icon;
    }


    public static <DTO> void adjustColWidthToCheckbox(List<TurkuTableCol> columns) {
        List<TurkuTableCol> toAdjust = new ArrayList<>(columns);
        toAdjust.sort((t0, t1) -> Integer.compare(t1.widthInPercent, t0.widthInPercent));

        int alreadyAdjusted = 0;
        int MAX_TO_ADJUST = 4;
        for (int i = 0; i < toAdjust.size(); i++) {
            TurkuTableCol col = toAdjust.get(i);
            int diff = 0;
            int width = col.widthInPercent;

            if (width >= 40 && alreadyAdjusted < MAX_TO_ADJUST) {
                diff = (MAX_TO_ADJUST - alreadyAdjusted);

            } else if (width >= 30 && alreadyAdjusted < MAX_TO_ADJUST) {
                diff = 2;

            } else if (width >= 10 && alreadyAdjusted < MAX_TO_ADJUST) {
                diff = 1;
            }

            col.widthInPercent -= diff;
            alreadyAdjusted += diff;
        }
    }

    public static boolean sameHkInThisRequest(String hk) {
        TurkuApplicationController crtl = Workarounds.getControllerFormUi(UI.getCurrent());
        return crtl.sameHkInThisRequest(hk);
    }

    public static String litPropertyName(String origNameWithDot) {
        return origNameWithDot.replace(".","_");
    }




    /* - - - - - - - - - - - - access to local environment and session - - - - - - - - - - - - */
    public static TurkuServlet getCurrentTurkuServlet() {
        return (TurkuServlet) VaadinServlet.getCurrent();
    }

    public static TurkuApplicationController getControllerFormUi(UI ui) {
        Component mainComponent = ui.getChildren().findFirst().orElse(null);

        if (mainComponent instanceof TurkuApp) {
            return ((TurkuApp) mainComponent).getApplicationController();
        } else {
            Turku.l("Workarounds.getControllerFrom(): This can not happen, mainComponent of Vaadin UI is " + mainComponent);
            return null;
        }
    }

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
}

