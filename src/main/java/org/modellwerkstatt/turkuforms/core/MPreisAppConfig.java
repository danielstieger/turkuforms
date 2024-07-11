package org.modellwerkstatt.turkuforms.core;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import org.modellwerkstatt.turkuforms.util.Turku;


@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
@Theme("bigm23")
@Push(transport = Transport.WEBSOCKET)
// When using WEBSOCKET, the service() of TurkuServlet get s no longer called

public class MPreisAppConfig implements AppShellConfigurator {
    public static final int REQUEST_TIME_REPORTING_THRESHOLD = 0;
    public static final String OK_HOKTEY = "F12";
    public static final String NO_HOKTEY = "ESC";
    public static final float DELEGATES_LINE_HIGHT_IN_REM = 1.125f; /* --lumo-font-size-m */
    public static final String HOME_REDIRECT_PREFIX_LABEL = "START";
    public static final int SESSION_TIMEOUT_FOR_APP_SEC = 60 * 60 * 5; /* prevent sleep modes kill app */


    @Override
    public void configurePage(AppShellSettings settings) {

        settings.setViewport("width=device-width, initial-scale=1");
        settings.addMetaTag("author", "modellwerkstatt.org");
        settings.setPageTitle("");

        settings.addFavIcon("icon", "static/favicon.png", "32x32");
        settings.addLink("shortcut icon", "static/favicon.png");

        Turku.l("AppConfig.configurePage()");
    }
}
