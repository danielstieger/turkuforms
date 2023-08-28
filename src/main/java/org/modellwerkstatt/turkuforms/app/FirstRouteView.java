package org.modellwerkstatt.turkuforms.app;

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
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

@PreserveOnRefresh
public class FirstRouteView extends Composite<Component> implements BeforeEnterObserver {

    public FirstRouteView() {
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
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();

        return defaultContent();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        WrappedSession session = UI.getCurrent().getSession().getSession();
        TurkuApplicationController crtl = Peculiar.getAppCrtlFromSession(session);
        boolean loggedIn = crtl != null;

        Turku.l("FirstRouteView.beforeEnter():" + event.getLocation().getSegments());
        Turku.l("FirstRouteView.beforeEnter(): already logged in " + loggedIn);
    }
}
