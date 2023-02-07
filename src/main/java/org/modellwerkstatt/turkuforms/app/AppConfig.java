package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;


public class AppConfig implements AppShellConfigurator {


    @Override
    public void configurePage(AppShellSettings settings) {
        Turku.l("AppConfig . configurePage");
    }
}
