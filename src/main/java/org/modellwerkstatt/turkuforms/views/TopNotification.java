package org.modellwerkstatt.turkuforms.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

public class TopNotification extends HorizontalLayout {

    private final Button closeButton;
    private Text message;

    public TopNotification() {
        message = new Text("");
        Div msgDiv = new Div(message);
        msgDiv.addClassName("TabLockingMessage");

        closeButton = new Button(Workarounds.createIconWithCollection("close", false));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClassName("TabLockingButton");

        add(msgDiv, closeButton);
        Peculiar.shrinkSpace(this);
    }

    void setText(String msg){
        message.setText(msg);
    }

    void addCloseListener(ComponentEventListener<ClickEvent<Button>> listener){
        closeButton.addClickListener(listener);
    }

}
