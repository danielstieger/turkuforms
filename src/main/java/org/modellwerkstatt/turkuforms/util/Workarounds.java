package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import org.modellwerkstatt.turkuforms.app.TurkuApplicationController;
import org.modellwerkstatt.turkuforms.forms.TurkuTableCol;

import java.util.ArrayList;
import java.util.List;

public class Workarounds {

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
        TurkuApplicationController crtl = Peculiar.getAppCrtlFromSession(UI.getCurrent().getSession().getSession());
        return crtl.sameHkInThisRequest(hk);
    }

    public static String litPropertyName(String origNameWithDot) {
        return origNameWithDot.replace(".","_");
    }

}

