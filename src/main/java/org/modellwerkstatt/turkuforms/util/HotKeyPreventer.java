package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.Shortcuts;

public class HotKeyPreventer {

    // Only single string keys .. : /
    private Key[] minNecessary = new Key[] {Key.F3, Key.F4, Key.F5, Key.F6, Key.F7, Key.F10, Key.F12};


    public HotKeyPreventer() {

    }

    public void remove(Key key) {
        for (int i = 0; i < minNecessary.length; i++) {
           if (minNecessary[i] != null && key.matches(minNecessary[i].getKeys().get(0))) {
                minNecessary[i] = null;
                break;
            }
        }
    }

    public void registerRemaining(Component layout) {
        for (int i = 0; i < minNecessary.length; i++) {

            if (minNecessary[i] != null) {
                ShortcutRegistration reg = Shortcuts.addShortcutListener(layout, () -> {
                    // Notification.show("This was a non-mapped hotkey.");
                }, minNecessary[i]);
                reg.setEventPropagationAllowed(false);
                reg.setBrowserDefaultAllowed(false);
            }

        }
    }

}
