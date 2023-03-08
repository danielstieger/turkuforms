package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.server.*;
import org.modellwerkstatt.dataux.runtime.core.ApplicationController;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;
import org.springframework.stereotype.Component;

public class TurkuServiceInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        Turku.l("TurkuServiceInitListener.serviceInit() installing handlers");

        event.getSource().addSessionDestroyListener(destroyEvent -> {

           VaadinSession session = destroyEvent.getSession();
           WrappedSession wrappedSession = session.getSession();

           Turku.l("TurkuServiceInitListener.sessionDestroyed() " + Turku.sessionToString(wrappedSession));

           ApplicationController crtl = Workarounds.getAppCrtlFromSession(wrappedSession);

           if (crtl != null && !crtl.inShutdownMode()) {
               Turku.l("TurkuServiceInitListener.sessionDestroyed() The crtl was shutdown by session destroy listener.");
               crtl.internal_immediatelyShutdown();
           }
        });

    }

}
