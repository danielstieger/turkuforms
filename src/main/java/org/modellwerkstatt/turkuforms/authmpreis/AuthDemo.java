package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.auth.GoogleOAuth2;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthDemo extends HorizontalLayout implements BeforeEnterObserver {
    private GoogleOAuth2 auth = new GoogleOAuth2();

    public AuthDemo() {
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {


        Turku.l("AuthDemo.beforeEnter()  on session " + VaadinSession.getCurrent().hashCode());
        VaadinSession session = VaadinSession.getCurrent();
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        ParamInfo paramInfo = new ParamInfo(beforeEnterEvent.getLocation().getQueryParameters());

        List<String> codes = beforeEnterEvent.getLocation().getQueryParameters().getParameters().get("code");
        List<String> states = beforeEnterEvent.getLocation().getQueryParameters().getParameters().get("state");
        String theState = null;
        String theCode = null;
        String errorMessage = "";
        String originalState = "" + session.hashCode();

        if (codes != null && states != null) {
            theState = states.get(0);
            theCode = codes.get(0);
        }



        if (theCode != null && originalState.equals(theState)) {
            String email = auth.retrieveUserWithAccessToken(theCode);

            if (email == null){
                errorMessage = "Problems while retrieving email from your google accound. We can not log you on.";

            } else {

                UserPrincipal userPrincipal = new UserPrincipal(email, "");
                UserPrincipal.setUserPrincipal(session, userPrincipal);
                UserEnvironmentInformation environment = new UserEnvironmentInformation();
                String msg = NavigationUtil.loginViaLoginCrtl(servlet, session, environment, userPrincipal.getUserName(), userPrincipal.getPassword());

                if (msg == null) {
                    NavigationUtil.setUserEnvForUi(environment);
                    NavigationUtil.ensureAppRoutPresentAndForward(beforeEnterEvent, paramInfo);

                }

            }



        } else if (theCode != null) {
            // code does not fit ..
            errorMessage = "Problems with the state while checking details with google. ";

        }


        if (theCode == null || !"".equals(errorMessage)) {

            add(new SimpleMessageCmpt(servlet.getAppNameVersion(), "Google Authentication", errorMessage, () -> {

                String url = auth.initialRedirect(originalState);
                UI.getCurrent().getPage().setLocation(url);

            }));
        }
    }





}
