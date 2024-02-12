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
public class MPreisAppConfig implements AppShellConfigurator {

    public static final String OK_HOKTEY = "F12";
    public static final String NO_HOKTEY = "ESC";
    public static final float DELEGATES_LINE_HIGHT_IN_REM = 1.125f; /* --lumo-font-size-m */


    @Override
    public void configurePage(AppShellSettings settings) {

        settings.setViewport("width=device-width, initial-scale=1");
        settings.addMetaTag("author", "modellwerkstatt.org");
        settings.setPageTitle("");

        settings.addFavIcon("icon", "static/favicon-32x32.png", "32x32");
        settings.addLink("shortcut icon", "static/favicon-32x32.png");
        Turku.l("AppConfig.configurePage()");
    }
}
