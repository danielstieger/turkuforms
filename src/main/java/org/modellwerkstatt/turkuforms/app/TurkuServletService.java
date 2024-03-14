package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import org.modellwerkstatt.dataux.runtime.telemetrics.AppJmxRegistration;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.io.IOException;

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

        /* can not easily determine TurkuAppCrtl from this method here
         * without traversing session and using vaadin internals. hooked in via
         * findUi below.
         */
    }

    @Override
    public UI findUI(VaadinRequest request) {
        UI theUi = super.findUI(request);

        if (theUi != null && !Workarounds.isHeartBeatRequest(request)) {

            TurkuApplicationController crtl = Workarounds.getControllerFormUi(theUi);
            if (crtl != null) {
                crtl.startRequest(request.hashCode());
            }
        }

        return theUi;
    }

    private String readUiId(VaadinRequest req){
        try {
            return req.getReader().readLine();
        } catch (IOException e) {
            // ignore?
        }
        return null;
    }

    @Override
    public void requestEnd(VaadinRequest request, VaadinResponse response, VaadinSession session) {

        boolean isVaadinHeartBeat = Workarounds.isHeartBeatRequest(request);
        boolean isBeacon = request.getPathInfo().equals("/beacon");

        UI currentUI = isVaadinHeartBeat || isBeacon ? null : UI.getCurrent();


        super.requestEnd(request, response, session);


        if (!isVaadinHeartBeat && currentUI != null) {
            TurkuApplicationController appCrtl = Workarounds.getControllerFormUi(currentUI);
            if (appCrtl != null) { // not login views etc.
                long startTime = appCrtl.requestDone();
                long reqTime = session.getLastRequestDuration();

                if (reqTime >= MPreisAppConfig.REQUEST_TIME_REPORTING_THRESHOLD) {
                    String remoteAddr = "" + session.getSession().getAttribute(TurkuApplicationController.REMOTE_SESSIONATTRIB);
                    String userName = "" + session.getSession().getAttribute(TurkuApplicationController.USERNAME_SESSIONATTRIB);
                    jmxRegistration.getAppTelemetrics().servedRequest(remoteAddr, userName, "some turku interaction", startTime);
                }
            }

        } else if (isBeacon) {
            String uiIdSt = readUiId(request);
            if (uiIdSt != null && session != null) {
                int uiId = Integer.parseInt(uiIdSt);
                UI uiToClose = session.getUIById(uiId);

                if (uiToClose != null) {
                    Turku.l("TurkuServletService.requestEnd() BEACON calling close on " + uiToClose + " now.");


                    uiToClose.access(() -> {
                        TurkuApplicationController crtl = Workarounds.getControllerFormUi(uiToClose);
                        if (crtl != null) { crtl.closeAppCrtlMissingHearbeatOrBeacon(session); }
                        // detach() might not be called imdtly upon ui.close()
                        uiToClose.close();
                    });
                }
            }
        }


    }
}
