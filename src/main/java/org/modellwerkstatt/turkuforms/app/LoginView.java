package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import org.modellwerkstatt.turkuforms.infra.TurkuLog;

public class LoginView extends Div implements BeforeEnterObserver, BeforeLeaveObserver {

    private Paragraph mainP;

    public LoginView() {
        super();
        this.add(new Label("LoginView.class"));

        mainP = new Paragraph();
        this.add(mainP);
        mainP.setText("LoginView.constructor()");
    }


    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        TurkuLog.l("LoginView.beforeLeave(): "+ event);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        TurkuLog.l("LoginView.beforeEnter(): "+ event);

    }


}
