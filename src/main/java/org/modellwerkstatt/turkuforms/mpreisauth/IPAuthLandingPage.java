package org.modellwerkstatt.turkuforms.mpreisauth;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.NavigationUtil;
import org.modellwerkstatt.turkuforms.util.ParamInfo;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.OK_HOKTEY;


public class IPAuthLandingPage extends HorizontalLayout implements BeforeEnterObserver {

    public IPAuthLandingPage() {
        // default, not content in landing page
        // registered as / and /login
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();
        ITurkuAppFactory factory = servlet.getUiFactory();


        ParamInfo paramInfo = new ParamInfo(event.getLocation().getQueryParameters());
        String naviPath = event.getLocation().getPath();


        if ("logout".equals(naviPath)) {
            String buttonName = factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY);
            String message = factory.getSystemLabel(-1, MoWareTranslations.Key.LOGOUT_SUCCESS);

            add(new SimpleMessageCmpt(servlet.getAppNameVersion(), buttonName, message, paramInfo));

        } else if ("login".equals(naviPath)) {


        } else {
            UserPrincipal userPrincipal = UserPrincipal.getUserPrincipal(vaadinSession);
            if (userPrincipal == null) {
                userPrincipal = new UserPrincipal(vaadinSession.getBrowser().getAddress(), "");
            }

            UserEnvironmentInformation environment = new UserEnvironmentInformation();
            String msg = NavigationUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, userPrincipal.getUserName(), userPrincipal.getPassword());

            if (msg == null) {
                // ok, access to app given..
                Workarounds.setUserEnvForUi(environment);
                NavigationUtil.ensureAppRoutPresentAndForward(event, paramInfo);

            } else {
                String buttonName = factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY);

                add(new SimpleMessageCmpt(servlet.getAppNameVersion(), buttonName, msg, paramInfo));
            }

        }
    }
}
