package org.modellwerkstatt.turkuforms.auth;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.server.VaadinSession;
import org.modellwerkstatt.dataux.runtime.genspecifications.IGenAppUiModule;
import org.modellwerkstatt.dataux.runtime.utils.MoWareTranslations;
import org.modellwerkstatt.objectflow.runtime.UserEnvironmentInformation;
import org.modellwerkstatt.turkuforms.app.ITurkuAppFactory;
import org.modellwerkstatt.turkuforms.app.TurkuServlet;
import org.modellwerkstatt.turkuforms.util.ParamInfo;
import org.modellwerkstatt.turkuforms.util.Peculiar;
import org.modellwerkstatt.turkuforms.util.Workarounds;

import static org.modellwerkstatt.turkuforms.app.MPreisAppConfig.OK_HOKTEY;

@PreserveOnRefresh
public class DefaultLoginWindow extends HorizontalLayout implements BeforeEnterObserver {
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

    public DefaultLoginWindow() {
        TurkuServlet servlet = Workarounds.getCurrentTurkuServlet();
        IGenAppUiModule appUiModule = servlet.getAppBehaviour();
        ITurkuAppFactory factory = servlet.getUiFactory();

        loginIdentityImage = new Span();
        loginIdentityImage.addClassName("DefaultLoginLogo");

        appName = new H1(appUiModule.getShortAppName() + " " + appUiModule.getApplicationVersion());

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
        String msg = AuthUtil.loginViaLoginCrtl(servlet, vaadinSession, environment, userName, password);

        if (msg == null) {
            AuthUtil.removeLoginRoute();

            UserPrincipal userPrincipal = new UserPrincipal(userName, password);
            UserPrincipal.setUserPrincipal(vaadinSession, userPrincipal);
            Workarounds.setUserEnvForUi(environment);

            AuthUtil.ensureAppRoutPresentAndForward(null, paramInfo);

        } else {
            messageDiv.setText(msg);
        }

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        paramInfo = new ParamInfo(beforeEnterEvent.getLocation().getQueryParameters());
    }
}
