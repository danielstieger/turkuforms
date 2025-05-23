package org.modellwerkstatt.turkuforms.core;

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
        jmxRegistration.checkMarkAsForwardGracyFully();
    }

    @Override
    public UI findUI(VaadinRequest request) {
        // overwritten to measure with startRequest()
        UI theUi = super.findUI(request);

        if (theUi != null && !Workarounds.isHeartBeatRequest(request)) {

            ITurkuAppCrtlAccess crtl = Workarounds.getControllerFormUi(theUi);
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

//        Turku.l("TurkuServletService.requestEnd() " + isVaadinHeartBeat + " / " + currentUI);

        super.requestEnd(request, response, session);
        String onWebSocket = "";
        if (session != null && session.getSession() != null) {
            WrappedSession httpSession = session.getSession();

            String xForwardFor = request.getHeader("x-forwarded-for");
            if (xForwardFor != null) {
                httpSession.setAttribute("x-forwarded-for", xForwardFor);
            }

            String webSocket = request.getHeader("sec-fetch-mode");
            String atmosphereSocket = request.getHeader("X-Atmosphere-Transport");

            onWebSocket = "" + webSocket + " / " + atmosphereSocket;

            onWebSocket += " / " + Turku.cookiesToString(request);

            httpSession.setAttribute("websocket", onWebSocket);
        }


        if (!isVaadinHeartBeat && currentUI != null) {
            ITurkuAppCrtlAccess appCrtl = Workarounds.getControllerFormUi(currentUI);
            if (appCrtl != null) { // not login views etc.
                appCrtl.setuser_connectionInfoAddOn(onWebSocket);
                long startTime = appCrtl.requestDone();
                long reqTime = session.getLastRequestDuration();

                if (reqTime >= MPreisAppConfig.REQUEST_TIME_REPORTING_THRESHOLD) {
                    WrappedSession httpSession = session.getSession();
                    if (httpSession != null) {
                        String remoteAddr = "" + httpSession.getAttribute(TurkuApplicationController.REMOTE_SESSIONATTRIB);
                        String userName = "" + httpSession.getAttribute(TurkuApplicationController.USERNAME_SESSIONATTRIB);
                        jmxRegistration.getAppTelemetrics().servedRequest(remoteAddr, userName, "some turku interaction", startTime);
                    }
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
                        ITurkuAppCrtlAccess crtl = Workarounds.getControllerFormUi(uiToClose);
                        if (crtl != null) { crtl.beaconClose(session, uiToClose); }
                        // detach() might not be called imdtly upon ui.close()
                        uiToClose.close();
                    });
                }
            }
        }


    }
}
