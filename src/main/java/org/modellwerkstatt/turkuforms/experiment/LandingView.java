package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.RouteConfiguration;
import org.modellwerkstatt.turkuforms.app.TurkuApp;
import org.modellwerkstatt.turkuforms.util.Turku;

@PreserveOnRefresh
public class LandingView extends AppLayout implements BeforeEnterObserver {

    public LandingView() {
        Turku.l("FirstRouteView.initContent()");
        throw new RuntimeException("Hello WORLD");
    }

    public Component defaultContent() {
        VerticalLayout layout = new VerticalLayout();
        layout.setHeightFull();
        layout.setWidthFull();

        Label label = new Label("(c) modellwerkstatt.org");
        label.addClassName("ModwerkLabel");
        layout.add(label);
        layout.setAlignSelf(FlexComponent.Alignment.CENTER, label);
        return layout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // This is not really beforeEnter. This is after the initContent()
        // was processed, so the view is already constructed and entered - kind of
        RouteConfiguration.forSessionScope().setRoute("app", TurkuApp.class);
        event.forwardTo("app");

    }
}
