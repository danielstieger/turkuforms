package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.app.TurkuApp;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.*;
import static org.modellwerkstatt.turkuforms.auth.SimpleIPAuthenticator.loginViaLoginCrtl;

@PreserveOnRefresh
public class DefaultLoginWindow extends HorizontalLayout {

    protected VerticalLayout innerLayout;
    protected H1 appName;
    protected Div messageDiv;
    protected Button loginButton;
    protected TextField userNameField;
    protected PasswordField passwordField;
    protected Image loginIdentityImage;

    protected String userName = "";
    protected String password = "";

    public DefaultLoginWindow() {
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();

        loginIdentityImage = new Image(MANUAL_THEME_URL_PATH + MANUAL_THEME_LOGINIDENTITYIMG, "Identity Image");
        appName = new H1(appUiModule.getShortAppName() + " " + appUiModule.getApplicationVersion());

        messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        messageDiv.setWidth(MANUAL_THEME_LOGINIDENTITYIMG_WIDTH);

        userNameField = new TextField(factory.getSystemLabel(-1, MoWareTranslations.Key.USERNAME));
        userNameField.setAutoselect(true);
        userNameField.setAutofocus(true);
        userNameField.setRequired(true);
        userNameField.setWidth(MANUAL_THEME_LOGINIDENTITYIMG_WIDTH);
        userNameField.setPlaceholder(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_EXTENDED_USERNAME));
        userNameField.setValueChangeMode(ValueChangeMode.LAZY);
        Peculiar.focusMoveEnterHk(false, userNameField, event -> {
            passwordField.setValue("");
            passwordField.focus();
        });

        passwordField = new PasswordField(factory.getSystemLabel(-1, MoWareTranslations.Key.PASSWORD));
        passwordField.setWidth(MANUAL_THEME_LOGINIDENTITYIMG_WIDTH);
        passwordField.setValueChangeMode(ValueChangeMode.LAZY);
        Peculiar.focusMoveEnterHk(false, passwordField, event -> {
            processInput();
        });

        loginButton = new Button(factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY), event -> { processInput();});
        Peculiar.useButtonShortcutHk(loginButton, OK_HOKTEY);
        loginButton.setWidth(MANUAL_THEME_LOGINIDENTITYIMG_WIDTH);

        innerLayout = new VerticalLayout();
        innerLayout.add(loginIdentityImage, appName, messageDiv, userNameField, passwordField, loginButton);

        innerLayout.setAlignSelf(Alignment.CENTER, loginIdentityImage);
        innerLayout.setAlignSelf(Alignment.CENTER, appName);
        innerLayout.setAlignSelf(Alignment.CENTER, messageDiv);
        innerLayout.setAlignSelf(Alignment.CENTER, userNameField);
        innerLayout.setAlignSelf(Alignment.CENTER, passwordField);
        innerLayout.setAlignSelf(Alignment.CENTER, loginButton);

        add(innerLayout);
        setAlignSelf(Alignment.CENTER, innerLayout);
        setHeightFull();
    }


    public void prepareInput() {
        userName = userNameField.getValue().trim();
        password = passwordField.getValue().trim();
        userNameField.setValue("");
        passwordField.setValue("");
    }

    public void processInput() {

        prepareInput();

        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        VaadinSession vaadinSession = VaadinSession.getCurrent();

        UserEnvironmentInformation environment = new UserEnvironmentInformation();
        String msg = loginViaLoginCrtl(servlet, vaadinSession, environment, userName, password);

        if (msg == null) {
            if (! RouteConfiguration.forSessionScope().getRoute("app").isPresent()) {
                RouteConfiguration.forSessionScope().setRoute("app", TurkuApp.class);
            }
            RouteConfiguration.forSessionScope().removeRoute("login");

            UserPrincipal userPrincipal = new UserPrincipal(userName, password);
            UserPrincipal.setUserPrincipal(vaadinSession, userPrincipal);
            Workarounds.setUserEnvForUi(environment);
            UI.getCurrent().navigate("app");

        } else {
            messageDiv.setText(msg);
        }

    }

}
