package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinServlet;
import org.modellwerkstatt.turkuforms.experiment.LoginView;
import org.modellwerkstatt.turkuforms.experiment.StaticView;
import org.modellwerkstatt.turkuforms.experiment.TestView;
import org.modellwerkstatt.turkuforms.views.TurkuLayout;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TurkuServlet extends VaadinServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        Turku.clearAndDelete();

        super.init(servletConfig);

    }

    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        Turku.l("Turkuservlet.servletInitializer() .... ");

        RouteConfiguration.forApplicationScope().setRoute("static", StaticView.class);
        RouteConfiguration.forApplicationScope().setRoute("view", TestView.class);
        RouteConfiguration.forApplicationScope().setRoute("login", LoginView.class);
        RouteConfiguration.forApplicationScope().setRoute("", TurkuLayout.class);



    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // No access here to UI.getCurrent()

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
