package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.ShortcutRegistration;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.modellwerkstatt.objectflow.runtime.OFXConsoleHelper;
import org.modellwerkstatt.turkuforms.util.Turku;

@Route("dialog-with-keyboard-shortcuts")
public class DialogWithKeyboardShortcuts extends VerticalLayout {

    public DialogWithKeyboardShortcuts() {
        add(new Button("Show dialog", event -> showDialog()));
    }

    private void showDialog() {
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        HorizontalLayout buttons = new HorizontalLayout(okButton, cancelButton);

        Dialog dialog = new Dialog(new Span("Dialog content goes here"), buttons);

        dialog.setCloseOnEsc(true);
        cancelButton.addClickListener(event -> dialog.close());

        okButton.addClickListener(
                event -> {
                    Notification.show("Accepted");
                    Turku.l("This is the close clickListener()");
                    dialog.close();
                }
        );
        /* okButton.addClickShortcut(Key.ENTER); */

        // Prevent click shortcut of the OK button from also triggering when
        // another button is focused
        ShortcutRegistration shortcutRegistration = Shortcuts
                .addShortcutListener(buttons, () -> {
                    Turku.l("This is the ShortcutRegistration ..... APPLAUSE ! ");
                    dialog.close();

                    }, Key.KEY_L, KeyModifier.CONTROL);

        shortcutRegistration.setEventPropagationAllowed(false);
        shortcutRegistration.setBrowserDefaultAllowed(false);

        dialog.open();
    }
}
