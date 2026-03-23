package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.*;
import org.modellwerkstatt.dataux.runtime.telemetrics.Dux;

@PreserveOnRefresh
public class LoginView extends Div implements BeforeEnterObserver, BeforeLeaveObserver {

    private Paragraph mainP;

    public LoginView() {
        super();
        this.add(new Label("LoginView.class"));

        mainP = new Paragraph();
        this.add(mainP);
        mainP.setText("LoginView.constructor()");

        Dux.hl("Login view created.");
        for (UI ui : UI.getCurrent().getSession().getUIs()) {
            Dux.hl("Open UI: " + ui);
        }
    }


    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        Dux.hl("Before leave: " + event);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Dux.hl("Before enter: " + event);
    }


}
