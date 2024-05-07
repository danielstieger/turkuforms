package org.modellwerkstatt.turkuforms.sditech;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public class LoginTestView extends VerticalLayout implements BeforeEnterObserver {
    public String rerouteTo = null;

    public LoginTestView() {
        Button loginBtn = new Button("Login" ,  buttonClickEvent -> {

            SdiAppCrtl crtl = new SdiAppCrtl("Fil 6842");
            crtl.authenticated = true;
            Hacks.setCrtl(crtl);

            UI.getCurrent().navigate(rerouteTo);
        });

        add(new Label("LOGIN Form"));
        add(loginBtn);
    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("reroute")) {
            rerouteTo = beforeEnterEvent.getLocation().getQueryParameters().getParameters().get("reroute").get(0);
        }
    }
}
