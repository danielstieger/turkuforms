package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
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
import org.modellwerkstatt.turkuforms.util.ParamInfo;

import java.util.Optional;


@SuppressWarnings("unchecked")
public class AuthUtil {
    public static final int TWO_WEEKS = 14;
    public static final int ONE_WEEK = 7;
    public static final int TWENTYFOUR_HOURS_MILLIS = 86400;
    public static final String LOGOUT_POSTFIX = "?logout=1";


    public static String loginViaLoginCrtl(TurkuServlet servlet, VaadinSession vaadinSession, UserEnvironmentInformation info, String userName, String password) {
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();

        LoginController crtl = new LoginController(IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU, servlet.getGuessedServerName(), vaadinSession.getBrowser().getAddress());

        info.setDevice("WebDesktop", "" + vaadinSession.getBrowser().toString(), "");

        String msg = crtl.checkLoginPrepareUserEnv(userName, password, info, appUiModule, factory);
        return msg;
    }


    public static void removeLoginRoute() {
        RouteConfiguration.forSessionScope().removeRoute("login");
    }

    public static void forwareToLogin(ParamInfo paramInfo) {
        UI.getCurrent().navigate("login" + paramInfo.getParamsToForwardIfAny());
    }

    public static void ensureLoginPresent(Class<? extends Component> loginComponent) {
        // no double registration for route !
        if (! RouteConfiguration.forSessionScope().getRoute("login").isPresent()) {
            RouteConfiguration.forSessionScope().setRoute("login", loginComponent);
        }
    }

    public static void ensureAppRoutPresentAndForward(BeforeEnterEvent evOrNull, ParamInfo paramInfo) {
        if (! RouteConfiguration.forSessionScope().getRoute("main/:cmdName?").isPresent()) {
            RouteConfiguration.forSessionScope().setRoute("main/:cmdName?", TurkuApp.class);
        }

        if (evOrNull == null) {
            UI.getCurrent().navigate("main" + paramInfo.getParamsToForwardIfAny());

        } else {
            evOrNull.forwardTo("main" + paramInfo.getParamsToForwardIfAny());
        }
    }

    public static boolean isLogout(BeforeEnterEvent ev) {
        return ev.getLocation().getQueryParameters().getParameters().containsKey("logout");
    }


    public static void setStartupInfoInBrowser(String value) {
        UI.getCurrent().getPage().executeJs("window.turku.setTurkuCookie", value, TWO_WEEKS);
    }

    public static void getStartupInfoInBrowser(SerializableConsumer myConsumer) {
        UI.getCurrent().getPage().executeJs("window.turku.getTurkuCookie").then(myConsumer);
    }
}
