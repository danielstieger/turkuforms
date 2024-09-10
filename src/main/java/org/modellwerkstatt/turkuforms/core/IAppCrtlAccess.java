package org.modellwerkstatt.turkuforms.core;

import com.vaadin.flow.server.VaadinSession;

import java.util.Map;

public interface IAppCrtlAccess {

    public void startRequest(int requestHash);

    public long requestDone();

    public boolean sameHkInThisRequest(String newHk);

    public String setuser_connectionInfoAddOn(String onWebSocket);

    public void logAppTrace(String commandName, String sessId, String source, String desc, String param, Map<String, Object> additionalParamsOrNull);

    public void logFrmwrkProblem(String commandName, String sessId, String source, Throwable t, String desc);

    public void closeAppCrtlMissingHearbeatOrBeacon(VaadinSession session);
}
