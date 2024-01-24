package org.modellwerkstatt.turkuforms.util;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.core.LoginController;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.app.TurkuApp;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;

public class NavigationUtil {

    public static void forwareToLogin(ParamInfo paramInfo) {
        UI.getCurrent().navigate("login" + paramInfo.getParamsToForwardIfAny());
    }

    public static String loginViaLoginCrtl(TurkuServlet servlet, VaadinSession vaadinSession, UserEnvironmentInformation info, String userName, String password) {
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();

        LoginController crtl = new LoginController(IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU, servlet.getGuessedServerName(), vaadinSession.getBrowser().getAddress());

        info.setDevice("TurkuDesk", "" + vaadinSession.getBrowser().getBrowserApplication(), "");

        String msg = crtl.checkLoginPrepareUserEnv(userName, password, info, appUiModule, factory);
        return msg;
    }

    public static void ensureAppRoutPresentAndForward(BeforeEnterEvent evOrNull, ParamInfo paramInfo) {
        if (! RouteConfiguration.forSessionScope().getRoute("/:cmdName?").isPresent()) {
            RouteConfiguration.forSessionScope().setRoute("/:cmdName?", TurkuApp.class);
        }

        if (evOrNull == null) {
            UI.getCurrent().navigate("/" + paramInfo.getParamsToForwardIfAny());

        } else {
            evOrNull.forwardTo("/" + paramInfo.getParamsToForwardIfAny());
        }
    }


    @Deprecated
    public static boolean isLogout(BeforeEnterEvent ev) {
        return ev.getLocation().getQueryParameters().getParameters().containsKey("logout");
    }

}
