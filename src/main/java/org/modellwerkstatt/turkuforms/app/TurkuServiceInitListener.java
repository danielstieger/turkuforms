package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.modellwerkstatt.turkuforms.infra.TurkuLog;

//TODO: use @Component when working with spring boot
public class TurkuServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        TurkuLog.l("TurkuServiceInitListener.serviceInit() preInit");

        event.getSource().addSessionInitListener(
                initEvent -> {
                    TurkuLog.l("TurkuServiceInitListener.sessionInit() ");

                }

                );

        event.getSource().addUIInitListener(
                initEvent -> TurkuLog.l("TurkuServiceInitListener.uiInit() "));



    }

}
