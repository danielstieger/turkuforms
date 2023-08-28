package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.*;
import org.modellwerkstatt.turkuforms.util.Turku;

@PreserveOnRefresh
public class LoginView extends Div implements BeforeEnterObserver, BeforeLeaveObserver {

    private Paragraph mainP;

    public LoginView() {
        super();
        this.add(new Label("LoginView.class"));

        mainP = new Paragraph();
        this.add(mainP);
        mainP.setText("LoginView.constructor()");

        Turku.l("LoginView.constructor() ");
        for (UI ui : UI.getCurrent().getSession().getUIs()) {
            Turku.l("> " + ui);
        }
    }


    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        Turku.l("LoginView.beforeLeave(): "+ event);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Turku.l("LoginView.beforeEnter(): "+ event);
    }


}
