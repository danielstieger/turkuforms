package org.modellwerkstatt.turkuforms.authmpreis;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinRequest;
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

        List<String> codes = beforeEnterEvent.getLocation().getQueryParameters().getParameters().get("code");
        List<String> states = beforeEnterEvent.getLocation().getQueryParameters().getParameters().get("state");
        Turku.l("                        codes " + (codes));

        if (codes != null && states != null) {
            Turku.l("                      " + codes.get(0) + " / " + states.get(0));

            HttpTransport netTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new GsonFactory();
            GoogleTokenResponse token;

            try {
                token = new GoogleAuthorizationCodeTokenRequest(
                        netTransport,
                        jsonFactory,
                        "938810109726-3lo3a5ebkq8u8lqli5prjq3609bkkn6h.apps.googleusercontent.com",
                        "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",
                        codes.get(0),
                        "http://localhost:8080/simpleone/login").execute();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Turku.l("The token is " + token);

            try {
                add(new Label(token.toPrettyString()));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {

            add(new SimpleMessageCmpt("TEST", "Go", "authenticate please", () -> {

                // logout ?

                String url = new GoogleBrowserClientRequestUrl("938810109726-3lo3a5ebkq8u8lqli5prjq3609bkkn6h.apps.googleusercontent.com",
                        "http://localhost:8080/simpleone/login", Arrays.asList(
                        "https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile"))
                        .setState("/profile").setResponseTypes(Arrays.asList("code")).build();


                UI.getCurrent().getPage().setLocation(url);

            }));
        }
    }



}
