package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.turkuforms.app.Turku;

@PreserveOnRefresh
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

        Turku.l("StaticView.constructor() UI is "+ UI.getCurrent());
    }

    @ClientCallable
    public void turkuOnWindowFocusEvent() {
        Turku.l("StaticView.turkuOnWindowFocusEvent(): ");
        for (UI ui : VaadinSession.getCurrent().getUIs()) {
            Turku.l("> UI: " + ui + (UI.getCurrent() == ui ? " [THIS]" : ""));
        }
    }

    @ClientCallable
    public void turkuOnWindowUnload() {
        Turku.l("StaticView.turkuOnWindowUnload(): ");
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        Turku.l("StaticView.beforeLeave(): "+ event);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Turku.l("StaticView.beforeEnter(): "+ event);
        for (UI ui : VaadinSession.getCurrent().getUIs()) {
            Turku.l("> UI: " + ui + (UI.getCurrent() == ui ? " [THIS]" : ""));
        }

        UI.getCurrent().getPage().executeJs("turku.init($0);", this);
    }


}
