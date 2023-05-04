package org.modellwerkstatt.turkuforms.app;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import org.modellwerkstatt.turkuforms.util.Turku;


@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js") // yes works :)
@Theme("bigm23")
@Push
public class AppConfig implements AppShellConfigurator {

    @Override
    public void configurePage(AppShellSettings settings) {
        Turku.l("AppConfig.configurePage()");
    }
}
