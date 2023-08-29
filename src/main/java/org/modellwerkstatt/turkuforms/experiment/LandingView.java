package org.modellwerkstatt.turkuforms.experiment;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.turkuforms.app.TurkuApp;
import org.modellwerkstatt.turkuforms.auth.DefaultLoginWindow;
import org.modellwerkstatt.turkuforms.auth.ILoginWindow;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

@PreserveOnRefresh
public class LandingView extends Composite<Component> implements BeforeEnterObserver {

    public LandingView() {

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
    protected Component initContent() {
        Turku.l("FirstRouteView.initContent()");
        return defaultContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // This is not really beforeEnter. This is after the initContent()
        // was processed, so the view is already constructed and entered - kind of
        RouteConfiguration.forSessionScope().setRoute("app", TurkuApp.class);


        WrappedSession session = UI.getCurrent().getSession().getSession();

        UserPrincipal principal = UserPrincipal.getUserPrincipal(session);
        // if (principal == null) { event.forwardTo("login");



        event.forwardTo("app");

    }
}
