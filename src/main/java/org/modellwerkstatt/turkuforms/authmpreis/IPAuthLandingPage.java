package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.app.TurkuApplicationController;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.OK_HOKTEY;


public class IPAuthLandingPage extends HorizontalLayout implements BeforeEnterObserver {

    public IPAuthLandingPage() {
        // default, not content in landing page
        // registered as / and /login
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        ITurkuAppFactory factory = servlet.getUiFactory();

        ParamInfo paramInfo = new ParamInfo(event.getLocation().getQueryParameters());
        String naviPath = event.getLocation().getPath();
        boolean otherCrtlPresent = vaadinSession.getSession().getAttributeNames()
                .stream().anyMatch(TurkuApplicationController::isTurkuControllerAttribute);

        Turku.l("IPAuthLandingPage.beforeEnter() naviPath " + naviPath + " oc=" + otherCrtlPresent + " al="+paramInfo.wasActiveLogout());
        if ("logout".equals(naviPath) || paramInfo.wasActiveLogout()) {
            // just navigating to /<servletname>/logout will not work, since
            // the app ui is still in the background - until missing heartbeat will destroy it

            String buttonName;
            String message = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGOUT_SUCCESS);

            if (otherCrtlPresent) {
                message += " " + factory.getSystemLabel(-1, MoWareTranslations.Key.LOGOUT_OTHERS_OPEN);
                buttonName = factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.RELOGIN_BUTTON), OK_HOKTEY);

            } else {
                buttonName = factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY);
            }

            setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), buttonName, message, () -> {
                // do not forward any params here, just a logout
                if (otherCrtlPresent) {
                    UI.getCurrent().navigate("/");
                } else {
                    UI.getCurrent().navigate("/login");
                }

            }));

        } else if ("login".equals(naviPath) && otherCrtlPresent) {
            String msg = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_NOT_POSSIBLE);

            setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), null, msg, () -> {
            }));


        } else if ("login".equals(naviPath)){
            setAsRoot(new SimpleLoginFormCmpt((username, password) -> {
                UserPrincipal userPrincipal = new UserPrincipal(username, password);
                UserPrincipal.setUserPrincipal(vaadinSession, userPrincipal);
                UI.getCurrent().navigate("/" + paramInfo.getParamsToForwardIfAny());
                return null;
            }));


        } else {
            UserPrincipal userPrincipal = UserPrincipal.getUserPrincipal(vaadinSession);
            if (userPrincipal == null) {
                userPrincipal = new UserPrincipal(vaadinSession.getBrowser().getAddress(), "");
                UserPrincipal.setUserPrincipal(vaadinSession, userPrincipal);
            }

            UserEnvironmentInformation environment = new UserEnvironmentInformation();
            String msg = NavigationUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, userPrincipal.getUserName(), userPrincipal.getPassword());

            if (msg == null) {
                Workarounds.setUserEnvForUi(environment);
                NavigationUtil.ensureAppRoutPresentAndForward(servlet.getAuthenticatorClass(), event, paramInfo);

            } else {
                String buttonName = factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY);

                setAsRoot(new SimpleMessageCmpt(servlet.getAppNameVersion(), buttonName, msg, () -> {
                    UI.getCurrent().navigate("/login" + paramInfo.getParamsToForwardIfAny());
                } ));
            }

        }
    }

    private void setAsRoot(Component c) {
        this.removeAll();
        this.add(c);
    }
}
