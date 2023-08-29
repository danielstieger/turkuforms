package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.TurkuApp;
import org.modellwerkstatt.turkuforms.util.Workarounds;

public class SimpleIPAuthenticator extends VerticalLayout implements BeforeEnterObserver {

    public SimpleIPAuthenticator() {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        // This is not really beforeEnter. This is after the initContent()
        // was processed, so the view is already constructed and entered - kind of



        WrappedSession session = UI.getCurrent().getSession().getSession();

        UserPrincipal principal = UserPrincipal.getUserPrincipal(session);
        // if (principal == null) { event.forwardTo("login");

        // no double registration for route !
        RouteConfiguration.forSessionScope().setRoute("app", TurkuApp.class);
        Workarounds.setUserEnvForUi(new UserEnvironmentInformation());
        beforeEnterEvent.forwardTo("app");
    }
}
