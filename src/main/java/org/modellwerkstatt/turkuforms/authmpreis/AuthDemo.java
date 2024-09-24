package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.turkuforms.auth.GoogleOAuth2;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthDemo extends HorizontalLayout implements BeforeEnterObserver {
    private GoogleOAuth2 auth = new GoogleOAuth2();

    public AuthDemo() {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {


        Turku.l("AuthDemo.beforeEnter()  on session " + VaadinSession.getCurrent().hashCode());
        VaadinSession session = VaadinSession.getCurrent();
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

            try {
                String token = auth.retrievAccessToken(theCode);

                add(new Label("" + token));


            } catch (IOException e) {
                e.printStackTrace();
                errorMessage = "IoException - " + e.getMessage() + ". ";

            }



        } else if (theCode != null) {
            // code does not fit ..
            errorMessage = "Problems with the state while checking details with google. ";

        }


        if (theCode == null || !"".equals(errorMessage)) {

            add(new SimpleMessageCmpt("Google Authentication", "Go", errorMessage + "Authenticated with google.", () -> {

                String url = auth.initialRedirect(originalState);
                UI.getCurrent().getPage().setLocation(url);

            }));
        }
    }



}
