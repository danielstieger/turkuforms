package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.modellwerkstatt.turkuforms.util.Turku;

//TODO: use @Component when working with spring boot
public class TurkuServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        Turku.l("TurkuServiceInitListener.serviceInit() preInit");

        event.getSource().addSessionInitListener(
                initEvent -> {
                    Turku.l("TurkuServiceInitListener.sessionInit() ");

                }

                );

        event.getSource().addUIInitListener(
                initEvent -> Turku.l("TurkuServiceInitListener.uiInit() "));



    }

}
