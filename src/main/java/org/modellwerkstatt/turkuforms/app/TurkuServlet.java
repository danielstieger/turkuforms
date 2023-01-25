package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.server.VaadinServlet;
import org.modellwerkstatt.turkuforms.infra.TurkuLog;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TurkuServlet extends VaadinServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        TurkuLog.clearAndDelete();

        super.init(servletConfig);

    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.service(request, response);

        boolean isVaadinHeartBeat = request.getContentLength() == 0;
        /* Vaadin heartbeats touch the session, therefore extending the TTL. Thus,
         * setting the session-timeout to e.g. 5 min and the heartberat to 1 min let
         * tomcat close the session, if a browser window is closed unexpectedly after 5 mins.
         */
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
