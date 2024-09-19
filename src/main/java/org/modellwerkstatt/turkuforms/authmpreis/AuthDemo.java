package org.modellwerkstatt.turkuforms.authmpreis;

import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinRequest;
import org.modellwerkstatt.turkuforms.util.Turku;

import java.util.Arrays;

public class AuthDemo extends HorizontalLayout implements BeforeEnterObserver {

    public AuthDemo() {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {


        Turku.l("AuthDemo.beforeEnter() \n\n" + Turku.requestToString(VaadinRequest.getCurrent()) + "\n\n" + beforeEnterEvent.getLocation().getQueryParameters().getParameters());
        Turku.l("" + beforeEnterEvent.getLocation().getPath());
        Turku.l("" + beforeEnterEvent.getLocation().getSubLocation());

        if (beforeEnterEvent.getLocation().getPath().contains("access_token")) {
            add(new Label(Turku.requestToString(VaadinRequest.getCurrent()) + " " + beforeEnterEvent.getLocation().getQueryParameters()));

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
