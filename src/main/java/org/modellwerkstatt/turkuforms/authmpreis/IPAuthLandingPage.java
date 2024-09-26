package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.core.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.core.TurkuApplicationController;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.core.MPreisAppConfig.OK_HOKTEY;


public class IPAuthLandingPage extends HorizontalLayout implements BeforeEnterObserver, HasDynamicTitle {
    private String title;

    public IPAuthLandingPage() {
        // default, not content in landing page
        // registered as / and /login
        setSizeFull();
    }


    protected  UserPrincipal tryIpOrParamLogin(ParamInfo params, ITurkuAppFactory factory) {
        String name = factory.getRemoteAddr();

        if (params.hasUsername()) {
            name = params.getUsername();
            if (name.length() == 4 && Character.isDigit(name.charAt(0)) && Character.isDigit(name.charAt(1))
                    && Character.isDigit(name.charAt(2)) && Character.isDigit(name.charAt(3))) {
                // okay for mpreis, seems to be fil num
            } else {
                name = "xxxxxxxxx";
            }


        }
        return new UserPrincipal(name, "");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        ITurkuAppFactory factory = servlet.getUiFactory();
        IMoLdapService ldapService = factory.getLdapServiceIfPresent();

        title = servlet.getAppNameVersion();
        ParamInfo paramInfo = new ParamInfo(event.getLocation().getQueryParameters());
        String naviPath = "/" + event.getLocation().getPath();


        boolean otherCrtlPresent = TurkuApplicationController.hasOtherControllersInSession(vaadinSession);

        Turku.l("IPAuthLandingPage.beforeEnter() naviPath " + naviPath + " oc=" + otherCrtlPresent + " al="+paramInfo.wasActiveLogout());

        if (factory.isSingleAppInstanceMode() && otherCrtlPresent) {
            Turku.l("IPAuthLandingPage.beforeEnter() in singleapp instance mode and other controllers present? "+ otherCrtlPresent);

            setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), "Start",
                    factory.getSystemLabel(-1, MoWareTranslations.Key.APPLICATION_RUNNING_IN_BROWSER), () -> {

                TurkuApplicationController.shutdownOtherControllersInSession(vaadinSession);
                UI.getCurrent().navigate(TurkuServlet.LOGIN_ROUTE);
            }));

            return;
        }






        if (TurkuServlet.LOGIN_ROUTE.equals(naviPath)) {
            // this should work, even in case other controllers are present ..

            UserPrincipal userPrincipal = UserPrincipal.getUserPrincipal(vaadinSession);
            String userNameDefault = paramInfo.hasUsername() ? paramInfo.getUsername() : "";

            if (userPrincipal == null) {
                userPrincipal = tryIpOrParamLogin(paramInfo, factory);
            }


            boolean loginDone = false;
            if (userPrincipal != null) {
                // try auto login
                UserPrincipal.setUserPrincipal(vaadinSession, userPrincipal);
                UserEnvironmentInformation environment = new UserEnvironmentInformation();
                String msg = NavigationUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, userPrincipal.getUserName(), userPrincipal.getPassword());

                if (msg == null) {
                    loginDone = true;
                    NavigationUtil.setUserEnvForUi(environment);
                    NavigationUtil.ensureAppRoutPresentAndForward(event, paramInfo, false);
                }

            }

            if (loginDone) {
                // no action needed here.

            } else if (otherCrtlPresent) {
                // strange, this should happen actually. In case otherCrtl is present, we should also have a valid
                // userprincipal ..
                String notPossible = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_NOT_POSSIBLE);

                setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), null, notPossible, () -> {
                }));

            } else {

                setAsRoot(new SimpleLoginFormCmpt(userNameDefault, (username, password) -> {

                    if (ldapService == null) {
                        String message = "INTERNAL ERROR - NO LDAP SERVICE CONFIGURED! " + factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_FAILED);
                        return message;
                    }

                    boolean thisAuthenticated = ldapService.authenticateUser(username, password);

                    if (!thisAuthenticated) {
                        String failed = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_FAILED);
                        return failed;

                    } else {
                        UserPrincipal newPrinci = new UserPrincipal(username, password);
                        UserPrincipal.setUserPrincipal(vaadinSession, newPrinci);

                        UserEnvironmentInformation ldapUserEnv = new UserEnvironmentInformation();
                        String viaLoginCrtl = NavigationUtil.loginViaLoginCrtl(servlet, vaadinSession, ldapUserEnv, newPrinci.getUserName(), newPrinci.getPassword());


                        boolean multiCrtlsAfterLogin = TurkuApplicationController.hasOtherControllersInSession(vaadinSession);

                        Turku.l("IPAuthLandingPage.login with ldap multiCrtlsAfterLogin=" + multiCrtlsAfterLogin);

                        if (factory.isSingleAppInstanceMode() && multiCrtlsAfterLogin) {
                            Turku.l("IPAuthLandingPage.login with ldap() in singleapp instance mode! and other controllers present? "+ otherCrtlPresent);

                            setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), "Start",
                                    factory.getSystemLabel(-1, MoWareTranslations.Key.APPLICATION_RUNNING_IN_BROWSER), () -> {

                                TurkuApplicationController.shutdownOtherControllersInSession(vaadinSession);
                                UI.getCurrent().navigate(TurkuServlet.LOGIN_ROUTE);
                            }));
                            return null;

                        } else if (viaLoginCrtl == null) {
                            NavigationUtil.setUserEnvForUi(ldapUserEnv);
                            NavigationUtil.ensureAppRoutPresentAndForward(null, paramInfo, false);
                            return null;

                        } else {
                            return viaLoginCrtl;
                        }
                    }
                }));
            }

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
