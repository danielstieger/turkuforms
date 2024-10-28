package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.auth.ExtAuthProvider;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.TurkuApplicationController;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.util.List;

import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.HOME_REDIRECT_PREFIX_LABEL;
import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.OK_HOKTEY;


public class OAuthLandingPage extends HorizontalLayout implements BeforeEnterObserver, HasDynamicTitle {
    public static String LOGIN_NOT_POSSIBLE = "Login not possible - ";
    public static String SESSION_PARAMS_ATTR = "OAuthLandingTempParams";
    private String title;
    private ExtAuthProvider provider;

    public OAuthLandingPage() {
        // default, not content in landing page
        // registered as / and /login
        setSizeFull();
    }



    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        NavigationUtil.setSessionUsername("OAuthLandingPage");

        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        ITurkuAppFactory factory = servlet.getUiFactory();
        IMoLdapService ldapService = factory.getLdapServiceIfPresent();

        title = servlet.getAppNameVersion();
        ParamInfo paramInfo = new ParamInfo(event.getLocation().getQueryParameters());
        String naviPath = "/" + event.getLocation().getPath();

        List<ExtAuthProvider> allProviders = servlet.getUiFactory().getAllExtAuthProviders();
        if (allProviders.size() != 1) {
            throw new RuntimeException("OAuthLandingPage() supports just one provider, but we have " + allProviders.size() + " here.");
        } else {
            provider = allProviders.get(0);
        }





        boolean otherCrtlPresent = TurkuApplicationController.hasOtherControllersInSession(vaadinSession);
        Turku.l("OAuthLandingPage.beforeEnter() naviPath " + naviPath + " oc=" + otherCrtlPresent + " al="+paramInfo.wasActiveLogout());

        if (factory.isSingleAppInstanceMode() && otherCrtlPresent) {
            Turku.l("IPAuthLandingPage.beforeEnter() in singleapp instance mode and other controllers present? "+ otherCrtlPresent);

            setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), "Start",
                    factory.getSystemLabel(-1, MoWareTranslations.Key.APPLICATION_RUNNING_IN_BROWSER), () -> {

                TurkuApplicationController.shutdownOtherControllersInSession(vaadinSession);
                // enqueue
                UI.getCurrent().access(() -> UI.getCurrent().navigate(TurkuServlet.LOGIN_ROUTE));
            }));

            return;
        }





        List<String> codes = event.getLocation().getQueryParameters().getParameters().get("code");
        List<String> states = event.getLocation().getQueryParameters().getParameters().get("state");
        String theState = states != null && states.size() == 1 ? states.get(0) : null;
        String theCode = codes != null && codes.size() == 1 ? codes.get(0) : null;
        String originalState = "" + vaadinSession.hashCode();


        if (TurkuServlet.LOGIN_ROUTE.equals(naviPath) && theState == null) {
            // this should work, even in case other controllers are present ..

            UserPrincipal userPrincipal = UserPrincipal.getUserPrincipal(vaadinSession);
            String userNameDefault = paramInfo.hasUsername() ? paramInfo.getUsername() : "";

            if (userPrincipal == null) {
                userPrincipal = IPAuthLandingPage.filUserAsParamHelper(paramInfo, factory);
            }

            // try auto login
            UserPrincipal.setUserPrincipal(vaadinSession, userPrincipal);
            UserEnvironmentInformation environment = new UserEnvironmentInformation();
            String msg = NavigationUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, userPrincipal.getUserName(), userPrincipal.getPassword());

            if (msg == null) {
                NavigationUtil.setUserEnvForUi(environment);
                NavigationUtil.ensureAppRoutPresentAndForward(event, paramInfo, false);
                return;
            }


            if (otherCrtlPresent) {
                // strange, this should happen actually. In case otherCrtl is present, we should also have a valid
                // userprincipal ..
                String notPossible = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_NOT_POSSIBLE);

                setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), null, notPossible, () -> {
                }));

            } else {
                // temporary save params
                vaadinSession.getSession().setAttribute(SESSION_PARAMS_ATTR, paramInfo);

                String url = provider.initialRedirect(originalState);
                UI.getCurrent().getPage().setLocation(url);
                return;
            }

        } else if (TurkuServlet.LOGIN_ROUTE.equals(naviPath) && theState != null) {
            String errorMsg = "";
            Exception exToReportOnPortJ = null;

            if (!originalState.equals(theState)) {
                errorMsg = LOGIN_NOT_POSSIBLE + "State for login procedure do not match.";

            }

            if ("".equals(errorMsg)) {
                try {
                    String credential = provider.retrieveUserWithAccessToken(theCode);

                    if (credential != null) {
                        UserPrincipal userPrincipal = new UserPrincipal(credential, "");
                        UserPrincipal.setUserPrincipal(vaadinSession, userPrincipal);
                        UserEnvironmentInformation environment = new UserEnvironmentInformation();
                        String msg = NavigationUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, userPrincipal.getUserName(), userPrincipal.getPassword());

                        if (msg == null) {
                            NavigationUtil.setUserEnvForUi(environment);

                            WrappedSession ws = vaadinSession.getSession();
                            if (ws.getAttribute(SESSION_PARAMS_ATTR) != null) {
                                paramInfo = (ParamInfo) ws.getAttribute(SESSION_PARAMS_ATTR);
                                ws.setAttribute(SESSION_PARAMS_ATTR, null);
                            }

                            ParamInfo finalParamInfo = paramInfo;
                            UI.getCurrent().access(() -> NavigationUtil.ensureAppRoutPresentAndForward(null, finalParamInfo, false));
                            return;

                        } else {
                            errorMsg = msg;
                        }

                    } else {
                        errorMsg = LOGIN_NOT_POSSIBLE + "Could not retrieve your credentials.";

                    }



                } catch (Exception ex) {
                    errorMsg = LOGIN_NOT_POSSIBLE + ex.getMessage();
                    exToReportOnPortJ = ex;

                }
            }

            Turku.l("OAuthLandingPage - " + errorMsg);
            servlet.logOnPortJTrace(OAuthLandingPage.class.getName(), factory.getRemoteAddr(), errorMsg, exToReportOnPortJ);
            setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), HOME_REDIRECT_PREFIX_LABEL, errorMsg, () -> {
                UI.getCurrent().navigate(TurkuServlet.LOGIN_ROUTE);
            }));



        } else if (TurkuServlet.LOGOUT_ROUTE.equals(naviPath) || paramInfo.wasActiveLogout()) {

            String buttonName;
            String message = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGOUT_SUCCESS);

            if (otherCrtlPresent) {
                message += " " + factory.getSystemLabel(-1, MoWareTranslations.Key.LOGOUT_OTHERS_OPEN);
                buttonName = factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.RELOGIN_BUTTON), OK_HOKTEY);

            } else {
                buttonName = factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY);
            }

            setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), buttonName, message, () -> {
                    UI.getCurrent().navigate(TurkuServlet.LOGIN_ROUTE);
            }));

        } else {
            throw new RuntimeException("This can not happen. navipath is '" + naviPath + "'");

        }
    }

    @Override
    public String getPageTitle() {
        return title;
    }

    private void setAsRoot(Component c) {
        this.removeAll();
        this.add(c);
    }
}
