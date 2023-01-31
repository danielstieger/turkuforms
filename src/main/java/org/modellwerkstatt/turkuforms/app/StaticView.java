package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.turkuforms.infra.TurkuLog;


public class StaticView extends Div implements BeforeEnterObserver, BeforeLeaveObserver {

    private Paragraph mainP;

    public StaticView() {
        super();
        this.add(new Label("StaticView.class"));

        mainP = new Paragraph();
        this.add(mainP);
        mainP.setText("StaticView.constructor()");
        this.add(new Button("To View Button", e -> { UI.getCurrent().navigate("view");}));
        this.add(new Anchor("view", "to View Anchor"));

        TurkuLog.l(" * * * * * * * creating all the stuff!  *  * * * * * * *");
        TurkuLog.l("StaticView.constructor() UI is "+ UI.getCurrent());

        for (UI ui : VaadinSession.getCurrent().getUIs()) {
            TurkuLog.l("UI: " + ui);
        }
    }


    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        TurkuLog.l("StaticView.beforeLeave(): "+ event);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        TurkuLog.l("StaticView.beforeEnter(): "+ event);

    }


}
