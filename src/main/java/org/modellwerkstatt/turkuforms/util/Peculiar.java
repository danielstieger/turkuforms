package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;

public class Peculiar {

    public static void shrinkSpace(ThemableLayout layout){
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.setMargin(false);
    }

    public static void crtlSpaceCrtlAHk(Component box, ShortcutEventListener listener) {
        ShortcutRegistration reg;

        reg = Shortcuts.addShortcutListener(box, listener, Key.SPACE, KeyModifier.CONTROL).listenOn(box);
        reg.setBrowserDefaultAllowed(false);
        reg.setEventPropagationAllowed(false);

        reg = Shortcuts.addShortcutListener(box, listener, Key.KEY_A, KeyModifier.CONTROL).listenOn(box);
        reg.setBrowserDefaultAllowed(false);
        reg.setEventPropagationAllowed(false);

    }

    public static void focusMoveEnterHk(boolean withShift, Component field, ShortcutEventListener listener) {
        ShortcutRegistration reg;

        if (withShift) {
            reg = Shortcuts.addShortcutListener(field, listener, Key.ENTER, KeyModifier.SHIFT).listenOn(field);
        } else {
            reg = Shortcuts.addShortcutListener(field, listener, Key.ENTER).listenOn(field).listenOn(field);
        }
        reg.setBrowserDefaultAllowed(false);
        reg.setEventPropagationAllowed(false);
    }



    // Only used for simple button shortcuts, prompt confirmation window
    // login window, etc.
    public static void useButtonShortcutHk(Button button, String hk) {
        ShortcutRegistration reg;
        if (Defs.hkNeedsCrtl(hk)) { reg = button.addClickShortcut(HkTranslate.trans(hk), KeyModifier.CONTROL); }
        else { reg = button.addClickShortcut(HkTranslate.trans(hk)); }

        reg.setBrowserDefaultAllowed(false);
        reg.setEventPropagationAllowed(false);
    }

    public static void useGridShortcutHk(Component grid, String hk, ShortcutEventListener listener) {
        // Turku.l("Peculiar.useGridShortcutHk() registering HK for " + hk);

        ShortcutRegistration reg;
        if (Defs.hkNeedsCrtl(hk)) { reg = Shortcuts.addShortcutListener(grid, listener, HkTranslate.trans(hk), KeyModifier.CONTROL); }
        else { reg = Shortcuts.addShortcutListener(grid, listener, HkTranslate.trans(hk)); }

        reg.listenOn(grid);
        reg.setBrowserDefaultAllowed(false);
        reg.setEventPropagationAllowed(false);
    }

    public static void useGlobalShortcutHk(Component layout, String hk, ShortcutEventListener listener) {
        // Turku.l("Peculiar.useGlobalShortcutHk() registering HK for " + hk);
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
