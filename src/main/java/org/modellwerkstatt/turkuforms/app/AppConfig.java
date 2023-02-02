package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Inline;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import org.modellwerkstatt.turkuforms.infra.TurkuLog;



public class AppConfig implements AppShellConfigurator {


    @Override
    public void configurePage(AppShellSettings settings) {
        TurkuLog.l("AppConfig . configurePage");
    }
}
