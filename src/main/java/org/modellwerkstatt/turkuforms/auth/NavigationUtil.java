package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.core.LoginController;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.TurkuApp;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

@SuppressWarnings("unchecked")
public class NavigationUtil {
    public static final String WAS_ACTIVE_LOGOUT_PARAM = "wasActiveLogout";
    public static final String CMD_TO_START = "command";
    public static final String CMD_TO_START_PARAM = "param";
    public static final String USERNAME_PARAM = "username";
    public static final String OTHER_TABS_OPEN = "/static/othertabsopen.html";


    public static void ensureAppRoutPresentAndForward(BeforeEnterEvent evOrNull, ParamInfo paramInfo) {
        Turku.l("NavigationUtil.ensureAppRoutPresentAndForward() forwarding .... ");

        if (! RouteConfiguration.forSessionScope().getRoute("/:cmdName?").isPresent()) {
            // necessary, otherwise turkuApp will get login/logout as cmdName
            RouteConfiguration.forSessionScope().setRoute(TurkuServlet.LOGIN_ROUTE, Workarounds.getCurrentTurkuServlet().getAuthenticatorClass());
            RouteConfiguration.forSessionScope().setRoute(TurkuServlet.LOGOUT_ROUTE, Workarounds.getCurrentTurkuServlet().getAuthenticatorClass());

            RouteConfiguration.forSessionScope().setRoute("/:cmdName?", TurkuApp.class);
        }

        if (evOrNull != null) {
            // this will take over the params also ..
            evOrNull.forwardTo("/");

        } else {
            UI.getCurrent().navigate("/" + paramInfo.getParamsToForwardIfAny());

        }
    }

    public static String loginViaLoginCrtl(TurkuServlet servlet, VaadinSession vaadinSession, UserEnvironmentInformation info, String userName, String password) {
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();

        LoginController crtl = new LoginController(IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU, servlet.getGuessedServerName(), factory.getRemoteAddr());

        info.setDevice("", "", "");

        String msg = crtl.checkLoginPrepareUserEnv(userName, password, info, appUiModule, factory);
        return msg;
    }



    /* cookie handling not used right now, Dan Winter 24 */
    public static final int TWO_WEEKS = 14;
    public static final int ONE_WEEK = 7;
    public static final int TWENTYFOUR_HOURS_MILLIS = 86400;
    public static void setStartupInfoInBrowser(String value) {
        UI.getCurrent().getPage().executeJs("window.turku.setTurkuCookie", value, TWO_WEEKS);
    }

    public static void getStartupInfoInBrowser(SerializableConsumer myConsumer) {
        UI.getCurrent().getPage().executeJs("window.turku.getTurkuCookie").then(myConsumer);
    }
}
