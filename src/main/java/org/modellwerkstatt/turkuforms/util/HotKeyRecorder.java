package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.notification.Notification;

public class HotKeyRecorder {

    // Only single string keys .. : /
    private Key[] minNecessary = new Key[] {Key.F12, Key.F5};


    public HotKeyRecorder() {

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
