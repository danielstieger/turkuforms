package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.turkuforms.app.ITurkuFactory;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.Peculiar;

import static org.modellwerkstatt.turkuforms.app.AppConfig.*;

public class DefaultLoginWindow extends HorizontalLayout implements ILoginWindow {

    protected VerticalLayout innerLayout;
    protected H1 appName;
    protected Button loginButton;
    protected TextField userNameField;
    protected PasswordField passwordField;
    protected Image loginIdentityImage;

    protected String userName = "";
    protected String password = "";
    protected ILoginWindow.IAuthenticateUiCallback callback;

    public DefaultLoginWindow() {

    }

    public Component init(TurkuServlet servlet, ILoginWindow.IAuthenticateUiCallback theCallback) {
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuFactory factory = servlet.getUiFactory();

        loginIdentityImage = new Image(MANUAL_THEME_URL_PATH + "img/loginIdentityImg.png", "Identity Image");
        appName = new H1(appUiModule.getShortAppName() + " " + appUiModule.getApplicationVersion());

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
        innerLayout.add(loginIdentityImage, appName, userNameField, passwordField, loginButton);

        innerLayout.setAlignSelf(Alignment.CENTER, loginIdentityImage);
        innerLayout.setAlignSelf(Alignment.CENTER, appName);
        innerLayout.setAlignSelf(Alignment.CENTER, userNameField);
        innerLayout.setAlignSelf(Alignment.CENTER, passwordField);
        innerLayout.setAlignSelf(Alignment.CENTER, loginButton);

        add(innerLayout);
        setAlignSelf(Alignment.CENTER, innerLayout);
        setHeightFull();

        callback = theCallback;
        return this;
    }


    public void processInput() {
        userName = userNameField.getValue().trim();
        password = passwordField.getValue().trim();
        userNameField.setValue("");
        passwordField.setValue("");

        callback.authenticate(userName, password);
    }


    public void gcClean() {
        callback = null;
    }
}
