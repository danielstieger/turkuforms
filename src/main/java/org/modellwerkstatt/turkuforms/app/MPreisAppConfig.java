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
    public static final String MANUAL_THEME_URL_PATH = "artefact/bigm23/";
    public static final String MANUAL_THEME_LOGINIDENTITYIMG_WIDTH = "330px";
    public static final String MANUAL_THEME_LOGINIDENTITYIMG = "img/loginIdentityImg.png";

    public static final String OK_HOKTEY = "F12";
    public static final String NO_HOKTEY = "ESC";


    @Override
    public void configurePage(AppShellSettings settings) {
        Turku.l("AppConfig.configurePage()");
    }
}
