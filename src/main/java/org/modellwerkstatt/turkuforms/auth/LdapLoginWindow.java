package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.IMoLdapService;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.app.TurkuApp;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.ParamInfo;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

public class LdapLoginWindow extends DefaultLoginWindow implements BeforeEnterObserver {
    protected ParamInfo paramInfo;

    public LdapLoginWindow() {
        super();

        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        ITurkuAppFactory uiFactory = servlet.getUiFactory();
        IMoLdapService ldapService = uiFactory.getLdapServiceIfPresent();

        if (ldapService == null) {
            throw new RuntimeException("LdapLoginWindow needs an instance of the ldap service but found none.");
        }



    }

    @Override
    public void processInput() {
        prepareInput();

        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        ITurkuAppFactory uiFactory = servlet.getUiFactory();

        IMoLdapService ldapService = uiFactory.getLdapServiceIfPresent();
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        // (1) Authenticate via ldap
        Turku.l("LdapLoginWindow.processInput() ldapService is " + ldapService);
        boolean thisAuthenticated = ldapService.authenticateUser(userName, password);

        if (!thisAuthenticated) {
            String message = uiFactory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_FAILED);
            messageDiv.setText(message);

        } else {
            // (2) Authenticate via application
            UserEnvironmentInformation environment = new UserEnvironmentInformation();
            String message = AuthUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, userName, password);

            if (message == null) {
                AuthUtil.removeLoginRoute();

                UserPrincipal userPrincipal = new UserPrincipal(userName, password);
                UserPrincipal.setUserPrincipal(vaadinSession, userPrincipal);
                Workarounds.setUserEnvForUi(environment);

                AuthUtil.ensureAppRoutPresentAndForward(null, paramInfo);

            } else {
                messageDiv.setText(message);
            }
        }


    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        UserPrincipal userPrincipal = UserPrincipal.getUserPrincipal(vaadinSession);
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();

        paramInfo = new ParamInfo(beforeEnterEvent.getLocation().getQueryParameters());

        if (userPrincipal != null) {
            UserEnvironmentInformation environment = new UserEnvironmentInformation();
            String message = AuthUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, userName, password);

            if (message == null) {
                Workarounds.setUserEnvForUi(environment);
                AuthUtil.ensureAppRoutPresentAndForward(null, paramInfo);

            } else {
                messageDiv.setText(message);
            }
        }

    }
}
