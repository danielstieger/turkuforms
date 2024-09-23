package org.modellwerkstatt.turkuforms.authmpreis;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthDemo extends HorizontalLayout implements BeforeEnterObserver {

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
        String originalCode = "" + session.hashCode();

        if (codes != null && states != null) {
            theState = states.get(0);
            theCode = codes.get(0);
        }



        if (theCode != null && originalCode.equals(theState)) {
            HttpTransport netTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new GsonFactory();
            GoogleTokenResponse token;

            try {
                token = new GoogleAuthorizationCodeTokenRequest(
                        netTransport,
                        jsonFactory,
                        "938810109726-3lo3a5ebkq8u8lqli5prjq3609bkkn6h.apps.googleusercontent.com",
                        "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",
                        theCode,
                        "http://localhost:8080/simpleone/login").execute();

                add(new Label(token.toPrettyString()));




            } catch (IOException e) {
                errorMessage = "IoException - " + e.getMessage() + ". ";

            }



        } else if (theCode != null) {
            // code does not fit ..
            errorMessage = "Problems with the state while checking details with google. ";

        }


        if (theCode == null || !"".equals(errorMessage)) {

            add(new SimpleMessageCmpt("Google Authentication", "Go", errorMessage + "Authenticated with google.", () -> {

                String url = new GoogleBrowserClientRequestUrl("938810109726-3lo3a5ebkq8u8lqli5prjq3609bkkn6h.apps.googleusercontent.com",
                        "http://localhost:8080/simpleone/login", Arrays.asList(
                        "https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile"))
                        .setState(originalCode).setResponseTypes(Arrays.asList("code")).build();

                UI.getCurrent().getPage().setLocation(url);

            }));
        }
    }



}
