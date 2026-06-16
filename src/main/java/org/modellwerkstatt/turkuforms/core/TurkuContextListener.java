package org.modellwerkstatt.turkuforms.core;

import org.modellwerkstatt.dataux.runtime.core.Shutdown;
import org.modellwerkstatt.dataux.runtime.telemetrics.Dux;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TurkuContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Dux.hl("at least i am present.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Shutdown.shutdownAppAndContext(servletContextEvent.getServletContext());
    }
}
