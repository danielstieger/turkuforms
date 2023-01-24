package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.server.VaadinServlet;
import org.modellwerkstatt.turkuforms.infra.TurkuLog;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class TurkuServlet extends VaadinServlet {

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        TurkuLog.l("TurkuServlet.init()");
        super.init(servletConfig);
    }

    @Override
    public void destroy() {
        TurkuLog.l("TurkuServlet.init()");
        super.destroy();
    }
}
