package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.turkuforms.app.TurkuApplicationController;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;

import javax.servlet.http.HttpSession;

public class Peculiar {

    public static TurkuApplicationController getAppCrtlFromSession(HttpSession session) {
        return (TurkuApplicationController) session.getAttribute(TurkuServlet.APPCRTL_SESSIONATTRIB_NAME);
    }

    public static TurkuApplicationController getAppCrtlFromSession(WrappedSession session) {
        return (TurkuApplicationController) session.getAttribute(TurkuServlet.APPCRTL_SESSIONATTRIB_NAME);
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

    public static void useGridShortcutHk(Component grid, String hk, ShortcutEventListener listener) {
        ShortcutRegistration reg;
        if (Defs.hkNeedsCrtl(hk)) { reg = Shortcuts.addShortcutListener(grid, listener, HkTranslate.trans(hk), KeyModifier.CONTROL); }
        else { reg = Shortcuts.addShortcutListener(grid, listener, HkTranslate.trans(hk)); }

        reg.listenOn(grid);
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
