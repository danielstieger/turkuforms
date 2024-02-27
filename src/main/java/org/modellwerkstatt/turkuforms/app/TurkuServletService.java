package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.vaadin.open.App;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

public class TurkuServletService extends VaadinServletService {
    AppJmxRegistration jmxRegistration;

    public TurkuServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration) {
        super(servlet, deploymentConfiguration);
    }

    public void setJmxRegistration(AppJmxRegistration jmxRegistration) {
        this.jmxRegistration = jmxRegistration;
    }

    @Override
    public void requestStart(VaadinRequest request, VaadinResponse response) {
        super.requestStart(request, response);

        boolean isVaadinHeartBeat = Workarounds.isHeartBeatRequest(request);
        if (!isVaadinHeartBeat) {
            WrappedSession session = request.getWrappedSession(false);
            if (session != null) {
                Turku.l("TurkuServletService.requestStart()");
                TurkuApplicationController appCrtl = Workarounds.getControllerFromRequest(request, session);
                Turku.l("TurkuServletService.requestStart() appCrtl = " + appCrtl);
                if (appCrtl != null) {
                    appCrtl.startRequest();
                }
            }
        }
    }

    @Override
    public void requestEnd(VaadinRequest request, VaadinResponse response, VaadinSession session) {


        boolean isVaadinHeartBeat = Workarounds.isHeartBeatRequest(request);
        UI currentUI = isVaadinHeartBeat ? null : UI.getCurrent();

        super.requestEnd(request, response, session);

        if (!isVaadinHeartBeat) {
            TurkuApplicationController appCrtl = Workarounds.getControllerFormUi(currentUI);
            if (appCrtl != null) { // not login views etc.
                long startTime = appCrtl.requestDone();
                long reqTime = session.getLastRequestDuration();

                if (reqTime >= MPreisAppConfig.REQUEST_TIME_REPORTING_THRESHOLD) {
                    String remoteAddr = "" + session.getSession().getAttribute(TurkuApplicationController.REMOTE_SESSIONATTRIB);
                    String userName = "" + session.getSession().getAttribute(TurkuApplicationController.USERNAME_SESSIONATTRIB);
                    jmxRegistration.getAppTelemetrics().servedRequest(remoteAddr, userName, "some vaadin interaction", startTime);
                }

                Turku.l("TurkuServletService.requestEnd() vaadin diff = " + reqTime + " ours = " + (System.currentTimeMillis() - startTime) + " - - - - - - - - - - - -");

            }


        }
    }
}
