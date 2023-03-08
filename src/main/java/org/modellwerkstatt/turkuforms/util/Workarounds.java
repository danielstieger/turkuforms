package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.WrappedSession;
import javafx.application.Application;
import org.modellwerkstatt.dataux.runtime.core.ApplicationController;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.forms.TurkuTableCol;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class Workarounds {

    public static ApplicationController getAppCrtlFromSession(HttpSession session) {
        return (ApplicationController) session.getAttribute(TurkuServlet.APPCRTL_SESSIONATTRIB_NAME);
    }
    public static ApplicationController getAppCrtlFromSession(WrappedSession session) {
        return (ApplicationController) session.getAttribute(TurkuServlet.APPCRTL_SESSIONATTRIB_NAME);
    }

    public static String mlToolTipText(String tooltip){
        // \n is acceptable here for now, since we use
        // css property:   white-space: pre;
        return tooltip;
    }

    public static Icon createIconWithCollection(String fullName) {
        try {
            if (fullName.contains(":")) {
                String[] parts = fullName.split(":");
                return new Icon(parts[0], parts[1]);
            }
            return new Icon("vaadin", fullName);

        } catch (Throwable t) {
            throw new RuntimeException("While looking for icon '" + fullName + "'", t);
        }
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

    public static void shrinkSpace(ThemableLayout layout){
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setMargin(false);
    }


    public static void useButtonShortcutHk(Button button, String hk) {
        ShortcutRegistration reg;
        if (Defs.hkNeedsCrtl(hk)) { reg = button.addClickShortcut(HkTranslate.trans(hk), KeyModifier.CONTROL); }
        else { reg = button.addClickShortcut(HkTranslate.trans(hk)); }

        reg.setBrowserDefaultAllowed(false);
        reg.setEventPropagationAllowed(false);
    }

    public static void useGlobalShortcutHk(Component layout, String hk, ShortcutEventListener listener) {
        ShortcutRegistration reg;

        if (Defs.hkNeedsCrtl(hk)) {
            reg = Shortcuts.addShortcutListener(layout, listener, HkTranslate.trans(hk), KeyModifier.CONTROL);
        } else {
            reg = Shortcuts.addShortcutListener(layout, listener, HkTranslate.trans(hk));
        }

        reg.setEventPropagationAllowed(false);
        reg.setBrowserDefaultAllowed(false);
    }

    public static void installMowareAddonHotkeys(Component layout, ShortcutEventListener listener) {
        ShortcutRegistration reg;

        reg = UI.getCurrent().addShortcutListener(listener, Key.F5, KeyModifier.SHIFT);
        reg.setEventPropagationAllowed(false);
        reg.setBrowserDefaultAllowed(false);
        reg = UI.getCurrent().addShortcutListener(listener, Key.F6, KeyModifier.SHIFT);
        reg.setEventPropagationAllowed(false);
        reg.setBrowserDefaultAllowed(false);
    }

}

