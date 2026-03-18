package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.core.LoginController;
import org.modellwerkstatt.dataux.runtime.genspecification.IGenAppUiModule;
import org.modellwerkstatt.objectflow.runtime.IOFXCoreReporter;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

@SuppressWarnings("unchecked")
public class NavigationUtil {
    public static final String WAS_ACTIVE_LOGOUT_PARAM = "wasActiveLogout";
    public static final String CMD_TO_START = "command";
    public static final String CMD_TO_START_PARAM = "param";
    public static final String USERNAME_PARAM = "username";
    public static final String REROUTE_TO = "reroute";
    public static final String OTHER_TABS_OPEN = "/static/othertabsopen.html";


    public static void ensureAppRoutPresentAndForward(BeforeEnterEvent evOrNull, ParamInfo paramInfo, boolean forceAbsolutNavi) {
        Turku.l("NavigationUtil.ensureAppRoutPresentAndForward() forwarding .... ");
        TurkuServlet theServlet = Workarounds.getCurrentTurkuServlet();

        if (! RouteConfiguration.forSessionScope().isRouteRegistered(theServlet.getTurkuAppImplClass())) {
            // necessary, otherwise turkuApp will get login/logout as cmdName
            RouteConfiguration.forSessionScope().setRoute(TurkuServlet.LOGIN_ROUTE, Workarounds.getCurrentTurkuServlet().getAuthenticatorClass());
            RouteConfiguration.forSessionScope().setRoute(TurkuServlet.LOGOUT_ROUTE, Workarounds.getCurrentTurkuServlet().getAuthenticatorClass());

            RouteConfiguration.forApplicationScope().removeRoute("/:path*");
            RouteConfiguration.forSessionScope().setRoute("/:path*", theServlet.getTurkuAppImplClass());
        }


        //
        // In the browser, the navigate() method triggers a location update and the
        // addition of a new history state entry, but doesn’t issue a full page reload.

        if (paramInfo.hasReroute()) {
            absolutNavi(paramInfo.getReroute());

        } else if (forceAbsolutNavi) {
            absolutNavi("/");

        } else if (evOrNull != null) {
            // this will take over the params also ..
            evOrNull.forwardTo("/");

        } else {
            UI.getCurrent().navigate("/" + paramInfo.getParamsToForwardIfAny());

        }
    }

    public static void absolutNavi(String path) {
        // vaadin bug with route prios? not forwarding with even.forwardTo()
        UI.getCurrent().getPage().setLocation(Workarounds.getCurrentTurkuServlet().getActualServletUrl() + path);
    }

    public static UserEnvironmentInformation getAndClearUserEnvFromUi() {
        VaadinSession current = VaadinSession.getCurrent();
        UserEnvironmentInformation env = (UserEnvironmentInformation) current.getAttribute("uiCurrentUserEnv");
        setUserEnvForUi(null);
        return env;
    }

    public static void setUserEnvForUi(UserEnvironmentInformation env) {
        VaadinSession current = VaadinSession.getCurrent();
        current.setAttribute("uiCurrentUserEnv", env);
    }

    public static String loginViaLoginCrtl(TurkuServlet servlet, VaadinSession vaadinSession, UserEnvironmentInformation info, String userName, String password) {
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();

        LoginController crtl = new LoginController(factory, IOFXCoreReporter.MoWarePlatform.MOWARE_TURKU, servlet.getGuessedServerName(), factory.getRemoteAddr());

        info.setDevice("", "", "");

        String msg = crtl.checkLoginPrepareUserEnv(userName, password, info, appUiModule);
        return msg;
    }
}
