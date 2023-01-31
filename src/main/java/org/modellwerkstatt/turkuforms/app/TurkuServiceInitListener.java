package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.modellwerkstatt.turkuforms.infra.TurkuLog;

public class TurkuServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        TurkuLog.l("TurkuServiceInitListener.serviceInit() preInit");

        RouteConfiguration.forApplicationScope().setRoute("static", StaticView.class);

        event.getSource().addSessionInitListener(
                initEvent -> {

                    TurkuLog.l("TurkuServiceInitListener.sessionInit() ");
                    RouteConfiguration.forSessionScope().setRoute("view", TestView.class);
                    RouteConfiguration.forSessionScope().setRoute("login", LoginView.class);
                }

                );

        event.getSource().addUIInitListener(
                initEvent -> TurkuLog.l("TurkuServiceInitListener.uiInit() "));



    }

}
