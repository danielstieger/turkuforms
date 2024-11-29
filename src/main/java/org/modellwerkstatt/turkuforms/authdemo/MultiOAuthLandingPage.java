package org.modellwerkstatt.turkuforms.authdemo;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.auth.ExtAuthProvider;
import org.modellwerkstatt.turkuforms.auth.NavigationUtil;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.auth.UserPrincipal;
import org.modellwerkstatt.turkuforms.authmpreis.SimpleMessageCmpt;
import org.modellwerkstatt.turkuforms.core.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Turku;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import java.io.IOException;
import java.util.List;

public class MultiOAuthLandingPage extends SimpleMessageCmpt implements BeforeEnterObserver {
    private List<ExtAuthProvider> providers;

    public MultiOAuthLandingPage() {
        super();
        setSizeFull();


    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {


        Turku.l("AuthDemo.beforeEnter()  on session " + VaadinSession.getCurrent().hashCode());
        VaadinSession session = VaadinSession.getCurrent();
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        providers = servlet.getUiFactory().getAllExtAuthProviders();

        ParamInfo paramInfo = new ParamInfo(beforeEnterEvent.getLocation().getQueryParameters());

        List<String> codes = beforeEnterEvent.getLocation().getQueryParameters().getParameters().get("code");
        List<String> states = beforeEnterEvent.getLocation().getQueryParameters().getParameters().get("state");
        String theState = null;
        String theCode = null;
        String errorMessage = "";
        String originalState = "" + session.hashCode();
        ExtAuthProvider providerToUse = null;


        if (codes != null && states != null) {
            theState = states.get(0);
            theCode = codes.get(0);

            String[] splitted = theState.split("_");
            if (splitted.length == 2) {
                providerToUse = providers.stream().filter(extAuthProvider -> extAuthProvider.getAuthProviderName().equals(splitted[0])).findFirst().orElse(null);
                theState = splitted[1];

            } else {
                theState = null;

            }

        }

        if (theCode != null && originalState.equals(theState) && providerToUse != null) {
            String email = null;
            try {
                email = providerToUse.retrieveUserWithAccessToken(theCode);

                if (email == null){
                    errorMessage = "Problems while retrieving email from your account. We can not log you on.";

                } else {

                    UserPrincipal userPrincipal = new UserPrincipal(email, "");
                    UserPrincipal.setUserPrincipal(session, userPrincipal);
                    UserEnvironmentInformation environment = new UserEnvironmentInformation();
                    String msg = NavigationUtil.loginViaLoginCrtl(servlet, session, environment, userPrincipal.getUserName(), userPrincipal.getPassword());

                    if (msg == null) {
                        NavigationUtil.setUserEnvForUi(environment);
                        NavigationUtil.ensureAppRoutPresentAndForward(null, paramInfo, true);

                    }

                }

            } catch (IOException ex) {
                errorMessage = ex.getMessage();
            }

        } else if (theCode != null) {
            // code does not fit ..
            errorMessage = "Problems with the state while checking details with google. ";

        }


        if (theCode == null || !"".equals(errorMessage)) {
            setAppNameMsg(servlet.getAppNameVersion(), errorMessage);

            providers.forEach(extAuthProvider ->
                    addButton(extAuthProvider.getAuthProviderName(), () -> {

                                String url = extAuthProvider.initialRedirect(extAuthProvider.getAuthProviderName() + "_" + originalState);
                                UI.getCurrent().getPage().setLocation(url);

                            }
                    ));
        }
    }





}
