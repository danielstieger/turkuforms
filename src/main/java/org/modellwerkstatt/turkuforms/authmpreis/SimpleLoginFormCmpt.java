package org.modellwerkstatt.turkuforms.authmpreis;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.turkuforms.infra.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.infra.TurkuServlet;
import org.modellwerkstatt.turkuforms.auth.ParamInfo;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.infra.MPreisAppConfig.OK_HOKTEY;

public class SimpleLoginFormCmpt extends HorizontalLayout {
    protected ParamInfo paramInfo;

    protected VerticalLayout innerLayout;
    protected H1 appName;
    protected Div messageDiv;
    protected Button loginButton;
    protected TextField userNameField;
    protected PasswordField passwordField;
    protected Span loginIdentityImage;

    protected String userName = "";
    protected String password = "";

    protected OnLogin onLoginCallback;


    public SimpleLoginFormCmpt(OnLogin onLogin) {
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        ITurkuAppFactory factory = servlet.getUiFactory();

        onLoginCallback = onLogin;

        loginIdentityImage = new Span();
        loginIdentityImage.addClassName("DefaultLoginLogo");

        appName = new H1(servlet.getAppNameVersion());

        messageDiv = new Div();
        messageDiv.addClassName("TurkuErrorDiv");
        messageDiv.addClassName("DefaultLoginContentWidth");

        userNameField = new TextField(factory.getSystemLabel(-1, MoWareTranslations.Key.USERNAME));
        userNameField.setAutoselect(true);
        userNameField.setAutofocus(true);
        userNameField.setRequired(true);
        userNameField.addClassName("DefaultLoginContentWidth");
        userNameField.setPlaceholder(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_EXTENDED_USERNAME));
        userNameField.setValueChangeMode(ValueChangeMode.LAZY);
        Peculiar.focusMoveEnterHk(false, userNameField, event -> {
            passwordField.setValue("");
            passwordField.focus();
        });

        passwordField = new PasswordField(factory.getSystemLabel(-1, MoWareTranslations.Key.PASSWORD));
        passwordField.addClassName("DefaultLoginContentWidth");
        passwordField.setValueChangeMode(ValueChangeMode.LAZY);
        Peculiar.focusMoveEnterHk(false, passwordField, event -> {
            processInput();
        });

        loginButton = new Button(factory.translateButtonLabel(factory.getSystemLabel(-1, MoWareTranslations.Key.LOGIN_BUTTON), OK_HOKTEY), event -> { processInput();});
        Peculiar.useButtonShortcutHk(loginButton, OK_HOKTEY);
        loginButton.addClassName("DefaultLoginContentWidth");

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
        innerLayout.setWidth("50%");


        Span largePicture = new Span();
        largePicture.addClassName("LargeLeftLoginPicture");
        add(largePicture);

        setSizeFull();
    }


    public void setCallback(OnLogin o) {
        onLoginCallback = o;
    }

    public void processInput() {

        userName = userNameField.getValue().trim();
        password = passwordField.getValue().trim();
        userNameField.setValue("");
        passwordField.setValue("");

        String msg = onLoginCallback.process(userName, password);
        if (msg != null) {
            messageDiv.setText(msg);
        }
    }

    public interface OnLogin {
        String process(String username, String password);
    }
}
